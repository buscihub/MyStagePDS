package pkgControl;

import java.sql.ResultSet;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pkgBoundary.DBMSboundary;
import pkgEntity.StanzaEntity;
import pkgEntity.VisualizzazioneDto;
import pkgTextmessage.SuccessfulText;
import pkgUtility.Router;
import pkgUtility.UserSession;

public class GestioneStanzeCtrl {

    @FXML private TableView<pkgEntity.VisualizzazioneDto> statisticheTable;

    @FXML
    public void initialize() {
        try { init_GestioneStanzeCtrl(); } catch(Exception e) { /* ignore */ }
        try { init_ListaVisualizzatoriCtrl(); } catch(Exception e) { /* ignore */ }
    }

@FXML private TableView<StanzaEntity> stanzeTable;
    @FXML private TableColumn<StanzaEntity, String> colNome;
    @FXML private TableColumn<StanzaEntity, Void> colAzioni;
    @FXML private TextField nuovoNomeStanzaField;

    private ObservableList<StanzaEntity> stanzeList = FXCollections.observableArrayList();

    private void init_GestioneStanzeCtrl() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nomeStanza"));
        
        setupAzioniColumn();
        caricaStanze();
    }

    private void setupAzioniColumn() {
        colAzioni.setCellFactory(param -> new TableCell<>() {
            private final Button btnCondividi = new Button("Condividi");
            private final Button btnGestisci = new Button("Gestisci");
            private final Button btnStatistiche = new Button("Statistiche");
            private final Button btnElimina = new Button("Elimina");
            private final HBox pane = new HBox(5, btnCondividi, btnGestisci, btnStatistiche, btnElimina);

            {
                btnCondividi.setOnAction(event -> {
                    StanzaEntity stanza = getTableView().getItems().get(getIndex());
                    condividiStanza(stanza);
                });
                btnGestisci.setOnAction(event -> {
                    StanzaEntity stanza = getTableView().getItems().get(getIndex());
                    gestisciStanza(stanza);
                });
                btnStatistiche.setOnAction(event -> {
                    StanzaEntity stanza = getTableView().getItems().get(getIndex());
                    vediStatistiche(stanza);
                });
                btnElimina.setOnAction(event -> {
                    StanzaEntity stanza = getTableView().getItems().get(getIndex());
                    eliminaStanza(stanza);
                });
                
                btnCondividi.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
                btnStatistiche.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void vediStatistiche(StanzaEntity stanza) {
        UserSession.getInstance().setStanzaSelezionata(stanza.getIdStanza());
        Router.getInstance().navigate("lista_visualizzatori.fxml", "MyStage - Statistiche Stanza");
    }

    private void caricaStanze() {
        stanzeList.clear();
        String cf = UserSession.getInstance().getUtenteLoggato();
        if (cf == null) return;

        try {
            ResultSet rs = DBMSboundary.getInstance().queryDBMSListaStanze(cf);
            while (rs != null && rs.next()) {
                stanzeList.add(new StanzaEntity(
                        rs.getInt("idStanza"),
                        rs.getString("codiceFiscaleArtist"),
                        rs.getString("nomeStanza"),
                        rs.getString("link")
                ));
            }
            stanzeTable.setItems(stanzeList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void creaNuovaStanza(ActionEvent event) {
        String nome = nuovoNomeStanzaField.getText();
        if (nome == null || nome.trim().isEmpty()) {
            new pkgTextmessage.ErrorText("Inserisci un nome per la stanza.").okay();
            return;
        }

        String cf = UserSession.getInstance().getUtenteLoggato();
        
        if (DBMSboundary.getInstance().queryDBMSVerificaNomeStanza(cf, nome)) {
            new pkgTextmessage.ErrorText("Nome già in uso").okay();
            return;
        }

        // Fetch private documents
        java.util.List<pkgEntity.DocumentoEntity> privateDocs = new java.util.ArrayList<>();
        try {
            ResultSet rsDocs = DBMSboundary.getInstance().queryDBMSListaDocumenti(cf);
            while (rsDocs != null && rsDocs.next()) {
                if (!rsDocs.getBoolean("visibile")) {
                    privateDocs.add(new pkgEntity.DocumentoEntity(
                        rsDocs.getInt("idDocumento"),
                        rsDocs.getString("codiceFiscaleArtist"),
                        rsDocs.getBoolean("visibile"),
                        rsDocs.getString("percorso")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create Dialog for document selection
        Dialog<java.util.List<javafx.util.Pair<Integer, Boolean>>> dialog = new Dialog<>();
        dialog.initOwner(pkgUtility.Router.getInstance().getStage());
        dialog.setTitle("Configura Stanza: " + nome);
        dialog.setHeaderText("Seleziona i documenti privati da includere e i permessi:");

        ButtonType creaButtonType = new ButtonType("Crea Stanza", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(creaButtonType, ButtonType.CANCEL);

        VBox vbox = new VBox(10);
        java.util.Map<CheckBox, ComboBox<String>> docMap = new java.util.HashMap<>();
        
        if (privateDocs.isEmpty()) {
            vbox.getChildren().add(new Label("Nessun documento privato disponibile."));
        } else {
            for (pkgEntity.DocumentoEntity doc : privateDocs) {
                HBox hbox = new HBox(10);
                CheckBox cb = new CheckBox(doc.getPercorso().substring(doc.getPercorso().lastIndexOf('/') + 1));
                cb.setPrefWidth(200);
                cb.setUserData(doc.getIdDocumento());
                
                ComboBox<String> comboPermesso = new ComboBox<>(FXCollections.observableArrayList("Scaricabile", "Solo visualizzazione"));
                comboPermesso.getSelectionModel().select("Solo visualizzazione");
                comboPermesso.setDisable(true); // enabled only if selected
                
                cb.setOnAction(e -> comboPermesso.setDisable(!cb.isSelected()));
                
                docMap.put(cb, comboPermesso);
                hbox.getChildren().addAll(cb, comboPermesso);
                vbox.getChildren().add(hbox);
            }
        }
        
        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(200);
        dialog.getDialogPane().setContent(scrollPane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == creaButtonType) {
                java.util.List<javafx.util.Pair<Integer, Boolean>> selections = new java.util.ArrayList<>();
                for (java.util.Map.Entry<CheckBox, ComboBox<String>> entry : docMap.entrySet()) {
                    if (entry.getKey().isSelected()) {
                        int docId = (Integer) entry.getKey().getUserData();
                        boolean scaricabile = entry.getValue().getValue().equals("Scaricabile");
                        selections.add(new javafx.util.Pair<>(docId, scaricabile));
                    }
                }
                return selections;
            }
            return null;
        });

        java.util.Optional<java.util.List<javafx.util.Pair<Integer, Boolean>>> result = dialog.showAndWait();
        
        result.ifPresent(selections -> {
            String link = "mystage.com/" + UUID.randomUUID().toString().substring(0, 8);
            try {
                ResultSet rs = DBMSboundary.getInstance().insertDBMSStanza(cf, nome, link);
                if (rs != null && rs.next()) {
                    int newStanzaId = rs.getInt(1);
                    
                    for (javafx.util.Pair<Integer, Boolean> sel : selections) {
                        DBMSboundary.getInstance().insertDocumentiDBMSStanza(newStanzaId, sel.getKey(), sel.getValue());
                    }
                    
                    new pkgTextmessage.SuccessfulText("Stanza creata con successo! Il link è: " + link).okay();
                    pkgUtility.UserSession.getInstance().clearCache("creazione_stanza_" + cf);
                    nuovoNomeStanzaField.clear();
                    caricaStanze(); 
                } else {
                    new pkgTextmessage.ErrorText("Connessione persa").okay();
                    java.util.Map<String, Object> stanzaData = new java.util.HashMap<>();
                    stanzaData.put("nome", nome);
                    stanzaData.put("selections", selections);
                    pkgUtility.UserSession.getInstance().saveToCache("creazione_stanza_" + cf, stanzaData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void condividiStanza(StanzaEntity stanza) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(pkgUtility.Router.getInstance().getStage());
        dialog.setTitle("Condividi Stanza");
        dialog.setHeaderText("Ecco il link univoco della tua stanza:");

        TextField linkField = new TextField("http://" + stanza.getLinkStanza());
        linkField.setEditable(false);
        linkField.setPrefWidth(300);

        Button copyBtn = new Button("Copia link");
        copyBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        copyBtn.setOnAction(e -> {
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(linkField.getText());
            clipboard.setContent(content);
            new SuccessfulText("Link copiato negli appunti con successo!").okay();
            dialog.close();
        });

        VBox content = new VBox(10, linkField, copyBtn);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.showAndWait();
    }

    private void gestisciStanza(StanzaEntity stanza) {
        UserSession.getInstance().setStanzaSelezionata(stanza.getIdStanza());
        Router.getInstance().navigate("modifica_stanza.fxml", "MyStage - Gestisci Documenti Stanza");
    }

    private void eliminaStanza(StanzaEntity stanza) {
        pkgTextmessage.ConfirmText confirm = new pkgTextmessage.ConfirmText("Vuoi davvero eliminare la stanza " + stanza.getNomeStanza() + "?");
        if (confirm.si()) {
            int res = DBMSboundary.getInstance().deleteDBMSStanza(stanza.getIdStanza());
            if (res > 0) {
                new pkgTextmessage.SuccessfulText("Stanza eliminata.").okay();
                caricaStanze();
            } else {
                new pkgTextmessage.ErrorText("Errore durante l'eliminazione.").okay();
            }
        }
    }

    @FXML
    public void goToHome(ActionEvent event) {
        Router.getInstance().navigate("home.fxml", "MyStage - Home");
    }

@FXML private Label titoloLabel;
// deleted duplicate FXML annotation
// deleted duplicate line
    @FXML private TableColumn<VisualizzazioneDto, String> colCognome;
    @FXML private TableColumn<VisualizzazioneDto, String> colEmail;
    @FXML private TableColumn<VisualizzazioneDto, String> colData;

    private final ObservableList<VisualizzazioneDto> statisticheList = FXCollections.observableArrayList();
    private Integer idStanzaCorrente;
    private String linkStanza;

    private void init_ListaVisualizzatoriCtrl() {
        if (colNome == null || colCognome == null) return; // Non siamo nella pagina delle statistiche

        idStanzaCorrente = UserSession.getInstance().getStanzaSelezionata();
        if (idStanzaCorrente == null) {
            goToStanze(null);
            return;
        }

        colNome.setCellValueFactory(new PropertyValueFactory<>("nomeVisualizzatore"));
        colCognome.setCellValueFactory(new PropertyValueFactory<>("cognomeVisualizzatore"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("emailVisualizzatore"));
        colData.setCellValueFactory(new PropertyValueFactory<>("dataVisualizzazione"));

        recuperaLinkStanza();
        caricaStatistiche();
    }

    private void recuperaLinkStanza() {
        try {
            ResultSet rs = DBMSboundary.getInstance().queryDBMSLinkStanza(idStanzaCorrente);
            if (rs != null && rs.next()) {
                linkStanza = rs.getString("link");
                titoloLabel.setText("Statistiche Accessi: " + linkStanza);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void caricaStatistiche() {
        if (linkStanza == null) return;
        
        statisticheList.clear();
        try {
            ResultSet rs = DBMSboundary.getInstance().queryDBMSListaVisualizzatori(linkStanza);
            while (rs != null && rs.next()) {
                statisticheList.add(new VisualizzazioneDto(
                        rs.getString("nomeVisualizzatore"),
                        rs.getString("cognomeVisualizzatore"),
                        rs.getString("emailVisualizzatore"),
                        rs.getString("dataVisualizzazione")
                ));
            }
            statisticheTable.setItems(statisticheList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToProfilo(ActionEvent event) {
        Router.getInstance().navigate("profilo.fxml", "MyStage - Profilo");
    }

    @FXML
    public void goToStanze(ActionEvent event) {
        UserSession.getInstance().setStanzaSelezionata(null);
        Router.getInstance().navigate("stanze.fxml", "MyStage - Gestione Stanze");
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        pkgTextmessage.ConfirmText conferma = new pkgTextmessage.ConfirmText("Vuoi davvero disconnetterti?");
        if (conferma.si()) {
            UserSession.getInstance().logout();
            Router.getInstance().navigate("login.fxml", "MyStage - Login");
        }
    }
}
