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
    private javafx.scene.control.ComboBox<String> enteSpidCombo;
    @FXML
    private javafx.scene.control.Label providerLabel;

    @FXML
    public void initialize() {
        if (sessoField != null) {
            sessoField.getItems().addAll("M", "F", "ND");
        }
        if (enteSpidCombo != null) {
            enteSpidCombo.getItems().addAll("PosteID", "Aruba ID", "SpidItalia", "Sielte id", "InfoCert ID");
        }
        
        String providerSelezionato = (String) UserSession.getInstance().retrieveFromCache("providerSPID");
        if (providerLabel != null && providerSelezionato != null) {
            providerLabel.setText("Provider selezionato: " + providerSelezionato);
        }
        
        @SuppressWarnings("unchecked")
        java.util.Map<String, String> formData = (java.util.Map<String, String>) pkgUtility.UserSession.getInstance().retrieveFromCache("registrazione_form");
        if (formData != null) {
            if (emailField != null) emailField.setText(formData.get("email"));
            if (nomeField != null) nomeField.setText(formData.get("nome"));
            if (cognomeField != null) cognomeField.setText(formData.get("cognome"));
            if (codiceFiscaleField != null) codiceFiscaleField.setText(formData.get("cf"));
            if (nomeDarteField != null) nomeDarteField.setText(formData.get("nomeDarte"));
            if (carrieraField != null) carrieraField.setText(formData.get("carriera"));
            if (passwordField != null && formData.get("password") != null) passwordField.setText(formData.get("password"));
            if (anniCarrieraField != null) anniCarrieraField.setText(formData.get("anniCarriera"));
            if (dataNascitaField != null && formData.get("dataNascita") != null && !formData.get("dataNascita").isEmpty()) {
                try { dataNascitaField.setValue(java.time.LocalDate.parse(formData.get("dataNascita"))); } catch(Exception e) {}
            }
            if (sessoField != null && formData.get("sesso") != null) sessoField.setValue(formData.get("sesso"));
        }
    }

    private void salvaCacheRegistrazione(String email, String password, String nome, String cognome, String cf, String nomeDarte, String carriera, String anniCarriera, String dataNascita, String sesso) {
        java.util.Map<String, String> formData = new java.util.HashMap<>();
        formData.put("email", email);
        formData.put("password", password);
        formData.put("nome", nome);
        formData.put("cognome", cognome);
        formData.put("cf", cf);
        formData.put("nomeDarte", nomeDarte);
        formData.put("carriera", carriera);
        formData.put("anniCarriera", anniCarriera);
        formData.put("dataNascita", dataNascita);
        formData.put("sesso", sesso);
        pkgUtility.UserSession.getInstance().saveToCache("registrazione_form", formData);
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
                    Router.getInstance().navigate("inserisci_codice.fxml", "MyStage - Verifica 2FA");
                } else {
                    new ErrorText("Errore durante l'invio dell'email per l'OTP.").okay();
                }
            } else {
                new ErrorText("Credenziali errate").okay();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToRegistrazione(ActionEvent event) {
        Router.getInstance().navigate("registrazione.fxml", "MyStage - Registrazione");
    }

    @FXML
    public void goToLogin(ActionEvent event) {
        Router.getInstance().navigate("login.fxml", "MyStage - Login");
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
            
            salvaCacheRegistrazione(email, password, nome, cognome, cf, nomeDarte, carriera, anniCarrieraStr, dataNascita, sesso);
            new ErrorText("Compilare tutti i campi obbligatori.").okay();
            Router.getInstance().navigate("registrazione.fxml", "MyStage - Registrazione");
            return;
        }

        int anniCarriera = 0;
        try {
            if (!anniCarrieraStr.isEmpty()) {
                anniCarriera = Integer.parseInt(anniCarrieraStr.trim());
            }
        } catch (NumberFormatException e) {
            salvaCacheRegistrazione(email, password, nome, cognome, cf, nomeDarte, carriera, anniCarrieraStr, dataNascita, sesso);
            new ErrorText("Gli anni di carriera devono essere un numero intero.").okay();
            Router.getInstance().navigate("registrazione.fxml", "MyStage - Registrazione");
            return;
        }

        try {
            java.sql.ResultSet rs = DBMSboundary.getInstance().queryDBMSVerificaRegistrazione(cf, email);
            if (rs != null && rs.next()) {
                salvaCacheRegistrazione(email, password, nome, cognome, cf, nomeDarte, carriera, String.valueOf(anniCarriera), dataNascita, sesso);
                new ErrorText("Registrazione fallita").okay();
                Router.getInstance().navigate("registrazione.fxml", "MyStage - Registrazione");
                return;
            }

            int res = DBMSboundary.getInstance().insertDBMSCreaProfilo(nome, cognome, dataNascita, sesso, cf, nomeDarte, carriera, anniCarriera, email, password);
            if (res > 0) {
                new SuccessfulText("Registrazione effettuata con successo!").okay();
                pkgUtility.UserSession.getInstance().clearCache("registrazione_form");
                Router.getInstance().navigate("login.fxml", "MyStage - Login");
            } else {
                salvaCacheRegistrazione(email, password, nome, cognome, cf, nomeDarte, carriera, String.valueOf(anniCarriera), dataNascita, sesso);
                new ErrorText("Connessione persa").okay();
                Router.getInstance().navigate("registrazione.fxml", "MyStage - Registrazione");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSPID(ActionEvent event) {
        cliccaEseguiAccessoconSPID(event);
    }

    @FXML
    public void cliccaEseguiAccessoconSPID(ActionEvent event) {
        Router.getInstance().navigate("accesso_con_spid_menu.fxml", "MyStage - Seleziona Provider SPID");
    }

    @FXML
    public void selezionaEnteSPID(ActionEvent event) {
        if (enteSpidCombo == null || enteSpidCombo.getValue() == null) {
            new ErrorText("Selezionare un provider SPID.").okay();
            return;
        }
        String provider = enteSpidCombo.getValue();
        UserSession.getInstance().saveToCache("providerSPID", provider);
        new SuccessfulText("Reindirizzamento al gateway di " + provider + "...").okay();
        Router.getInstance().navigate("accesso_con_spid_form.fxml", "MyStage - Gateway SPID");
    }

    @FXML
    public void cliccaInviaSPID(ActionEvent event) {
        String provider = (String) UserSession.getInstance().retrieveFromCache("providerSPID");
        new SuccessfulText("Autenticazione in corso da parte del provider " + provider + "...").okay();
        
        String cfSpid = "RSSMRA80A01H501U"; // CF mockato dal provider SPID
        try {
            java.sql.ResultSet rs = DBMSboundary.getInstance().queryDBMSVerificaEsistenzaAccountByCF(cfSpid);
            if (rs != null && rs.next()) {
                String email = rs.getString("email");
                String otp = String.format("%06d", new java.util.Random().nextInt(999999));
                DBMSboundary.getInstance().insertDBMScodice(email, otp);
                boolean sent = EmailSender.inviaCodice2FA(email, otp);
                
                if (sent) {
                    UserSession.getInstance().setEmailInVerifica(email);
                    UserSession.getInstance().setAzioneVerifica("LOGIN");
                    Router.getInstance().navigate("inserisci_codice.fxml", "MyStage - Verifica 2FA");
                } else {
                    new ErrorText("Errore durante l'invio dell'email per l'OTP.").okay();
                }
            } else {
                new ErrorText("Il Codice Fiscale fornito dallo SPID (" + cfSpid + ") non è associato ad alcun account registrato. Autenticazione fallita.").okay();
                Router.getInstance().navigate("login.fxml", "MyStage - Login");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRecuperaPassword(ActionEvent event) {
        // RAD rcpr_pswd passo 2: il sistema presenta il modulo RecuperaPasswordForm
        Router.getInstance().navigate("recupera_password.fxml", "MyStage - Recupera Password");
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
                Router.getInstance().navigate("vista_scouter.fxml", "MyStage - Vista Stanza");
            } else {
                new pkgTextmessage.ErrorText("Nessuna stanza trovata con questo link.").okay();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void cercaArtistiGuest(ActionEvent event) {
        UserSession.getInstance().setUtenteLoggato(null); // Assicuriamoci che non ci sia sessione
        Router.getInstance().navigate("cerca_artista.fxml", "MyStage - Ricerca Artisti");
    }
}
