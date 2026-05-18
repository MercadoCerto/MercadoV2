package com.mercadocerto.controller;

import com.mercadocerto.dto.DTOs.LoginResponseDTO;
import com.mercadocerto.model.PasswordResetToken;
import com.mercadocerto.model.Usuario;
import com.mercadocerto.repository.PasswordResetRepository;
import com.mercadocerto.service.EmailService;
import com.mercadocerto.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetRepository resetRepo;

    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> cadastrar(@RequestBody Usuario usuario) {
        try {
            Usuario cadastrado = usuarioService.cadastrar(usuario);
            return ResponseEntity.ok(toLoginResponse(cadastrado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> dados) {
        String login = dados.get("login");
        String senha = dados.get("senha");

        Optional<Usuario> user = usuarioService.buscarPorLogin(login);
        if (user.isEmpty()) {
            user = usuarioService.buscarPorEmail(login);
        }

        if (user.isPresent() && passwordEncoder.matches(senha, user.get().getSenha())) {
            return ResponseEntity.ok(toLoginResponse(user.get()));
        }

        return ResponseEntity.status(401).body("Credenciais inválidas");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("E-mail obrigatório");
        }

        Optional<Usuario> userOpt = usuarioService.buscarPorEmail(email.trim().toLowerCase());
        if (userOpt.isEmpty()) {
            // Retorna ok mesmo se não encontrado (segurança — não revelar se e-mail existe)
            return ResponseEntity.ok(Map.of("message", "Se o e-mail estiver cadastrado, você receberá as instruções em breve."));
        }

        Usuario usuario = userOpt.get();

        // Remove tokens anteriores do mesmo usuário
        resetRepo.deleteByIdUsuario(usuario.getIdUsuario());

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .idUsuario(usuario.getIdUsuario())
                .expiracao(LocalDateTime.now().plusHours(1))
                .usado(false)
                .build();
        resetRepo.save(resetToken);

        try {
            emailService.enviarEmailRecuperacaoSenha(usuario.getEmail(), token);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao enviar e-mail. Tente novamente.");
        }

        return ResponseEntity.ok(Map.of("message", "Se o e-mail estiver cadastrado, você receberá as instruções em breve."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String novaSenha = body.get("novaSenha");

        if (token == null || novaSenha == null || novaSenha.length() < 6) {
            return ResponseEntity.badRequest().body("Token e nova senha (mínimo 6 caracteres) são obrigatórios");
        }

        Optional<PasswordResetToken> tokenOpt = resetRepo.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Token inválido");
        }

        PasswordResetToken resetToken = tokenOpt.get();
        if (resetToken.isUsado()) {
            return ResponseEntity.badRequest().body("Token já utilizado");
        }
        if (resetToken.getExpiracao().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token expirado. Solicite uma nova recuperação de senha.");
        }

        Optional<Usuario> userOpt = usuarioService.buscarPorId(resetToken.getIdUsuario());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }

        Usuario usuario = userOpt.get();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioService.salvar(usuario);

        resetToken.setUsado(true);
        resetRepo.save(resetToken);

        return ResponseEntity.ok(Map.of("message", "Senha alterada com sucesso!"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        return usuarioService.buscarPorId(id)
                .map(u -> ResponseEntity.ok(toLoginResponse(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listarTodos();
    }

    @GetMapping("/login/{login}")
    public Optional<Usuario> buscarPorLogin(@PathVariable String login) {
        return usuarioService.buscarPorLogin(login);
    }

    @GetMapping("/email/{email}")
    public Optional<Usuario> buscarPorEmail(@PathVariable String email) {
        return usuarioService.buscarPorEmail(email);
    }

    private LoginResponseDTO toLoginResponse(Usuario u) {
        return new LoginResponseDTO(
            u.getIdUsuario(),
            u.getNomeUsuario(),
            u.getEmail(),
            u.getLogin(),
            u.getTipoConta().name()
        );
    }
}
