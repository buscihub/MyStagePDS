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

public class PermessiDocStanzaCtrl {

    @FXML private TableView<DocumentoStanzaDto> documentiStanzaTable;
    @FXML private TableColumn<DocumentoStanzaDto, String> colNomeStanza;
    @FXML private TableColumn<DocumentoStanzaDto, Void> colScaricabile;

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

    private void aggiornaScaricabile(DocumentoStanzaDto doc, boolean scaricabile) {
        DBMSboundary.getInstance().queryDBMSUpdateScaricabiliENonScaricabiliDocumentiStanza(idStanzaCorrente, doc.getIdDocumento(), scaricabile);
        doc.setScaricabile(scaricabile);
    }

    @FXML
    public void goBack(ActionEvent event) {
        Router.getInstance().navigate("modifica_stanza.fxml", "MyStage - Modifica Stanza");
    }
}
