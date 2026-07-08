package pkgTextmessage;

import javafx.scene.control.Alert;
import pkgUtility.Router;

public class ErrorText {
    private Alert alert;

    public ErrorText(String message) {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(Router.getInstance().getStage());
    }

    public void okay() {
        alert.showAndWait();
    }

    public void destroy() {
        if (alert != null) {
            alert.close();
            alert = null;
        }
    }
}
