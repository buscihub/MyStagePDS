package pkgUtility;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {

    // Dummy credentials (should be replaced by real ones)
    public static final String EMAIL_MITTENTE = "federicobusciglio229@gmail.com";
    public static final String PASSWORD_MITTENTE = "vkmebtwclpnnyqdr";

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
            message.setText("Il tuo codice di verifica OTP è: " + codiceGenerato
                    + "\n\nNon condividere questo codice con nessuno.");

            // Invio reale
            Transport.send(message);

            return true;
        } catch (Exception mex) {
            mex.printStackTrace();
            return false;
        }
    }
}
