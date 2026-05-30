package com.mercadocerto.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "produto")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Integer idProduto;

    @Column(name = "nome_produto", nullable = false, length = 100)
    private String nomeProduto;

    @Column(nullable = false, length = 60)
    private String marca;

    @Column(nullable = false, length = 60)
    private String categoria;

    @Column(name = "codigo_barras", nullable = false, length = 30)
    private String codigoBarras;

    @Column(length = 255)
    private String imagem;

    @Column(nullable = false)
    private Double preco;

    @Column(nullable = false, length = 30)
    private String validade;

    /**
     * Tipo de medida da embalagem. Determina quais dos campos abaixo são
     * relevantes e como o preço por unidade é calculado:
     *   PESO    → peso + unidade (kg/g),  quantidade ignorada
     *   VOLUME  → peso + unidade (L/ml),  quantidade ignorada
     *   UNIDADE → quantidade,             peso/unidade ignorados
     *   PACK    → quantidade × peso + unidade (kg/g/L/ml)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_medida", length = 20)
    private TipoMedida tipoMedida;

    /** Quantidade de itens na embalagem (UNIDADE: total; PACK: itens do pack). */
    @Column
    private Integer quantidade;

    /** Peso/volume — total (PESO/VOLUME) ou por item (PACK). */
    @Column
    private Double peso;

    /** Unidade de medida: kg, g, L, ml, un */
    @Column(length = 10)
    private String unidade;

    /** Coordenadas onde o produto foi registrado */
    private Double latitude;
    private Double longitude;

    /**
     * Usado apenas no fluxo de cadastro para vincular ao mercado via tabela preco.
     * NÃO é persistido como coluna — a relação existe na tabela preco.
     */
    @Transient
    private Integer idMercado;
}
