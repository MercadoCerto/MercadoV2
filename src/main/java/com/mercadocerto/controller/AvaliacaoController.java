package com.mercadocerto.controller;

import com.mercadocerto.model.Avaliacao;
import com.mercadocerto.service.AvaliacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/avaliacoes")
@CrossOrigin(origins = "*")
public class AvaliacaoController {

    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Avaliacao> cadastrar(@RequestBody Avaliacao a) {
        Avaliacao salvo = service.salvar(a);
        return ResponseEntity.created(URI.create("/api/avaliacoes/" + salvo.getIdAvaliacao())).body(salvo);
    }

    @GetMapping("/mercado/{idMercado}")
    public List<Avaliacao> listarPorMercado(@PathVariable Integer idMercado) {
        return service.listarPorMercado(idMercado);
    }

    @GetMapping("/media/{idMercado}")
    public Double media(@PathVariable Integer idMercado) {
        return service.mediaPorMercado(idMercado);
    }

    @GetMapping("/total/{idMercado}")
    public Long total(@PathVariable Integer idMercado) {
        return service.totalPorMercado(idMercado);
    }

    @GetMapping("/ranking")
    public List<Map<String, Object>> ranking() {
        return service.ranking();
    }
}
