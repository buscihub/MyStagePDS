package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import pkgUtility.Router;

public class GestioneProfiloCtrl {

    @FXML
    public void goToStanze(ActionEvent event) {
        Router.getInstance().navigate("stanze.fxml", "ShareRoomAfam - Stanze");
    }

    @FXML
    public void goToProfili(ActionEvent event) {
        Router.getInstance().navigate("visualizza_profili.fxml", "ShareRoomAfam - Cerca Profili");
    }

    @FXML
    public void doLogout(ActionEvent event) {
        Router.getInstance().navigate("login.fxml", "ShareRoomAfam - Login");
    }

    @FXML
    public void selezionaFile(ActionEvent event) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Seleziona File da Caricare");
        java.io.File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("File Selezionato");
            alert.setHeaderText(null);
            alert.setContentText("File selezionato: " + file.getName());
            alert.showAndWait();
        }
    }
}
