package br.com.loginService.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${MAIL_HOST}")
    private String mailFrom;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendConfirmationEmail(String to, String code) throws MessagingException {
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

        helper.setFrom(mailFrom);
        helper.setTo(to);
        helper.setSubject("Confirme seu email");

        String html = """
            <html>
               <body>
                   <h2>Bem-vindo!</h2>
                   <p>Segue abaixo seu código de confirmação:</p>
                   <p><strong>%s</strong></p>
                </body>
           </html>
        """.formatted(code);

        helper.setText(html, true);

        mailSender.send(msg);
    }
}
