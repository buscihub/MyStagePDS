package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import pkgUtility.Router;
import pkgUtility.UserSession;
import pkgBoundary.DBMSboundary;

public class ModificaPasswordCtrl {

    @FXML
    private PasswordField nuovaPasswordField;

    @FXML
    private PasswordField confermaPasswordField;

    @FXML
    public void conferma(ActionEvent event) {
        String p1 = nuovaPasswordField.getText();
        String p2 = confermaPasswordField.getText();
        if (p1 == null || p1.trim().isEmpty()) {
            new pkgTextmessage.ErrorText("La password non può essere vuota.").okay();
            return;
        }
        if (!p1.equals(p2)) {
            new pkgTextmessage.ErrorText("Le password non coincidono.").okay();
            return;
        }
        String utente = UserSession.getInstance().getUtenteLoggato();
        if (DBMSboundary.getInstance().queryDBMSVerificaPassword(utente, p1)) {
            new pkgTextmessage.ErrorText("Attenzione: la nuova password deve essere diversa dalla precedente").okay();
            return;
        }
        DBMSboundary.getInstance().updateDBMSPassword(utente, p1);
        new pkgTextmessage.SuccessfulText("Password aggiornata con successo.").okay();
        nuovaPasswordField.clear();
        confermaPasswordField.clear();
        
        Router.getInstance().navigate("gestione_dati_personali.fxml", "MyStage - Gestione Dati Personali");
    }

    @FXML
    public void annulla(ActionEvent event) {
        Router.getInstance().navigate("gestione_dati_personali.fxml", "MyStage - Gestione Dati Personali");
    }
}
