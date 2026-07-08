package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import pkgUtility.Router;

public class ModificaCarrieraCtrl {

    @FXML
    public void goToAggiungiCarriera(ActionEvent event) {
        Router.getInstance().navigate("inserisci_dati_carriera.fxml", "MyStage - Aggiungi Carriera");
    }

    @FXML
    public void goToRimuoviCarriera(ActionEvent event) {
        Router.getInstance().navigate("lista_carriere.fxml", "MyStage - Rimuovi Carriere");
    }

    @FXML
    public void goBack(ActionEvent event) {
        Router.getInstance().navigate("gestione_dati_personali.fxml", "MyStage - Gestione Dati Personali");
    }
}
