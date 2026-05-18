package com.mercadocerto.controller;

import com.mercadocerto.dto.DTOs.ComparadorDTO;
import com.mercadocerto.model.Preco;
import com.mercadocerto.repository.PrecoRepository;
import com.mercadocerto.service.PrecoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/precos")
@CrossOrigin(origins = "*")
public class PrecoController {

    private final PrecoService    service;
    private final PrecoRepository precoRepo;

    public PrecoController(PrecoService service, PrecoRepository precoRepo) {
        this.service   = service;
        this.precoRepo = precoRepo;
    }

    @GetMapping("/comparar")
    public ResponseEntity<ComparadorDTO> comparar(@RequestParam("produto") Integer idProduto) {
        try {
            return ResponseEntity.ok(service.compararPrecos(idProduto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/historico")
    public List<Preco> historico(
            @RequestParam("produto") Integer idProduto,
            @RequestParam("mercado") Integer idMercado) {
        return service.historico(idProduto, idMercado);
    }

    @GetMapping("/mercado/{idMercado}")
    public List<Preco> catalogoMercado(@PathVariable Integer idMercado) {
        return service.catalogoMercado(idMercado);
    }

    @PostMapping
    public ResponseEntity<Preco> publicarPreco(@RequestBody Map<String, Object> body) {
        Integer idProduto = (Integer) body.get("idProduto");
        Integer idMercado = (Integer) body.get("idMercado");
        Object valorObj = body.get("valor");

        if (idProduto == null || idMercado == null || valorObj == null) {
            return ResponseEntity.badRequest().build();
        }

        BigDecimal valor;
        if (valorObj instanceof Number) {
            valor = BigDecimal.valueOf(((Number) valorObj).doubleValue());
        } else {
            valor = new BigDecimal(valorObj.toString());
        }

        Preco preco = Preco.builder()
                .idProduto(idProduto)
                .idMercado(idMercado)
                .valor(valor)
                .build();

        return ResponseEntity.ok(precoRepo.save(preco));
    }

    /** Remove um produto do catálogo do mercado (deleta o registro de preço) */
    @DeleteMapping("/{idPreco}")
    public ResponseEntity<Void> removerDoCaltalogo(@PathVariable Integer idPreco) {
        if (!precoRepo.existsById(idPreco)) {
            return ResponseEntity.notFound().build();
        }
        precoRepo.deleteById(idPreco);
        return ResponseEntity.noContent().build();
    }
}
