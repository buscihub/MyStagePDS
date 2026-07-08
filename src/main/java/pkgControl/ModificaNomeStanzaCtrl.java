package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import pkgBoundary.DBMSboundary;
import pkgUtility.Router;
import pkgUtility.UserSession;
import pkgTextmessage.ErrorText;
import pkgTextmessage.SuccessfulText;

public class ModificaNomeStanzaCtrl {

    @FXML
    private TextField nuovoNomeStanzaField;

    private Integer idStanzaCorrente;

    @FXML
    public void initialize() {
        idStanzaCorrente = UserSession.getInstance().getStanzaSelezionata();
    }

    @FXML
    public void rinominaStanza(ActionEvent event) {
        if (idStanzaCorrente == null) return;
        
        String nuovoNome = nuovoNomeStanzaField.getText();
        if (nuovoNome == null || nuovoNome.trim().isEmpty()) {
            new ErrorText("Inserisci un nuovo nome valido").okay();
            return;
        }
        
        String cf = UserSession.getInstance().getUtenteLoggato();
        if (DBMSboundary.getInstance().queryDBMSVerificaNomeStanza(cf, nuovoNome)) {
            new ErrorText("Hai già una stanza con questo nome.").okay();
            return;
        }
        
        int res = DBMSboundary.getInstance().updateDBMSNomeStanza(idStanzaCorrente, nuovoNome);
        if (res > 0) {
            new SuccessfulText("Stanza rinominata con successo!").okay();
            nuovoNomeStanzaField.clear();
        } else {
            new ErrorText("Errore durante l'aggiornamento.").okay();
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        Router.getInstance().navigate("modifica_stanza.fxml", "MyStage - Modifica Stanza");
    }
}
