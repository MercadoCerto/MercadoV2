package com.mercadocerto.service;
 
import com.mercadocerto.model.Avaliacao;
import com.mercadocerto.repository.AvaliacaoRepository;
import org.springframework.stereotype.Service;
 
import java.time.LocalDateTime;
import java.util.*;
 
@Service
public class AvaliacaoService {
 
    private final AvaliacaoRepository repo;
 
    public AvaliacaoService(AvaliacaoRepository repo) {
        this.repo = repo;
    }
 
    public Avaliacao salvar(Avaliacao a) {
        // id_usuario agora é nullable no banco — deixa NULL para anônimos
        // (não seta 0, pois 0 não existe na tabela usuario e violaria a FK)
        if (a.getDataAvaliacao() == null) {
            a.setDataAvaliacao(LocalDateTime.now());
        }
        return repo.save(a);
    }
 
    public List<Avaliacao> listarPorMercado(Integer idMercado) {
        return repo.findByIdMercado(idMercado);
    }
 
    public Double mediaPorMercado(Integer idMercado) {
        return Optional.ofNullable(repo.calcularMediaPorMercado(idMercado)).orElse(0.0);
    }
 
    public Long totalPorMercado(Integer idMercado) {
        return Optional.ofNullable(repo.contarPorMercado(idMercado)).orElse(0L);
    }
 
    public List<Map<String, Object>> ranking() {
        List<Object[]> rows = repo.rankingNativo();
        List<Map<String, Object>> lista = new ArrayList<>();
        for (Object[] r : rows) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id_mercado",   r[0]);
            item.put("nome_mercado", r[1]);
            item.put("media",        r[2]);
            item.put("votos",        r[3]);
            lista.add(item);
        }
        return lista;
    }
}