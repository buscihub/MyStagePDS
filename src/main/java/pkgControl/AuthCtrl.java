package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pkgUtility.Router;
import textmessage.ErrorText;

public class AuthCtrl {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        
        if (email.isEmpty() || password.isEmpty()) {
            new ErrorText("Inserire email e password.").okay();
            return;
        }

        // Simula la validazione col DB
        // boolean esito = DBMSboundary.getInstance().verificaCredenziali(email, password);
        // Per ora passiamo direttamente:
        Router.getInstance().navigate("profilo.fxml", "ShareRoomAfam - Profilo");
    }

    @FXML
    public void goToRegistrazione(ActionEvent event) {
        Router.getInstance().navigate("registrazione.fxml", "ShareRoomAfam - Registrazione");
    }

    @FXML
    public void goToLogin(ActionEvent event) {
        Router.getInstance().navigate("login.fxml", "ShareRoomAfam - Login");
    }

    @FXML
    public void handleRegistrazione(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            new ErrorText("Compilare tutti i campi.").okay();
            return;
        }

        new ErrorText("Registrazione effettuata con successo!").okay();
        Router.getInstance().navigate("login.fxml", "ShareRoomAfam - Login");
    }
}
