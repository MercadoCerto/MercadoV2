package com.mercadocerto.repository;
 
import com.mercadocerto.model.Preco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
 
import java.util.List;
 
public interface PrecoRepository extends JpaRepository<Preco, Integer> {
 
    List<Preco> findByIdProdutoAndIdMercadoOrderByDataHoraAsc(
            Integer idProduto, Integer idMercado);
 
    /** Preço atual (mais recente) de um produto em cada mercado, ordenado do mais barato */
    @Query(value = """
        SELECT p1.*
        FROM preco p1
        INNER JOIN (
            SELECT id_mercado, MAX(data_hora) AS ultima
            FROM preco
            WHERE id_produto = :idProduto
            GROUP BY id_mercado
        ) p2 ON p1.id_mercado = p2.id_mercado
             AND p1.data_hora  = p2.ultima
             AND p1.id_produto = :idProduto
        ORDER BY p1.valor ASC
        """, nativeQuery = true)
    List<Preco> findPrecoAtualPorMercado(@Param("idProduto") Integer idProduto);
 
    /** Todos os preços atuais dos produtos de um mercado (catálogo do comércio) */
    @Query(value = """
        SELECT p1.*
        FROM preco p1
        INNER JOIN (
            SELECT id_produto, MAX(data_hora) AS ultima
            FROM preco
            WHERE id_mercado = :idMercado
            GROUP BY id_produto
        ) p2 ON p1.id_produto = p2.id_produto
             AND p1.data_hora  = p2.ultima
             AND p1.id_mercado = :idMercado
        ORDER BY p1.id_produto ASC
        """, nativeQuery = true)
    List<Preco> findPrecoAtualPorMercadoCompleto(@Param("idMercado") Integer idMercado);
 
    /** Menor preço atual de cada item de uma lista (para o otimizador) */
    @Query(value = """
        SELECT p1.*
        FROM preco p1
        INNER JOIN item_lista il ON il.id_produto = p1.id_produto
        INNER JOIN (
            SELECT id_produto, id_mercado, MAX(data_hora) AS ultima
            FROM preco
            GROUP BY id_produto, id_mercado
        ) p2 ON p1.id_produto = p2.id_produto
             AND p1.id_mercado = p2.id_mercado
             AND p1.data_hora  = p2.ultima
        WHERE il.id_lista = :idLista
        ORDER BY p1.id_produto, p1.valor ASC
        """, nativeQuery = true)
    List<Preco> findMelhoresPrecosPorLista(@Param("idLista") Integer idLista);
}