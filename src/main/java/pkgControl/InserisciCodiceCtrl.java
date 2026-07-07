package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import pkgUtility.Router;
import pkgUtility.UserSession;
import pkgBoundary.DBMSboundary;
import pkgTextmessage.ErrorText;
import java.sql.ResultSet;

public class InserisciCodiceCtrl {

    @FXML
    private TextField codiceField;

    @FXML
    public void handleInvia(ActionEvent event) {
        String codice = codiceField.getText();
        String email = UserSession.getInstance().getEmailInVerifica();
        String azione = UserSession.getInstance().getAzioneVerifica();

        if (codice == null || codice.isEmpty()) {
            new ErrorText("Inserisci un codice valido.").okay();
            return;
        }

        try {
            ResultSet rs = DBMSboundary.getInstance().queryDBMSVerificaCodice(email, codice);
            if (rs != null && rs.next()) {
                String cf = rs.getString("codiceFiscale");

                if ("LOGIN".equals(azione)) {
                    UserSession.getInstance().setUtenteLoggato(cf);
                    Router.getInstance().navigate("profilo.fxml", "ShareRoomAfam - Profilo");
                } else if ("RECUPERO".equals(azione)) {
                    // RAD rcpr_pswd step 6.5: mostra la password corrente e reindirizza al login
                    String currentPassword = rs.getString("password");
                    new pkgTextmessage.SuccessfulText("La tua password corrente è: " + currentPassword
                            + "\nAccedi e cambiala subito dal pannello Gestione Profilo.").okay();
                    Router.getInstance().navigate("login.fxml", "ShareRoomAfam - Login");
                }
            } else {
                new ErrorText("Codice OTP errato. Riprova.").okay();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new ErrorText("Errore durante la verifica del codice.").okay();
        }
    }

    @FXML
    public void handleTornaIndietro(ActionEvent event) {
        Router.getInstance().navigate("login.fxml", "ShareRoomAfam - Login");
    }
}
