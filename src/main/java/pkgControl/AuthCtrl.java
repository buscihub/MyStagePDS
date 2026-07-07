package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import pkgUtility.Router;
import pkgUtility.EmailSender;
import pkgUtility.UserSession;
import pkgTextmessage.ErrorText;
import pkgTextmessage.SuccessfulText;
import pkgBoundary.DBMSboundary;

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
    @FXML private TextField codiceFiscaleField;
    @FXML private TextField nomeDarteField;
    @FXML private TextField carrieraField;
    @FXML private TextField anniCarrieraField;

    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        
        if (email.isEmpty() || password.isEmpty()) {
            new pkgTextmessage.ErrorText("Inserire email e password.").okay();
            return;
        }

        try {
            java.sql.ResultSet rs = DBMSboundary.getInstance().queryDBMSVerificaCredenziali(email, password);
            if (rs != null && rs.next()) {
                // Generazione OTP per il login
                String otp = String.format("%06d", new java.util.Random().nextInt(999999));
                DBMSboundary.getInstance().insertDBMScodice(email, otp);
                boolean sent = EmailSender.inviaCodice2FA(email, otp);
                
                if (sent) {
                    UserSession.getInstance().setEmailInVerifica(email);
                    UserSession.getInstance().setAzioneVerifica("LOGIN");
                    Router.getInstance().navigate("inserisci_codice.fxml", "ShareRoomAfam - Verifica 2FA");
                } else {
                    new ErrorText("Errore durante l'invio dell'email per l'OTP.").okay();
                }
            } else {
                new ErrorText("Credenziali non valide.").okay();
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
        String carriera = carrieraField != null ? carrieraField.getText() : "";
        String anniCarrieraStr = anniCarrieraField != null ? anniCarrieraField.getText() : "0";
        
        String dataNascita = dataNascitaField.getValue() != null ? dataNascitaField.getValue().toString() : "";
        String sesso = sessoField.getValue() != null ? sessoField.getValue() : "ND";
        
        if (email == null || password == null || email.isEmpty() || password.isEmpty() ||
            nome == null || nome.isEmpty() || cognome == null || cognome.isEmpty() ||
            cf == null || cf.isEmpty()) {
            
            new ErrorText("Compilare tutti i campi obbligatori.").okay();
            return;
        }

        int anniCarriera = 0;
        try {
            if (!anniCarrieraStr.isEmpty()) {
                anniCarriera = Integer.parseInt(anniCarrieraStr.trim());
            }
        } catch (NumberFormatException e) {
            new ErrorText("Gli anni di carriera devono essere un numero intero.").okay();
            return;
        }

        try {
            java.sql.ResultSet rs = DBMSboundary.getInstance().queryDBMSVerificaRegistrazione(cf, email);
            if (rs != null && rs.next()) {
                new ErrorText("Account già esistente con questo CF o Email.").okay();
                return;
            }

            int res = DBMSboundary.getInstance().insertDBMSCreaProfilo(nome, cognome, dataNascita, sesso, cf, nomeDarte, carriera, anniCarriera, email, password);
            if (res > 0) {
                new SuccessfulText("Registrazione effettuata con successo!").okay();
                pkgUtility.UserSession.getInstance().clearCache("registrazione_form");
                Router.getInstance().navigate("login.fxml", "ShareRoomAfam - Login");
            } else {
                new ErrorText("Connessione persa o errore DBMS. Dati salvati in cache temporanea.").okay();
                java.util.Map<String, String> formData = new java.util.HashMap<>();
                formData.put("email", email);
                formData.put("password", password);
                formData.put("nome", nome);
                formData.put("cognome", cognome);
                formData.put("cf", cf);
                formData.put("nomeDarte", nomeDarte);
                formData.put("carriera", carriera);
                formData.put("anniCarriera", String.valueOf(anniCarriera));
                formData.put("dataNascita", dataNascita);
                formData.put("sesso", sesso);
                pkgUtility.UserSession.getInstance().saveToCache("registrazione_form", formData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSPID(ActionEvent event) {
        new SuccessfulText("Reindirizzamento all'Identity Provider SPID...").okay();
        
        String cfSpid = "RSSMRA80A01H501U"; // CF mockato dal provider SPID
        try {
            java.sql.ResultSet rs = DBMSboundary.getInstance().queryDBMSVerificaEsistenzaAccountByCF(cfSpid);
            if (rs != null && rs.next()) {
                UserSession.getInstance().setUtenteLoggato(cfSpid);
                Router.getInstance().navigate("profilo.fxml", "ShareRoomAfam - Profilo");
            } else {
                new ErrorText("Il Codice Fiscale fornito dallo SPID (" + cfSpid + ") non è associato ad alcun account registrato.").okay();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRecuperaPassword(ActionEvent event) {
        // RAD rcpr_pswd passo 2: il sistema presenta il modulo RecuperaPasswordForm
        Router.getInstance().navigate("recupera_password.fxml", "ShareRoomAfam - Recupera Password");
    }

    @FXML
    private TextField linkStanzaField;

    @FXML
    public void handleGuestLogin(ActionEvent event) {
        if (linkStanzaField == null) return;
        String link = linkStanzaField.getText();
        if (link == null || link.trim().isEmpty()) {
            new ErrorText("Inserisci un link valido.").okay();
            return;
        }

        try {
            java.sql.ResultSet rs = DBMSboundary.getInstance().queryDBMSStanzaByLink(link);
            if (rs != null && rs.next()) {
                int idStanza = rs.getInt("idStanza");
                UserSession.getInstance().setStanzaSelezionata(idStanza);
                // Registra un visualizzatore anonimo (nome/email lasciati vuoti per accesso da link diretto)
                try {
                    java.sql.ResultSet rsVis = DBMSboundary.getInstance().insertDBMSVisualizzatore("Ospite", "", "");
                    if (rsVis != null && rsVis.next()) {
                        int idVisualizzatore = rsVis.getInt(1);
                        DBMSboundary.getInstance().insertDBMSVisualizzazione(idStanza, idVisualizzatore);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                Router.getInstance().navigate("vista_scouter.fxml", "ShareRoomAfam - Vista Stanza");
            } else {
                new pkgTextmessage.ErrorText("Nessuna stanza trovata con questo link.").okay();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
