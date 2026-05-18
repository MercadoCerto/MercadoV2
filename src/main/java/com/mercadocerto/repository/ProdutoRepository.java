package com.mercadocerto.repository;
 
import com.mercadocerto.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
 
import java.util.Optional;
 
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
 
    Optional<Produto> findByCodigoBarras(String codigoBarras);
}
 