package com.mercadocerto.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mercado")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Mercado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mercado")
    private Integer idMercado;

    @Column(name = "nome_mercado", nullable = false)
    private String nomeMercado;

    private Double latitude;
    private Double longitude;

    /** Endereço completo do mercado (ex: Rua das Flores, 123 - Centro) */
    @Column(length = 300)
    private String endereco;

    /** Cidade onde o mercado está localizado */
    @Column(length = 100)
    private String cidade;

    /** Nome do arquivo de foto salvo em /uploads/ */
    @Column(length = 255)
    private String foto;
}
