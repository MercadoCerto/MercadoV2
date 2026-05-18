package com.mercadocerto.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_token")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String token;

    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "expiracao", nullable = false)
    private LocalDateTime expiracao;

    @Column(nullable = false)
    private boolean usado = false;
}
