package com.mercadocerto.model;
 
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
 
@Entity
@Table(name = "lista_compras")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ListaCompras {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lista")
    private Integer idLista;
 
    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;
 
    @Column(name = "nome_lista", nullable = false, length = 100)
    private String nomeLista;
 
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;
 
    @OneToMany(mappedBy = "lista", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<ItemLista> itens = new ArrayList<>();
 
    @PrePersist
    public void prePersist() {
        if (dataCriacao == null) dataCriacao = LocalDateTime.now();
    }
}
 