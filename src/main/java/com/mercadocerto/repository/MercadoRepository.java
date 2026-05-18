package com.mercadocerto.repository;
 
import com.mercadocerto.model.Mercado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
 
import java.util.List;
 
public interface MercadoRepository extends JpaRepository<Mercado, Integer> {
 
    @Query(value = """
        SELECT * FROM mercado m
        WHERE (6371 * acos(
            cos(radians(:lat)) * cos(radians(m.latitude)) *
            cos(radians(m.longitude) - radians(:lng)) +
            sin(radians(:lat)) * sin(radians(m.latitude))
        )) <= :raioKm
        """, nativeQuery = true)
    List<Mercado> buscarPorProximidade(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("raioKm") double raioKm);
}
 