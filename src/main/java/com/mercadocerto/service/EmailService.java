package com.mercadocerto.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@mercadocerto.com}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmailRecuperacaoSenha(String para, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(para);
        message.setSubject("MercadoCerto — Recuperação de Senha");
        message.setText(
            "Olá!\n\n" +
            "Recebemos uma solicitação para redefinir a senha da sua conta MercadoCerto.\n\n" +
            "Clique no link abaixo para criar uma nova senha (válido por 1 hora):\n\n" +
            baseUrl + "/nova-senha.html?token=" + token + "\n\n" +
            "Se você não solicitou a redefinição de senha, ignore este e-mail.\n\n" +
            "Equipe MercadoCerto"
        );
        mailSender.send(message);
    }
}
