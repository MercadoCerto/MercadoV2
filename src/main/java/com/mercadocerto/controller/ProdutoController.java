package com.mercadocerto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadocerto.model.Produto;
import com.mercadocerto.repository.ProdutoRepository;
import com.mercadocerto.service.ProdutoService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    private final ProdutoService    produtoService;
    private final ProdutoRepository produtoRepo;
    private final ObjectMapper      objectMapper;

    public ProdutoController(ProdutoService produtoService,
                             ProdutoRepository produtoRepo,
                             ObjectMapper objectMapper) {
        this.produtoService = produtoService;
        this.produtoRepo    = produtoRepo;
        this.objectMapper   = objectMapper;
    }

    @GetMapping
    public List<Produto> listarTodos() {
        return produtoRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Integer id) {
        return produtoRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cadastro de produto via multipart/form-data.
     */
    @PostMapping(value = "/cadastrar", consumes = "multipart/form-data")
    public ResponseEntity<?> cadastrar(
            @RequestPart("produto")                              String        produtoJson,
            @RequestPart(value = "idMercado", required = false) String        idMercadoStr,
            @RequestPart(value = "imagem",    required = false) MultipartFile imagem
    ) throws Exception {

        Produto produto = objectMapper.readValue(produtoJson, Produto.class);

        if (idMercadoStr != null && !idMercadoStr.isBlank()) {
            produto.setIdMercado(Integer.parseInt(idMercadoStr.trim()));
        }

        Produto salvo = produtoService.salvarProduto(produto, imagem);
        return ResponseEntity.ok(salvo);
    }

    /** Exclui um produto. Retorna 409 se existirem registros dependentes. */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable Integer id) {
        if (!produtoRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            produtoRepo.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409)
                    .body("Produto possui preços ou listas vinculadas. Remova-os primeiro.");
        }
    }
}
