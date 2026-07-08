package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import pkgBoundary.DBMSboundary;
import pkgTextmessage.ErrorText;
import pkgUtility.EmailSender;
import pkgUtility.Router;
import pkgUtility.UserSession;

import java.sql.ResultSet;

/**
 * Controller per la schermata "Recupera Password" (RecuperaPasswordForm).
 * RAD §3.3.4.4 — Caso d'uso rcpr_pswd, passi 1–6.1.
 * Riceve l'email, verifica esistenza account, genera OTP e naviga a inserisci_codice.fxml.
 */
public class RecuperaPasswordCtrl {

    @FXML
    private TextField emailRecuperoField;

    @FXML
    public void handleInviaEmail(ActionEvent event) {
        String email = emailRecuperoField.getText();
        if (email == null || email.trim().isEmpty()) {
            new ErrorText("Inserisci la tua email.").okay();
            return;
        }

        try {
            ResultSet rs = DBMSboundary.getInstance().queryDBMSVerificaEmail(email.trim());
            if (rs != null && rs.next()) {
                // RAD rcpr_pswd passo 6.1 — genera, salva e invia OTP
                String otp = String.format("%06d", new java.util.Random().nextInt(999999));
                DBMSboundary.getInstance().insertDBMScodice(email.trim(), otp);

                boolean sent = EmailSender.inviaCodice2FA(email.trim(), otp);
                if (sent) {
                    UserSession.getInstance().setEmailInVerifica(email.trim());
                    UserSession.getInstance().setAzioneVerifica("RECUPERO");
                    // RAD passo 6.2 — compare il form "Inserisci codice di recupero"
                    Router.getInstance().navigate("inserisci_codice.fxml", "MyStage - Verifica 2FA");
                } else {
                    new ErrorText("Errore durante l'invio dell'email.").okay();
                }
            } else {
                // RAD passo 5.1 — "Indirizzo email inesistente"
                new ErrorText("Indirizzo email inesistente.").okay();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new ErrorText("Errore di connessione al sistema.").okay();
        }
    }

    @FXML
    public void handleTornaLogin(ActionEvent event) {
        Router.getInstance().navigate("login.fxml", "MyStage - Login");
    }
}
