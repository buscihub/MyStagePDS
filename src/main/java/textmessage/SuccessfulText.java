package textmessage;

import javafx.scene.control.Alert;

public class SuccessfulText {
    private Alert alert;

    public SuccessfulText(String message) {
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText(message);
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
