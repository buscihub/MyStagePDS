package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pkgUtility.UserSession;
import pkgBoundary.DBMSboundary;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class GestioneDatiPersonaliCtrl implements Initializable {

    @FXML
    private TextField nuovoNomeArteField;

    @FXML
    private PasswordField nuovaPasswordField;

    @FXML
    private PasswordField confermaPasswordField;

    private String getUtenteCorrente() {
        return UserSession.getInstance().getUtenteLoggato();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void cambiaNomeArte(ActionEvent event) {
        String nuovoNome = nuovoNomeArteField.getText();
        if (nuovoNome == null || nuovoNome.trim().isEmpty()) {
            new pkgTextmessage.ErrorText("Il nome d'arte non può essere vuoto.").okay();
            return;
        }
        if (DBMSboundary.getInstance().queryDBMSVerificaNomeArte(nuovoNome)) {
            new pkgTextmessage.ErrorText("Nome d'arte non disponibile.").okay();
            return;
        }
        DBMSboundary.getInstance().updateDBMSNomeArte(getUtenteCorrente(), nuovoNome);
        new pkgTextmessage.SuccessfulText("Nome d'arte aggiornato con successo.").okay();
        nuovoNomeArteField.clear();
    }

    @FXML
    public void cambiaPassword(ActionEvent event) {
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
        if (DBMSboundary.getInstance().queryDBMSVerificaPassword(getUtenteCorrente(), p1)) {
            new pkgTextmessage.ErrorText("Attenzione: la nuova password deve essere diversa dalla precedente").okay();
            return;
        }
        DBMSboundary.getInstance().updateDBMSPassword(getUtenteCorrente(), p1);
        new pkgTextmessage.SuccessfulText("Password aggiornata con successo.").okay();
        nuovaPasswordField.clear();
        confermaPasswordField.clear();
    }

}
