package pkgControl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pkgBoundary.DBMSboundary;
import pkgEntity.DocumentoEntity;
import pkgUtility.Router;
import pkgUtility.UserSession;

import java.sql.ResultSet;

public class AggiungiDocStanzaCtrl {

    @FXML private TableView<DocumentoEntity> documentiDisponibiliTable;
    @FXML private TableColumn<DocumentoEntity, String> colNomeDisponibile;
    @FXML private TableColumn<DocumentoEntity, Void> colAggiungi;

    private ObservableList<DocumentoEntity> documentiDisponibiliList = FXCollections.observableArrayList();
    private Integer idStanzaCorrente;

    @FXML
    public void initialize() {
        idStanzaCorrente = UserSession.getInstance().getStanzaSelezionata();
        if (idStanzaCorrente == null) {
            goBack(null);
            return;
        }

        colNomeDisponibile.setCellValueFactory(new PropertyValueFactory<>("percorso"));
        
        colAggiungi.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Aggiungi");
            {
                btn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
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

        caricaDati();
    }

    private void caricaDati() {
        documentiDisponibiliList.clear();
        String cf = UserSession.getInstance().getUtenteLoggato();

        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void aggiungiDocumento(DocumentoEntity doc) {
        DBMSboundary.getInstance().insertDocumentiDBMSStanza(idStanzaCorrente, doc.getIdDocumento(), false);
        caricaDati();
    }

    @FXML
    public void goBack(ActionEvent event) {
        Router.getInstance().navigate("modifica_stanza.fxml", "MyStage - Modifica Stanza");
    }
}
