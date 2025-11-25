package br.com.louise.AppProdutos.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @InjectMocks
    private EmailServiceImpl emailService;

    @Mock
    private JavaMailSender mailSender;

    @BeforeEach
    void setup() {
        // Injeta o valor da propriedade @Value manualmente
        ReflectionTestUtils.setField(emailService, "sender", "no-reply@test.com");
    }

    @Test
    void sendSimpleEmail_ShouldSendMail_WhenValid() {
        String to = "user@test.com";
        String subject = "Teste";
        String body = "Conteúdo";

        emailService.sendSimpleEmail(to, subject, body);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(body, sentMessage.getText());
        assertEquals("no-reply@test.com", sentMessage.getFrom());
    }

    @Test
    void sendSimpleEmail_ShouldLogException_WhenMailSenderFails() {
        // Simula erro no envio
        doThrow(new RuntimeException("Erro SMTP")).when(mailSender).send(any(SimpleMailMessage.class));

        // O método não deve lançar exceção para quem chamou (por causa do try-catch interno)
        assertDoesNotThrow(() ->
                emailService.sendSimpleEmail("to", "sub", "body")
        );
    }
}