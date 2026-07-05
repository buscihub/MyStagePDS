package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import pkgUtility.Router;

public class GestioneStanzeCtrl {

    @FXML
    public void goToProfilo(ActionEvent event) {
        Router.getInstance().navigate("profilo.fxml", "ShareRoomAfam - Profilo");
    }

    @FXML
    public void creaNuovaStanza(ActionEvent event) {
        // Logica mock per creare una stanza
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText("Stanza creata con successo!");
        alert.showAndWait();
    }
}
