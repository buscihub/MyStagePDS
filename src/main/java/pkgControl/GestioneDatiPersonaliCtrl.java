package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import pkgUtility.Router;

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
}
