package pkgUtility;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {

    // Dummy credentials (should be replaced by real ones)
    public static final String EMAIL_MITTENTE = "noreply.mystage@gmail.com";
    public static final String PASSWORD_MITTENTE = "dummy_password_123";

    public static boolean inviaCodice2FA(String emailDestinatario, String codiceGenerato) {
        String host = "smtp.gmail.com";
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_MITTENTE, PASSWORD_MITTENTE);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_MITTENTE));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailDestinatario));
            message.setSubject("MyStage - Codice di Verifica");
            message.setText("Il tuo codice di verifica OTP è: " + codiceGenerato + "\n\nNon condividere questo codice con nessuno.");

            // To avoid crash if credentials are wrong, we catch AuthenticationFailedException
            // Transport.send(message); // Uncomment to really send
            
            System.out.println("\n=======================================================");
            System.out.println(" 🚨 SIMULAZIONE INVIO EMAIL 2FA 🚨");
            System.out.println("=======================================================");
            System.out.println("Destinatario: " + emailDestinatario);
            System.out.println("Codice OTP:   " + codiceGenerato);
            System.out.println("=======================================================\n");

            return true;
        } catch (Exception mex) {
            mex.printStackTrace();
            return false;
        }
    }
}
