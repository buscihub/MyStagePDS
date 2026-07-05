package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import pkgUtility.Router;
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
                new pkgBoundary.ErrorText("Account già esistente con questo CF o Email.").okay();
                return;
            }

            // Generate OTP for registration
            String otp = String.format("%06d", new java.util.Random().nextInt(999999));
            boolean sent = pkgUtility.EmailSender.inviaCodice2FA(email, otp);
            if (sent) {
                javafx.scene.control.TextInputDialog otpDialog = new javafx.scene.control.TextInputDialog();
                otpDialog.setTitle("Verifica OTP");
                otpDialog.setHeaderText("Abbiamo inviato un codice a " + email);
                otpDialog.setContentText("Codice OTP:");
                
                java.util.Optional<String> otpResult = otpDialog.showAndWait();
                if (otpResult.isPresent() && otpResult.get().equals(otp)) {
                    int res = pkgBoundary.DBMSboundary.getInstance().insertDBMSCreaProfilo(nome, cognome, dataNascita, sesso, cf, nomeDarte, email, password);
                    if (res > 0) {
                        new pkgBoundary.SuccessfulText("Registrazione effettuata con successo!").okay();
                        Router.getInstance().navigate("login.fxml", "ShareRoomAfam - Login");
                    } else {
                        new pkgBoundary.ErrorText("Errore durante la registrazione nel database.").okay();
                    }
                } else {
                    new pkgBoundary.ErrorText("Codice OTP errato. Registrazione annullata.").okay();
                }
            } else {
                new pkgBoundary.ErrorText("Errore durante l'invio dell'email per l'OTP.").okay();
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
            String email = result.get();
            try {
                java.sql.ResultSet rs = pkgBoundary.DBMSboundary.getInstance().queryDBMSVerificaEmail(email);
                if (rs != null && rs.next()) {
                    // Generate OTP
                    String otp = String.format("%06d", new java.util.Random().nextInt(999999));
                    pkgBoundary.DBMSboundary.getInstance().insertDBMScodice(email, otp);
                    
                    boolean sent = pkgUtility.EmailSender.inviaCodice2FA(email, otp);
                    if (sent) {
                        javafx.scene.control.TextInputDialog otpDialog = new javafx.scene.control.TextInputDialog();
                        otpDialog.setTitle("Verifica OTP");
                        otpDialog.setHeaderText("Abbiamo inviato un codice a " + email);
                        otpDialog.setContentText("Codice OTP:");
                        
                        java.util.Optional<String> otpResult = otpDialog.showAndWait();
                        if (otpResult.isPresent() && otpResult.get().equals(otp)) {
                            javafx.scene.control.TextInputDialog newPwdDialog = new javafx.scene.control.TextInputDialog();
                            newPwdDialog.setTitle("Nuova Password");
                            newPwdDialog.setHeaderText("Inserisci la nuova password");
                            newPwdDialog.setContentText("Password:");
                            
                            java.util.Optional<String> pwdResult = newPwdDialog.showAndWait();
                            if (pwdResult.isPresent() && !pwdResult.get().isEmpty()) {
                                String cf = rs.getString("codiceFiscale");
                                pkgBoundary.DBMSboundary.getInstance().updateDBMSPassword(cf, pwdResult.get());
                                new pkgBoundary.SuccessfulText("Password aggiornata con successo!").okay();
                            }
                        } else {
                            new pkgBoundary.ErrorText("Codice OTP errato.").okay();
                        }
                    } else {
                        new pkgBoundary.ErrorText("Errore durante l'invio dell'email.").okay();
                    }
                } else {
                    new pkgBoundary.ErrorText("Email non trovata nel sistema.").okay();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private TextField linkStanzaField;

    @FXML
    public void handleGuestLogin(ActionEvent event) {
        if (linkStanzaField == null) return;
        String link = linkStanzaField.getText();
        if (link == null || link.trim().isEmpty()) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText(null);
            alert.setContentText("Inserisci un link valido.");
            alert.showAndWait();
            return;
        }

        try {
            java.sql.ResultSet rs = pkgBoundary.DBMSboundary.getInstance().queryDBMSStanzaByLink(link);
            if (rs != null && rs.next()) {
                int idStanza = rs.getInt("idStanza");
                
                // Richiedi i dati allo Scouter
                javafx.scene.control.Dialog<javafx.util.Pair<String, String>> dialog = new javafx.scene.control.Dialog<>();
                dialog.setTitle("Dati Ospite");
                dialog.setHeaderText("Inserisci i tuoi dati per accedere alla stanza");

                javafx.scene.control.ButtonType loginButtonType = new javafx.scene.control.ButtonType("Entra", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, javafx.scene.control.ButtonType.CANCEL);

                javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                
                TextField nomeGuest = new TextField();
                nomeGuest.setPromptText("Nome");
                TextField cognomeGuest = new TextField();
                cognomeGuest.setPromptText("Cognome");
                TextField emailGuest = new TextField();
                emailGuest.setPromptText("Email");

                grid.add(new javafx.scene.control.Label("Nome:"), 0, 0);
                grid.add(nomeGuest, 1, 0);
                grid.add(new javafx.scene.control.Label("Cognome:"), 0, 1);
                grid.add(cognomeGuest, 1, 1);
                grid.add(new javafx.scene.control.Label("Email:"), 0, 2);
                grid.add(emailGuest, 1, 2);

                dialog.getDialogPane().setContent(grid);

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == loginButtonType) {
                        return new javafx.util.Pair<>(nomeGuest.getText(), emailGuest.getText());
                    }
                    return null;
                });

                java.util.Optional<javafx.util.Pair<String, String>> result = dialog.showAndWait();

                result.ifPresent(dati -> {
                    String nome = dati.getKey();
                    String email = dati.getValue();
                    String cognome = cognomeGuest.getText();

                    if (!nome.isEmpty() && !email.isEmpty()) {
                        try {
                            java.sql.ResultSet rsVis = pkgBoundary.DBMSboundary.getInstance().insertDBMSVisualizzatore(nome, cognome, email);
                            if (rsVis != null && rsVis.next()) {
                                int idVisualizzatore = rsVis.getInt(1);
                                pkgBoundary.DBMSboundary.getInstance().insertDBMSVisualizzazione(idStanza, idVisualizzatore);
                            }
                            
                            pkgUtility.UserSession.getInstance().setStanzaSelezionata(idStanza);
                            Router.getInstance().navigate("vista_scouter.fxml", "ShareRoomAfam - Vista Stanza");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        javafx.scene.control.Alert alertInfo = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                        alertInfo.setTitle("Dati incompleti");
                        alertInfo.setHeaderText(null);
                        alertInfo.setContentText("Devi inserire Nome e Email per accedere.");
                        alertInfo.showAndWait();
                    }
                });
            } else {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText(null);
                alert.setContentText("Nessuna stanza trovata con questo link.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
