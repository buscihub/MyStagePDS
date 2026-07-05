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
    private javafx.scene.control.PasswordField passwordField;

    @FXML
    public void initialize() {
        if (sessoField != null) {
            sessoField.getItems().addAll("M", "F", "ND");
        }
    }

    @FXML
    private TextField nomeField;
    @FXML
    private TextField cognomeField;
    @FXML
    private javafx.scene.control.DatePicker dataNascitaField;
    @FXML
    private javafx.scene.control.ComboBox<String> sessoField;
    @FXML
    private TextField codiceFiscaleField;
    @FXML
    private TextField nomeDarteField;



    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        
        if (email.isEmpty() || password.isEmpty()) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText(null);
            alert.setContentText("Inserire email e password.");
            alert.showAndWait();
            return;
        }

        try {
            java.sql.ResultSet rs = pkgBoundary.DBMSboundary.getInstance().queryDBMSVerificaCredenziali(email, password);
            if (rs != null && rs.next()) {
                String cf = rs.getString("codiceFiscale");
                pkgUtility.UserSession.getInstance().setUtenteLoggato(cf);
                Router.getInstance().navigate("profilo.fxml", "ShareRoomAfam - Profilo");
            } else {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText(null);
                alert.setContentText("Credenziali non valide.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        String nome = nomeField.getText();
        String cognome = cognomeField.getText();
        String cf = codiceFiscaleField.getText();
        String nomeDarte = nomeDarteField.getText();
        
        String dataNascita = dataNascitaField.getValue() != null ? dataNascitaField.getValue().toString() : "";
        String sesso = sessoField.getValue() != null ? sessoField.getValue() : "ND";
        
        if (email == null || password == null || email.isEmpty() || password.isEmpty() ||
            nome == null || nome.isEmpty() || cognome == null || cognome.isEmpty() ||
            cf == null || cf.isEmpty()) {
            
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText(null);
            alert.setContentText("Compilare tutti i campi obbligatori.");
            alert.showAndWait();
            return;
        }

        try {
            java.sql.ResultSet rs = pkgBoundary.DBMSboundary.getInstance().queryDBMSVerificaRegistrazione(cf, email);
            if (rs != null && rs.next()) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText(null);
                alert.setContentText("Account già esistente con questo CF o Email.");
                alert.showAndWait();
                return;
            }

            int res = pkgBoundary.DBMSboundary.getInstance().insertDBMSCreaProfilo(nome, cognome, dataNascita, sesso, cf, nomeDarte, email, password);
            if (res > 0) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Successo");
                alert.setHeaderText(null);
                alert.setContentText("Registrazione effettuata con successo!");
                alert.showAndWait();
                Router.getInstance().navigate("login.fxml", "ShareRoomAfam - Login");
            } else {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText(null);
                alert.setContentText("Errore durante la registrazione.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSPID(ActionEvent event) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("SPID");
        alert.setHeaderText(null);
        alert.setContentText("Reindirizzamento all'Identity Provider SPID...");
        alert.showAndWait();
        
        // Mock: login automatico
        pkgUtility.UserSession.getInstance().setUtenteLoggato("RSSMRA80A01H501U");
        Router.getInstance().navigate("profilo.fxml", "ShareRoomAfam - Profilo");
    }

    @FXML
    public void handleRecuperaPassword(ActionEvent event) {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Recupero Password");
        dialog.setHeaderText("Inserisci la tua email per ricevere l'OTP");
        dialog.setContentText("Email:");

        java.util.Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("OTP Inviato");
            alert.setHeaderText(null);
            alert.setContentText("Un codice OTP è stato inviato a " + result.get() + ".");
            alert.showAndWait();
        }
    }
}
