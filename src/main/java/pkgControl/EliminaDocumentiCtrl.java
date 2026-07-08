package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import pkgEntity.DocumentoEntity;
import pkgUtility.UserSession;
import pkgBoundary.DBMSboundary;

public class EliminaDocumentiCtrl {

    @FXML
    private TableView<DocumentoEntity> documentiTable;

    @FXML
    private TableColumn<DocumentoEntity, String> percorsoCol;

    @FXML
    private TableColumn<DocumentoEntity, Void> selezioneCol;

    private java.util.Set<Integer> selectedDocIds = new java.util.HashSet<>();

    private String getUtenteCorrente() {
        return UserSession.getInstance().getUtenteLoggato();
    }

    @FXML
    public void initialize() {
        percorsoCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("percorso"));
        
        selezioneCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            private final javafx.scene.control.CheckBox cb = new javafx.scene.control.CheckBox();
            {
                cb.setOnAction(e -> {
                    DocumentoEntity doc = getTableView().getItems().get(getIndex());
                    if (cb.isSelected()) {
                        selectedDocIds.add(doc.getIdDocumento());
                    } else {
                        selectedDocIds.remove(doc.getIdDocumento());
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    DocumentoEntity doc = getTableView().getItems().get(getIndex());
                    cb.setSelected(selectedDocIds.contains(doc.getIdDocumento()));
                    setGraphic(cb);
                }
            }
        });

        loadDocumenti();
    }

    private void loadDocumenti() {
        try {
            selectedDocIds.clear();
            documentiTable.getItems().clear();
            java.sql.ResultSet rs = DBMSboundary.getInstance().queryDBMSListaDocumenti(getUtenteCorrente());
            while (rs != null && rs.next()) {
                DocumentoEntity doc = new DocumentoEntity(
                    rs.getInt("idDocumento"),
                    rs.getString("codiceFiscaleArtist"),
                    rs.getBoolean("visibile"),
                    rs.getString("percorso")
                );
                documentiTable.getItems().add(doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void eliminaSelezionati(ActionEvent event) {
        if (selectedDocIds.isEmpty()) {
            new pkgTextmessage.ErrorText("Nessun documento selezionato.").okay();
            return;
        }
        pkgTextmessage.ConfirmText conferma = new pkgTextmessage.ConfirmText("Vuoi davvero eliminare i " + selectedDocIds.size() + " documenti selezionati?");
        if (conferma.si()) {
            for (int id : selectedDocIds) {
                DBMSboundary.getInstance().queryDBMSRemoveDocumenti(id);
            }
            new pkgTextmessage.SuccessfulText("Documenti eliminati con successo.").okay();
            loadDocumenti();
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        pkgUtility.Router.getInstance().navigate("gestisci_documenti.fxml", "MyStage - Gestisci Documenti");
    }
}
