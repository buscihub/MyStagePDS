package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ModificaStanzaCtrl {

    @FXML
    public void goToModificaNome(ActionEvent event) {
        pkgUtility.Router.getInstance().navigate("modifica_nome_stanza.fxml", "MyStage - Modifica Nome Stanza");
    }

    @FXML
    public void goToAggiungiDocumenti(ActionEvent event) {
        pkgUtility.Router.getInstance().navigate("aggiungi_doc_stanza.fxml", "MyStage - Aggiungi Documenti Stanza");
    }

    @FXML
    public void goToRimuoviDocumenti(ActionEvent event) {
        pkgUtility.Router.getInstance().navigate("rimuovi_doc_stanza.fxml", "MyStage - Rimuovi Documenti Stanza");
    }

    @FXML
    public void goToPermessiDocumenti(ActionEvent event) {
        pkgUtility.Router.getInstance().navigate("permessi_doc_stanza.fxml", "MyStage - Permessi Documenti Stanza");
    }

    @FXML
    public void goToStanze(ActionEvent event) {
        pkgUtility.UserSession.getInstance().setStanzaSelezionata(null);
        pkgUtility.Router.getInstance().navigate("stanze.fxml", "MyStage - Gestione Stanze");
    }
}
