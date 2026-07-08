package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pkgBoundary.DBMSboundary;
import pkgUtility.Router;
import pkgUtility.UserSession;

public class GestioneDatiPersonaliCtrl {

@FXML
    public void goToModificaImmagine(ActionEvent event) {
        Router.getInstance().navigate("modifica_immagine_profilo.fxml", "MyStage - Modifica Immagine Profilo");
    }

    @FXML
    public void goToModificaPassword(ActionEvent event) {
        Router.getInstance().navigate("modifica_password.fxml", "MyStage - Cambia Password");
    }

    @FXML
    public void goToModificaNomeArte(ActionEvent event) {
        Router.getInstance().navigate("modifica_nome_arte.fxml", "MyStage - Modifica Nome d'Arte");
    }

    @FXML
    public void goToModificaCarriera(ActionEvent event) {
        Router.getInstance().navigate("modifica_carriera.fxml", "MyStage - Modifica Carriera");
    }

    @FXML
    public void goBack(ActionEvent event) {
        Router.getInstance().navigate("profilo.fxml", "MyStage - Profilo");
    }

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

@FXML
    private TextField nuovoNomeArteField;

    @FXML
    public void salva(ActionEvent event) {
        String nuovoNome = nuovoNomeArteField.getText();
        if (nuovoNome == null || nuovoNome.trim().isEmpty()) {
            new pkgTextmessage.ErrorText("Il nome d'arte non può essere vuoto.").okay();
            return;
        }
        if (DBMSboundary.getInstance().queryDBMSVerificaNomeArte(nuovoNome)) {
            new pkgTextmessage.ErrorText("Nome d'arte non disponibile.").okay();
            return;
        }
        String utente = UserSession.getInstance().getUtenteLoggato();
        DBMSboundary.getInstance().updateDBMSNomeArte(utente, nuovoNome);
        new pkgTextmessage.SuccessfulText("Nome d'arte aggiornato con successo.").okay();
        nuovoNomeArteField.clear();
        
        Router.getInstance().navigate("gestione_dati_personali.fxml", "MyStage - Gestione Dati Personali");
    }

    @FXML
    public void annulla_ModificaNomeArteCtrl(ActionEvent event) {
        Router.getInstance().navigate("gestione_dati_personali.fxml", "MyStage - Gestione Dati Personali");
    }
}
