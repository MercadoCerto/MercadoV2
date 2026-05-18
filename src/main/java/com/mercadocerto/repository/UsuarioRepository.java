package com.mercadocerto.repository;
 
import com.mercadocerto.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
 
import java.util.Optional;
 
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
 
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByLogin(String login);
}
 