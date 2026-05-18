package com.mercadocerto.service;
 
import com.mercadocerto.model.Preco;
import com.mercadocerto.model.Produto;
import com.mercadocerto.repository.PrecoRepository;
import com.mercadocerto.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepo;
    private final PrecoRepository   precoRepo;

    @Value("${uploads.path:uploads}")
    private String uploadsPath;

    public ProdutoService(ProdutoRepository produtoRepo, PrecoRepository precoRepo) {
        this.produtoRepo = produtoRepo;
        this.precoRepo   = precoRepo;
    }

    public Produto salvarProduto(Produto produto, MultipartFile imagem) throws IOException {
        if (produto.getPreco() == null) {
            throw new IllegalArgumentException("Preço é obrigatório.");
        }
        if (imagem != null && !imagem.isEmpty()) {
            produto.setImagem(salvarImagemNoDisco(imagem));
        }

        Integer idMercado = produto.getIdMercado();
        Produto salvo = produtoRepo.save(produto);

        // Cria registro na tabela preco vinculando produto ao mercado
        if (idMercado != null) {
            Preco preco = Preco.builder()
                    .idProduto(salvo.getIdProduto())
                    .idMercado(idMercado)
                    .valor(BigDecimal.valueOf(salvo.getPreco()))
                    .build();
            precoRepo.save(preco);
        }

        return salvo;
    }

    public List<Produto> listarTodos() {
        return produtoRepo.findAll();
    }
 
    private String salvarImagemNoDisco(MultipartFile imagem) throws IOException {
        Path dir = Paths.get(uploadsPath);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        String original  = imagem.getOriginalFilename();
        // Sanitização segura: remove qualquer caracter fora de alfanumérico, ponto e hífen
        String nomeSeguro = System.currentTimeMillis() + "_" +
                (original != null ? original.replaceAll("[^a-zA-Z0-9._-]", "_") : "img");
        Files.copy(imagem.getInputStream(), dir.resolve(nomeSeguro));
        return nomeSeguro;
    }
}