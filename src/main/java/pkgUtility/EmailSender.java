package pkgUtility;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.Random;

public class EmailSender {
    private static EmailSender instance;
    private final String username = "shareroomafam@gmail.com"; // Sostituire con email reale
    private final String password = "password"; // Sostituire con password reale/app password

    private EmailSender() {}

    public static EmailSender getInstance() {
        if (instance == null) {
            instance = new EmailSender();
        }
        return instance;
    }

    public void inviaEmail(String destinatario, String subject, String text) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public String generaCodice() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }
}
