package com.mercadocerto.service;

import com.mercadocerto.model.Usuario;
import com.mercadocerto.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder   passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder   = passwordEncoder;
    }

    public Usuario cadastrar(Usuario usuario) {
        if (usuario.getLogin() == null || usuario.getLogin().isBlank()) {
            usuario.setLogin(usuario.getEmail());
        }
        if (usuario.getNomeUsuario() == null || usuario.getNomeUsuario().isBlank()) {
            usuario.setNomeUsuario(usuario.getLogin());
        }

        if (Usuario.TipoConta.COMERCIO.equals(usuario.getTipoConta())) {
            if (usuario.getCnpj() == null || usuario.getCnpj().isBlank()) {
                throw new IllegalArgumentException("CNPJ é obrigatório para contas de comércio");
            }
            String cnpjLimpo = usuario.getCnpj().replaceAll("[^0-9]", "");
            if (!validarCnpj(cnpjLimpo)) {
                throw new IllegalArgumentException("CNPJ inválido");
            }
            usuario.setCnpj(cnpjLimpo);
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarPorLogin(String login) {
        return usuarioRepository.findByLogin(login);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    private boolean validarCnpj(String cnpj) {
        if (cnpj.length() != 14) return false;
        if (cnpj.chars().distinct().count() == 1) return false;

        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int soma = 0;
        for (int i = 0; i < 12; i++) {
            soma += (cnpj.charAt(i) - '0') * pesos1[i];
        }
        int dig1 = soma % 11 < 2 ? 0 : 11 - (soma % 11);
        if ((cnpj.charAt(12) - '0') != dig1) return false;

        soma = 0;
        for (int i = 0; i < 13; i++) {
            soma += (cnpj.charAt(i) - '0') * pesos2[i];
        }
        int dig2 = soma % 11 < 2 ? 0 : 11 - (soma % 11);
        return (cnpj.charAt(13) - '0') == dig2;
    }
}
