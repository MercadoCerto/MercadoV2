package com.mercadocerto.controller;

import com.mercadocerto.dto.DTOs.DashboardComercioDTO;
import com.mercadocerto.dto.DTOs.ReputacaoMensalDTO;
import com.mercadocerto.model.Avaliacao;
import com.mercadocerto.model.Mercado;
import com.mercadocerto.model.Preco;
import com.mercadocerto.repository.MercadoRepository;
import com.mercadocerto.service.AvaliacaoService;
import com.mercadocerto.service.PrecoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/painel")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final AvaliacaoService avaliacaoService;
    private final PrecoService     precoService;
    private final MercadoRepository mercadoRepo;

    public DashboardController(AvaliacaoService avaliacaoService,
                               PrecoService precoService,
                               MercadoRepository mercadoRepo) {
        this.avaliacaoService = avaliacaoService;
        this.precoService     = precoService;
        this.mercadoRepo      = mercadoRepo;
    }

    /**
     * Dashboard do comércio (painel-comercio.html).
     * GET /api/painel/dashboard/{idMercado}
     */
    @GetMapping("/dashboard/{idMercado}")
    public ResponseEntity<DashboardComercioDTO> dashboardComercio(@PathVariable Integer idMercado) {
        Optional<Mercado> mercadoOpt = mercadoRepo.findById(idMercado);
        if (mercadoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Double mediaGeral = avaliacaoService.mediaPorMercado(idMercado);
        Long totalAvaliacoes = avaliacaoService.totalPorMercado(idMercado);

        // Total de produtos cadastrados no mercado
        List<Preco> catalogo = precoService.catalogoMercado(idMercado);
        int totalProdutos = catalogo.size();

        // Evolução mensal das avaliações
        List<Avaliacao> avaliacoes = avaliacaoService.listarPorMercado(idMercado);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");

        Map<String, List<Avaliacao>> porMes = avaliacoes.stream()
                .filter(a -> a.getDataAvaliacao() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getDataAvaliacao().format(fmt),
                        TreeMap::new,
                        Collectors.toList()));

        List<ReputacaoMensalDTO> evolucao = new ArrayList<>();
        for (Map.Entry<String, List<Avaliacao>> entry : porMes.entrySet()) {
            double media = entry.getValue().stream()
                    .filter(a -> a.getNota() != null)
                    .mapToInt(Avaliacao::getNota)
                    .average().orElse(0.0);
            evolucao.add(new ReputacaoMensalDTO(
                    entry.getKey(), Math.round(media * 100.0) / 100.0, (long) entry.getValue().size()));
        }

        // Avaliações recentes (últimas 10)
        List<Map<String, Object>> recentes = avaliacoes.stream()
                .sorted(Comparator.comparing(
                        Avaliacao::getDataAvaliacao, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .map(a -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("nota", a.getNota());
                    m.put("comentario", a.getComentario());
                    m.put("data", a.getDataAvaliacao() != null ? a.getDataAvaliacao().toString() : null);
                    return m;
                })
                .collect(Collectors.toList());

        DashboardComercioDTO dto = new DashboardComercioDTO(
                mediaGeral, totalAvaliacoes, totalProdutos, evolucao, recentes);

        return ResponseEntity.ok(dto);
    }
}
