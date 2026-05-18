package com.mercadocerto.controller;

import com.mercadocerto.dto.DTOs.ItemListaRequestDTO;
import com.mercadocerto.dto.DTOs.ResultadoOtimizadorDTO;
import com.mercadocerto.model.ListaCompras;
import com.mercadocerto.service.ListaComprasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lista")
@CrossOrigin(origins = "*")
public class ListaComprasController {

    private final ListaComprasService service;

    public ListaComprasController(ListaComprasService service) {
        this.service = service;
    }

    /**
     * Listar listas de compras de um usuário.
     * GET /api/lista/usuario/{idUsuario}
     */
    @GetMapping("/usuario/{idUsuario}")
    public List<ListaCompras> listarPorUsuario(@PathVariable Integer idUsuario) {
        return service.listarPorUsuario(idUsuario);
    }

    /**
     * Buscar lista por ID.
     * GET /api/lista/{idLista}
     */
    @GetMapping("/{idLista}")
    public ResponseEntity<ListaCompras> buscar(@PathVariable Integer idLista) {
        return service.buscarPorId(idLista)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Criar nova lista de compras.
     * POST /api/lista { "idUsuario": 1, "nomeLista": "Compras da semana" }
     */
    @PostMapping
    public ResponseEntity<ListaCompras> criar(@RequestBody Map<String, Object> body) {
        Integer idUsuario = (Integer) body.get("idUsuario");
        String nomeLista = (String) body.get("nomeLista");

        if (idUsuario == null || nomeLista == null || nomeLista.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        ListaCompras lista = service.criar(idUsuario, nomeLista);
        return ResponseEntity.created(URI.create("/api/lista/" + lista.getIdLista())).body(lista);
    }

    /**
     * Adicionar item à lista.
     * POST /api/lista/{idLista}/item { "idProduto": 1, "quantidade": 2 }
     */
    @PostMapping("/{idLista}/item")
    public ResponseEntity<ListaCompras> adicionarItem(
            @PathVariable Integer idLista,
            @RequestBody ItemListaRequestDTO item) {
        try {
            ListaCompras lista = service.adicionarItem(idLista, item.idProduto(), item.quantidade());
            return ResponseEntity.ok(lista);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Remover item da lista.
     * DELETE /api/lista/{idLista}/item/{idProduto}
     */
    @DeleteMapping("/{idLista}/item/{idProduto}")
    public ResponseEntity<ListaCompras> removerItem(
            @PathVariable Integer idLista,
            @PathVariable Integer idProduto) {
        try {
            ListaCompras lista = service.removerItem(idLista, idProduto);
            return ResponseEntity.ok(lista);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Remover lista inteira.
     * DELETE /api/lista/{idLista}
     */
    @DeleteMapping("/{idLista}")
    public ResponseEntity<Void> removerLista(@PathVariable Integer idLista) {
        service.removerLista(idLista);
        return ResponseEntity.noContent().build();
    }

    /**
     * Otimizar lista de compras (melhor preço por item distribuído por mercados).
     * GET /api/lista/{idLista}/otimizar
     */
    @GetMapping("/{idLista}/otimizar")
    public ResponseEntity<ResultadoOtimizadorDTO> otimizar(@PathVariable Integer idLista) {
        try {
            return ResponseEntity.ok(service.otimizar(idLista));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
