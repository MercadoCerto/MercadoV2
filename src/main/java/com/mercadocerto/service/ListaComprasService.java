package com.mercadocerto.service;

import com.mercadocerto.dto.DTOs.*;
import com.mercadocerto.model.*;
import com.mercadocerto.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ListaComprasService {

    private final ListaComprasRepository listaRepo;
    private final ProdutoRepository      produtoRepo;
    private final PrecoRepository        precoRepo;
    private final MercadoRepository      mercadoRepo;

    public ListaComprasService(ListaComprasRepository listaRepo,
                               ProdutoRepository produtoRepo,
                               PrecoRepository precoRepo,
                               MercadoRepository mercadoRepo) {
        this.listaRepo   = listaRepo;
        this.produtoRepo = produtoRepo;
        this.precoRepo   = precoRepo;
        this.mercadoRepo = mercadoRepo;
    }

    public List<ListaCompras> listarPorUsuario(Integer idUsuario) {
        return listaRepo.findByIdUsuarioOrderByDataCriacaoDesc(idUsuario);
    }

    public Optional<ListaCompras> buscarPorId(Integer idLista) {
        return listaRepo.findById(idLista);
    }

    @Transactional
    public ListaCompras criar(Integer idUsuario, String nomeLista) {
        ListaCompras lista = ListaCompras.builder()
                .idUsuario(idUsuario)
                .nomeLista(nomeLista)
                .build();
        return listaRepo.save(lista);
    }

    @Transactional
    public ListaCompras adicionarItem(Integer idLista, Integer idProduto, Integer quantidade) {
        ListaCompras lista = listaRepo.findById(idLista)
                .orElseThrow(() -> new RuntimeException("Lista não encontrada: " + idLista));

        if (idProduto == null || !produtoRepo.existsById(idProduto)) {
            throw new IllegalArgumentException("Produto não cadastrado: " + idProduto);
        }

        // Verifica se o produto já está na lista
        Optional<ItemLista> existente = lista.getItens().stream()
                .filter(i -> i.getIdProduto().equals(idProduto))
                .findFirst();

        if (existente.isPresent()) {
            existente.get().setQuantidade(existente.get().getQuantidade() + (quantidade != null ? quantidade : 1));
        } else {
            ItemLista item = ItemLista.builder()
                    .lista(lista)
                    .idProduto(idProduto)
                    .quantidade(quantidade != null ? quantidade : 1)
                    .build();
            lista.getItens().add(item);
        }

        return listaRepo.save(lista);
    }

    @Transactional
    public ListaCompras removerItem(Integer idLista, Integer idProduto) {
        ListaCompras lista = listaRepo.findById(idLista)
                .orElseThrow(() -> new RuntimeException("Lista não encontrada: " + idLista));
        lista.getItens().removeIf(i -> i.getIdProduto().equals(idProduto));
        return listaRepo.save(lista);
    }

    @Transactional
    public void removerLista(Integer idLista) {
        listaRepo.deleteById(idLista);
    }

    /**
     * Otimizador: calcula o melhor plano de compras distribuído por mercados,
     * escolhendo o preço mais barato de cada item.
     */
    public ResultadoOtimizadorDTO otimizar(Integer idLista) {
        ListaCompras lista = listaRepo.findById(idLista)
                .orElseThrow(() -> new RuntimeException("Lista não encontrada: " + idLista));

        List<Preco> precos = precoRepo.findMelhoresPrecosPorLista(idLista);

        // Mapa de quantidades da lista
        Map<Integer, Integer> qtdMap = lista.getItens().stream()
                .collect(Collectors.toMap(ItemLista::getIdProduto, ItemLista::getQuantidade));

        // Agrupa o melhor preço por produto (o primeiro de cada grupo é o mais barato)
        Map<Integer, Preco> melhorPreco = new LinkedHashMap<>();
        for (Preco p : precos) {
            melhorPreco.putIfAbsent(p.getIdProduto(), p);
        }

        // Agrupa por mercado para gerar os planos
        Map<Integer, List<ItemOtimizadoDTO>> porMercado = new LinkedHashMap<>();
        BigDecimal totalOtimizado = BigDecimal.ZERO;

        for (Map.Entry<Integer, Preco> entry : melhorPreco.entrySet()) {
            Integer idProduto = entry.getKey();
            Preco p = entry.getValue();
            int qtd = qtdMap.getOrDefault(idProduto, 1);

            String nomeProduto = produtoRepo.findById(idProduto)
                    .map(Produto::getNomeProduto).orElse("Produto #" + idProduto);
            String nomeMercado = mercadoRepo.findById(p.getIdMercado())
                    .map(Mercado::getNomeMercado).orElse("Mercado #" + p.getIdMercado());

            ItemOtimizadoDTO item = new ItemOtimizadoDTO(
                    idProduto, nomeProduto, p.getIdMercado(), nomeMercado, p.getValor(), qtd);

            porMercado.computeIfAbsent(p.getIdMercado(), k -> new ArrayList<>()).add(item);
            totalOtimizado = totalOtimizado.add(p.getValor().multiply(BigDecimal.valueOf(qtd)));
        }

        // Monta planos
        List<PlanoMercadoDTO> planos = new ArrayList<>();
        for (Map.Entry<Integer, List<ItemOtimizadoDTO>> entry : porMercado.entrySet()) {
            Mercado m = mercadoRepo.findById(entry.getKey()).orElse(null);
            BigDecimal subtotal = entry.getValue().stream()
                    .map(i -> i.valor().multiply(BigDecimal.valueOf(i.quantidade())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            planos.add(new PlanoMercadoDTO(
                    entry.getKey(),
                    m != null ? m.getNomeMercado() : "Mercado #" + entry.getKey(),
                    m != null ? m.getLatitude() : null,
                    m != null ? m.getLongitude() : null,
                    entry.getValue(),
                    subtotal));
        }

        // Calcula o custo total se comprasse tudo no melhor mercado único
        BigDecimal totalMelhorMercadoUnico = BigDecimal.ZERO;
        if (!planos.isEmpty()) {
            // Para cada mercado, calcula quanto custaria comprar TODOS os itens nele
            // (usa o preço desse mercado quando disponível, senão ignora)
            Map<Integer, BigDecimal> custoTotalPorMercado = new HashMap<>();
            for (Map.Entry<Integer, Preco> entry : melhorPreco.entrySet()) {
                Integer idProd = entry.getKey();
                int qtd = qtdMap.getOrDefault(idProd, 1);
                // Busca preço deste produto em cada mercado
                for (Preco pr : precos) {
                    if (pr.getIdProduto().equals(idProd)) {
                        custoTotalPorMercado.merge(pr.getIdMercado(),
                                pr.getValor().multiply(BigDecimal.valueOf(qtd)), BigDecimal::add);
                    }
                }
            }
            totalMelhorMercadoUnico = custoTotalPorMercado.values().stream()
                    .min(BigDecimal::compareTo)
                    .orElse(totalOtimizado);
        }

        BigDecimal economia = totalMelhorMercadoUnico.subtract(totalOtimizado);

        return new ResultadoOtimizadorDTO(
                idLista, lista.getNomeLista(), planos,
                totalOtimizado, totalMelhorMercadoUnico, economia);
    }
}
