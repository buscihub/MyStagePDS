package pkgControl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import pkgBoundary.DBMSboundary;
import pkgEntity.StanzaEntity;
import pkgUtility.Router;
import pkgUtility.UserSession;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.UUID;

public class GestioneStanzeCtrl implements Initializable {

    @FXML private TableView<StanzaEntity> stanzeTable;
    @FXML private TableColumn<StanzaEntity, String> colNome;
    @FXML private TableColumn<StanzaEntity, String> colLink;
    @FXML private TableColumn<StanzaEntity, Void> colAzioni;
    @FXML private TextField nuovoNomeStanzaField;

    private ObservableList<StanzaEntity> stanzeList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nomeStanza"));
        colLink.setCellValueFactory(new PropertyValueFactory<>("linkStanza"));
        
        setupAzioniColumn();
        caricaStanze();
    }

    private void setupAzioniColumn() {
        colAzioni.setCellFactory(param -> new TableCell<>() {
            private final Button btnGestisci = new Button("Gestisci");
            private final Button btnStatistiche = new Button("Statistiche");
            private final Button btnElimina = new Button("Elimina");
            private final HBox pane = new HBox(5, btnGestisci, btnStatistiche, btnElimina);

            {
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
        Router.getInstance().navigate("statistiche_stanza.fxml", "ShareRoomAfam - Statistiche Stanza");
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
            showAlert(Alert.AlertType.ERROR, "Errore", "Inserisci un nome per la stanza.");
            return;
        }

        String cf = UserSession.getInstance().getUtenteLoggato();
        
        // Verifica nome duplicato
        if (DBMSboundary.getInstance().queryDBMSVerificaNomeStanza(cf, nome)) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Hai già una stanza con questo nome.");
            return;
        }

        String link = "shareroom.com/" + UUID.randomUUID().toString().substring(0, 8);
        try {
            ResultSet rs = DBMSboundary.getInstance().insertDBMSStanza(cf, nome, link);
            if (rs != null && rs.next()) {
                showAlert(Alert.AlertType.INFORMATION, "Successo", "Stanza creata! Aggiungi documenti cliccando su 'Gestisci'.");
                nuovoNomeStanzaField.clear();
                caricaStanze();
            } else {
                showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile creare la stanza.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gestisciStanza(StanzaEntity stanza) {
        UserSession.getInstance().setStanzaSelezionata(stanza.getIdStanza());
        Router.getInstance().navigate("gestione_documenti_stanza.fxml", "ShareRoomAfam - Gestisci Documenti Stanza");
    }

    private void eliminaStanza(StanzaEntity stanza) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Vuoi davvero eliminare la stanza " + stanza.getNomeStanza() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            int res = DBMSboundary.getInstance().deleteDBMSStanza(stanza.getIdStanza());
            if (res > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Successo", "Stanza eliminata.");
                caricaStanze();
            } else {
                showAlert(Alert.AlertType.ERROR, "Errore", "Errore durante l'eliminazione.");
            }
        }
    }

    @FXML
    public void goToProfilo(ActionEvent event) {
        Router.getInstance().navigate("profilo.fxml", "ShareRoomAfam - Profilo");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
