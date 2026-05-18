package com.mercadocerto.model;
 
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "preco")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Preco {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_preco")
    private Integer idPreco;
 
    @Column(name = "id_produto", nullable = false)
    private Integer idProduto;
 
    @Column(name = "id_mercado", nullable = false)
    private Integer idMercado;
 
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
 
    @Column(name = "data_hora")
    private LocalDateTime dataHora;
 
    @PrePersist
    public void prePersist() {
        if (dataHora == null) dataHora = LocalDateTime.now();
    }
}