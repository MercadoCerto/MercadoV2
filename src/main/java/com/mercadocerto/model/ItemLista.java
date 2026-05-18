package com.mercadocerto.model;
 
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
 
@Entity
@Table(name = "item_lista")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ItemLista {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    private Integer idItem;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lista", nullable = false)
    @JsonBackReference
    private ListaCompras lista;
 
    @Column(name = "id_produto", nullable = false)
    private Integer idProduto;
 
    @Column(nullable = false)
    @Builder.Default
    private Integer quantidade = 1;
}