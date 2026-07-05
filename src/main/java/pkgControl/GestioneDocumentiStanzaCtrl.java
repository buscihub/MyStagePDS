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
import pkgEntity.DocumentoEntity;
import pkgEntity.DocumentoStanzaDto;
import pkgUtility.Router;
import pkgUtility.UserSession;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class GestioneDocumentiStanzaCtrl implements Initializable {

    @FXML private TableView<DocumentoEntity> documentiDisponibiliTable;
    @FXML private TableColumn<DocumentoEntity, String> colNomeDisponibile;
    @FXML private TableColumn<DocumentoEntity, Void> colAggiungi;

    @FXML private TableView<DocumentoStanzaDto> documentiStanzaTable;
    @FXML private TableColumn<DocumentoStanzaDto, String> colNomeStanza;
    @FXML private TableColumn<DocumentoStanzaDto, Void> colScaricabile;
    @FXML private TableColumn<DocumentoStanzaDto, Void> colRimuovi;

    @FXML private Label titoloLabel;

    private ObservableList<DocumentoEntity> documentiDisponibiliList = FXCollections.observableArrayList();
    private ObservableList<DocumentoStanzaDto> documentiStanzaList = FXCollections.observableArrayList();

    private Integer idStanzaCorrente;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idStanzaCorrente = UserSession.getInstance().getStanzaSelezionata();
        if (idStanzaCorrente == null) {
            goToStanze(null);
            return;
        }

        colNomeDisponibile.setCellValueFactory(new PropertyValueFactory<>("percorso"));
        colNomeStanza.setCellValueFactory(new PropertyValueFactory<>("percorso"));

        setupDisponibiliColumns();
        setupStanzaColumns();

        caricaDati();
    }

    private void setupDisponibiliColumns() {
        colAggiungi.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Aggiungi");
            {
                btn.setOnAction(event -> {
                    DocumentoEntity doc = getTableView().getItems().get(getIndex());
                    aggiungiDocumento(doc);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void setupStanzaColumns() {
        colScaricabile.setCellFactory(param -> new TableCell<>() {
            private final CheckBox chk = new CheckBox();
            {
                chk.setOnAction(event -> {
                    DocumentoStanzaDto doc = getTableView().getItems().get(getIndex());
                    aggiornaScaricabile(doc, chk.isSelected());
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    DocumentoStanzaDto doc = getTableView().getItems().get(getIndex());
                    chk.setSelected(doc.isScaricabile());
                    setGraphic(chk);
                }
            }
        });

        colRimuovi.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Rimuovi");
            {
                btn.setOnAction(event -> {
                    DocumentoStanzaDto doc = getTableView().getItems().get(getIndex());
                    rimuoviDocumento(doc);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void caricaDati() {
        documentiDisponibiliList.clear();
        documentiStanzaList.clear();
        String cf = UserSession.getInstance().getUtenteLoggato();

        try {
            // Documenti NON in stanza
            ResultSet rsDisp = DBMSboundary.getInstance().queryDocumentiNonInStanza(idStanzaCorrente, cf);
            while (rsDisp != null && rsDisp.next()) {
                documentiDisponibiliList.add(new DocumentoEntity(
                        rsDisp.getInt("idDocumento"),
                        rsDisp.getString("codiceFiscaleArtist"),
                        rsDisp.getBoolean("visibile"),
                        rsDisp.getString("percorso")
                ));
            }
            documentiDisponibiliTable.setItems(documentiDisponibiliList);

            // Documenti IN stanza
            ResultSet rsStanza = DBMSboundary.getInstance().queryDBMSListaDocumentiStanza(idStanzaCorrente);
            while (rsStanza != null && rsStanza.next()) {
                documentiStanzaList.add(new DocumentoStanzaDto(
                        rsStanza.getInt("idDocumento"),
                        rsStanza.getString("codiceFiscaleArtist"),
                        rsStanza.getBoolean("visibile"),
                        rsStanza.getString("percorso"),
                        rsStanza.getBoolean("scaricabile")
                ));
            }
            documentiStanzaTable.setItems(documentiStanzaList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void aggiungiDocumento(DocumentoEntity doc) {
        DBMSboundary.getInstance().insertDocumentiDBMSStanza(idStanzaCorrente, doc.getIdDocumento(), false);
        caricaDati();
    }

    private void rimuoviDocumento(DocumentoStanzaDto doc) {
        DBMSboundary.getInstance().queryDBMSRemoveDocumentiStanza(idStanzaCorrente, doc.getIdDocumento());
        caricaDati();
    }

    private void aggiornaScaricabile(DocumentoStanzaDto doc, boolean scaricabile) {
        DBMSboundary.getInstance().queryDBMSUpdateScaricabiliENonScaricabiliDocumentiStanza(idStanzaCorrente, doc.getIdDocumento(), scaricabile);
        doc.setScaricabile(scaricabile); // update UI model
    }

    @FXML
    public void goToStanze(ActionEvent event) {
        UserSession.getInstance().setStanzaSelezionata(null);
        Router.getInstance().navigate("stanze.fxml", "ShareRoomAfam - Gestione Stanze");
    }
}
