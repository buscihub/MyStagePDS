package pkgControl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import pkgBoundary.DBMSboundary;
import pkgEntity.DocumentoStanzaDto;
import pkgUtility.Router;
import pkgUtility.UserSession;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class VistaScouterCtrl implements Initializable {

    @FXML private Label titoloStanzaLabel;
    @FXML private TableView<DocumentoStanzaDto> documentiTable;
    @FXML private TableColumn<DocumentoStanzaDto, String> colNomeFile;
    @FXML private TableColumn<DocumentoStanzaDto, Void> colAzioni;

    private final ObservableList<DocumentoStanzaDto> documentiList = FXCollections.observableArrayList();
    private Integer idStanzaCorrente;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idStanzaCorrente = UserSession.getInstance().getStanzaSelezionata();
        if (idStanzaCorrente == null) {
            handleEsci(null);
            return;
        }

        colNomeFile.setCellValueFactory(new PropertyValueFactory<>("percorso"));
        
        setupAzioniColumn();
        caricaDati();
    }

    private void setupAzioniColumn() {
        colAzioni.setCellFactory(param -> new TableCell<>() {
            private final Button btnVisualizza = new Button("Visualizza");
            private final Button btnScarica = new Button("Scarica");
            private final HBox pane = new HBox(10); // Spacing 10

            {
                btnVisualizza.setOnAction(event -> {
                    DocumentoStanzaDto doc = getTableView().getItems().get(getIndex());
                    visualizzaDocumento(doc);
                });
                btnScarica.setOnAction(event -> {
                    DocumentoStanzaDto doc = getTableView().getItems().get(getIndex());
                    scaricaDocumento(doc);
                });
                
                // Style per i bottoni
                btnVisualizza.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");
                btnScarica.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    DocumentoStanzaDto doc = getTableView().getItems().get(getIndex());
                    pane.getChildren().clear();
                    pane.getChildren().add(btnVisualizza);
                    if (doc.isScaricabile()) {
                        pane.getChildren().add(btnScarica);
                    }
                    setGraphic(pane);
                }
            }
        });
    }

    private void caricaDati() {
        documentiList.clear();
        try {
            // Ottenere i dettagli della stanza per il titolo
            // Since we don't have a direct query for Stanza by ID that returns ResultSet easily, we can just use "Stanza"
            // Wait, we can do a simple query or just leave a generic title
            titoloStanzaLabel.setText("Contenuto della Stanza Condivisa");

            ResultSet rs = DBMSboundary.getInstance().queryDBMSListaDocumentiStanza(idStanzaCorrente);
            while (rs != null && rs.next()) {
                documentiList.add(new DocumentoStanzaDto(
                        rs.getInt("idDocumento"),
                        rs.getString("codiceFiscaleArtist"),
                        rs.getBoolean("visibile"),
                        rs.getString("percorso"),
                        rs.getBoolean("scaricabile")
                ));
            }
            documentiTable.setItems(documentiList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void visualizzaDocumento(DocumentoStanzaDto doc) {
        try {
            java.io.File file = new java.io.File(doc.getPercorso());
            if (file.exists()) {
                if (java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop.getDesktop().open(file);
                } else {
                    new pkgTextmessage.ErrorText("Il sistema non supporta l'apertura automatica dei file.").okay();
                }
            } else {
                new pkgTextmessage.ErrorText("File non trovato nel percorso: " + doc.getPercorso()).okay();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new pkgTextmessage.ErrorText("Impossibile aprire the file.").okay();
        }
    }

    private void scaricaDocumento(DocumentoStanzaDto doc) {
        try {
            java.io.File sourceFile = new java.io.File(doc.getPercorso());
            if (!sourceFile.exists()) {
                new pkgTextmessage.ErrorText("File sorgente non trovato.").okay();
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Salva Documento");
            fileChooser.setInitialFileName(sourceFile.getName());
            
            // Per ottenere la Window corrente, usiamo la tabella come riferimento
            Window window = documentiTable.getScene().getWindow();
            java.io.File destFile = fileChooser.showSaveDialog(window);

            if (destFile != null) {
                java.nio.file.Files.copy(sourceFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                new pkgTextmessage.SuccessfulText("Download completato con successo in:\n" + destFile.getAbsolutePath()).okay();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new pkgTextmessage.ErrorText("Errore durante il download del file.").okay();
        }
    }

    @FXML
    public void handleEsci(ActionEvent event) {
        UserSession.getInstance().setStanzaSelezionata(null);
        Router.getInstance().navigate("login.fxml", "ShareRoomAfam - Login");
    }
}
