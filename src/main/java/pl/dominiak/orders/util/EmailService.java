package pl.dominiak.orders.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import pl.dominiak.orders.config.AppSettings;

public class EmailService {
    public static void send(String to, String subject, String content, AppSettings settings) throws Exception {
        Properties prop = new Properties();

        prop.put("mail.smtp.host", "localhost");
        prop.put("mail.smtp.port", "1025");

        Session session = Session.getInstance(prop);
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("orders@dominiak.pl"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(content, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        message.setContent(multipart);

        Transport.send(message);
    }
}