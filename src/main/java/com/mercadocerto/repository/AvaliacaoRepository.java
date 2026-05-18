package com.mercadocerto.repository;
 
import com.mercadocerto.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
 
import java.util.List;
 
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Integer> {
 
    List<Avaliacao> findByIdMercado(Integer idMercado);
 
    @Query(value = "SELECT AVG(a.nota) FROM avaliacao a WHERE a.id_mercado = :idMercado",
           nativeQuery = true)
    Double calcularMediaPorMercado(@Param("idMercado") Integer idMercado);
 
    @Query(value = "SELECT COUNT(a.id_avaliacao) FROM avaliacao a WHERE a.id_mercado = :idMercado",
           nativeQuery = true)
    Long contarPorMercado(@Param("idMercado") Integer idMercado);
 
    // Mantido para compatibilidade com o AvaliacaoService
    @Query(value =
            "SELECT m.id_mercado, m.nome_mercado, " +
            "ROUND(AVG(a.nota),2) AS media, COUNT(a.id_avaliacao) AS votos " +
            "FROM mercado m " +
            "LEFT JOIN avaliacao a ON a.id_mercado = m.id_mercado " +
            "GROUP BY m.id_mercado, m.nome_mercado " +
            "ORDER BY media DESC",
            nativeQuery = true)
    List<Object[]> rankingNativo();
}