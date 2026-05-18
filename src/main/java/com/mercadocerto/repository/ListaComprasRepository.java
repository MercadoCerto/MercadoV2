package com.mercadocerto.repository;
 
import com.mercadocerto.model.ListaCompras;
import org.springframework.data.jpa.repository.JpaRepository;
 
import java.util.List;
 
public interface ListaComprasRepository extends JpaRepository<ListaCompras, Integer> {
 
    List<ListaCompras> findByIdUsuarioOrderByDataCriacaoDesc(Integer idUsuario);
}
 