package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import pkgEntity.DocumentoEntity;
import pkgUtility.UserSession;
import pkgBoundary.DBMSboundary;

public class CambiaStatoDocumentiCtrl {

    @FXML
    private TableView<DocumentoEntity> documentiTable;

    @FXML
    private TableColumn<DocumentoEntity, String> percorsoCol;

    @FXML
    private TableColumn<DocumentoEntity, Boolean> visibileCol;

    private String getUtenteCorrente() {
        return UserSession.getInstance().getUtenteLoggato();
    }

    @FXML
    public void initialize() {
        percorsoCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("percorso"));
        
        visibileCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("visibile"));
        visibileCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            private final javafx.scene.control.CheckBox checkBox = new javafx.scene.control.CheckBox();
            {
                checkBox.setOnAction(e -> {
                    DocumentoEntity doc = getTableView().getItems().get(getIndex());
                    doc.setVisibile(checkBox.isSelected());
                    DBMSboundary.getInstance().queryDBMSUpdateStatoDocumenti(doc.getIdDocumento(), checkBox.isSelected());
                });
            }
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item);
                    setGraphic(checkBox);
                }
            }
        });

        loadDocumenti();
    }

    private void loadDocumenti() {
        try {
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
    public void goBack(ActionEvent event) {
        pkgUtility.Router.getInstance().navigate("gestisci_documenti.fxml", "MyStage - Gestisci Documenti");
    }
}
