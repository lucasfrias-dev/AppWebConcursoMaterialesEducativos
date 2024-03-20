package ar.edu.unnoba.appweb_concurso_materiales_educativos.service.email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom("ced.prueba123@gmail.com");
        message.setText(body);
        message.setSubject(subject);

        javaMailSender.send(message);

        System.out.println("Email enviado exitosamente a " + toEmail);
    }
}