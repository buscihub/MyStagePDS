package pkgControl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pkgBoundary.DBMSboundary;
import pkgEntity.DocumentoStanzaDto;
import pkgUtility.Router;
import pkgUtility.UserSession;

import java.sql.ResultSet;

public class RimuoviDocStanzaCtrl {

    @FXML private TableView<DocumentoStanzaDto> documentiStanzaTable;
    @FXML private TableColumn<DocumentoStanzaDto, String> colNomeStanza;
    @FXML private TableColumn<DocumentoStanzaDto, Void> colRimuovi;

    private ObservableList<DocumentoStanzaDto> documentiStanzaList = FXCollections.observableArrayList();
    private Integer idStanzaCorrente;

    @FXML
    public void initialize() {
        idStanzaCorrente = UserSession.getInstance().getStanzaSelezionata();
        if (idStanzaCorrente == null) {
            goBack(null);
            return;
        }

        colNomeStanza.setCellValueFactory(new PropertyValueFactory<>("percorso"));
        
        colRimuovi.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Rimuovi");
            {
                btn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
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

        caricaDati();
    }

    private void caricaDati() {
        documentiStanzaList.clear();

        try {
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

    private void rimuoviDocumento(DocumentoStanzaDto doc) {
        pkgTextmessage.ConfirmText conferma = new pkgTextmessage.ConfirmText("Vuoi davvero rimuovere questo documento dalla stanza?");
        if (conferma.si()) {
            DBMSboundary.getInstance().queryDBMSRemoveDocumentiStanza(idStanzaCorrente, doc.getIdDocumento());
            caricaDati();
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        Router.getInstance().navigate("modifica_stanza.fxml", "MyStage - Modifica Stanza");
    }
}
