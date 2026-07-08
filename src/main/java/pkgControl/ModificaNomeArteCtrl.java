package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import pkgUtility.Router;
import pkgUtility.UserSession;
import pkgBoundary.DBMSboundary;

public class ModificaNomeArteCtrl {

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
    public void annulla(ActionEvent event) {
        Router.getInstance().navigate("gestione_dati_personali.fxml", "MyStage - Gestione Dati Personali");
    }
}
