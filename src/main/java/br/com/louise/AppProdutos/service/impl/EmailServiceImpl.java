package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}") // Usamos o user do config como remetente padrão
    private String sender;

    @Override
    @Async // Executa em uma thread separada (não trava o sistema)
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            log.info("📧 Enviando email para: {}", to);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender); // Quem manda (no Mailtrap isso é fictício)
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            log.info("Email enviado com sucesso!");
        } catch (Exception e) {
            log.error("Falha ao enviar email: ", e);
        }
    }
}