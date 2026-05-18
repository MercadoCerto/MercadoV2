package com.mercadocerto.service;

import com.mercadocerto.dto.DTOs.ComparadorDTO;
import com.mercadocerto.dto.DTOs.PrecoMercadoDTO;
import com.mercadocerto.model.Mercado;
import com.mercadocerto.model.Preco;
import com.mercadocerto.model.Produto;
import com.mercadocerto.repository.MercadoRepository;
import com.mercadocerto.repository.PrecoRepository;
import com.mercadocerto.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PrecoService {

    private final PrecoRepository   precoRepo;
    private final ProdutoRepository produtoRepo;
    private final MercadoRepository mercadoRepo;

    public PrecoService(PrecoRepository precoRepo,
                        ProdutoRepository produtoRepo,
                        MercadoRepository mercadoRepo) {
        this.precoRepo   = precoRepo;
        this.produtoRepo = produtoRepo;
        this.mercadoRepo = mercadoRepo;
    }

    /**
     * Compara preços de um produto em todos os mercados que o vendem.
     */
    public ComparadorDTO compararPrecos(Integer idProduto) {
        Optional<Produto> produtoOpt = produtoRepo.findById(idProduto);
        if (produtoOpt.isEmpty()) {
            throw new RuntimeException("Produto não encontrado: " + idProduto);
        }

        Produto produto = produtoOpt.get();
        List<Preco> precos = precoRepo.findPrecoAtualPorMercado(idProduto);

        List<PrecoMercadoDTO> lista = new ArrayList<>();
        for (Preco p : precos) {
            String nomeMercado = mercadoRepo.findById(p.getIdMercado())
                    .map(Mercado::getNomeMercado)
                    .orElse("Mercado #" + p.getIdMercado());

            lista.add(new PrecoMercadoDTO(
                    p.getIdMercado(), nomeMercado, p.getValor(), p.getDataHora()));
        }

        lista.sort(Comparator.comparing(PrecoMercadoDTO::valor));
        return new ComparadorDTO(idProduto, produto.getNomeProduto(), lista);
    }

    /**
     * Histórico de preços de um produto em um mercado específico.
     */
    public List<Preco> historico(Integer idProduto, Integer idMercado) {
        return precoRepo.findByIdProdutoAndIdMercadoOrderByDataHoraAsc(idProduto, idMercado);
    }

    /**
     * Catálogo de preços atuais de um mercado (todos os produtos).
     */
    public List<Preco> catalogoMercado(Integer idMercado) {
        return precoRepo.findPrecoAtualPorMercadoCompleto(idMercado);
    }
}
