package com.mercadocerto.model;
 
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "avaliacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Avaliacao {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avaliacao")
    private Integer idAvaliacao;
 
    @Column(name = "id_mercado", nullable = false)
    private Integer idMercado;
 
    // nullable = true para aceitar avaliações anônimas (id = 0)
    // O service preenche com 0 quando não informado
    @Column(name = "id_usuario")
    private Integer idUsuario;
 
    @Column(name = "nota")
    private Integer nota;
 
    @Column(name = "comentario", columnDefinition = "text")
    private String comentario;
 
    @Column(name = "data_avaliacao")
    private LocalDateTime dataAvaliacao;
}
 