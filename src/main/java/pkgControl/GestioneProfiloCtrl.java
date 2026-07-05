package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import pkgUtility.Router;

public class GestioneProfiloCtrl {

    private final String UTENTE_ATTIVO_MOCK = "RSSMRA80A01H501U";

    @FXML
    private javafx.scene.control.TableView<pkgEntity.DocumentoEntity> documentiTable;
    @FXML
    private javafx.scene.control.TableColumn<pkgEntity.DocumentoEntity, String> percorsoCol;
    @FXML
    private javafx.scene.control.TableColumn<pkgEntity.DocumentoEntity, Boolean> visibileCol;
    @FXML
    private javafx.scene.control.TableColumn<pkgEntity.DocumentoEntity, Void> azioneCol;

    @FXML
    public void initialize() {
        percorsoCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("percorso"));
        
        visibileCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("visibile"));
        visibileCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            private final javafx.scene.control.CheckBox checkBox = new javafx.scene.control.CheckBox();
            {
                checkBox.setOnAction(e -> {
                    pkgEntity.DocumentoEntity doc = getTableView().getItems().get(getIndex());
                    doc.setVisibile(checkBox.isSelected());
                    pkgBoundary.DBMSboundary.getInstance().queryDBMSUpdateStatoDocumenti(doc.getIdDocumento(), checkBox.isSelected());
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

        azioneCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            private final javafx.scene.control.Button btn = new javafx.scene.control.Button("Elimina");
            {
                btn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                btn.setOnAction(e -> {
                    pkgEntity.DocumentoEntity doc = getTableView().getItems().get(getIndex());
                    pkgBoundary.DBMSboundary.getInstance().queryDBMSRemoveDocumenti(doc.getIdDocumento());
                    loadDocumenti();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        loadDocumenti();
    }

    private void loadDocumenti() {
        try {
            documentiTable.getItems().clear();
            java.sql.ResultSet rs = pkgBoundary.DBMSboundary.getInstance().queryDBMSListaDocumenti(UTENTE_ATTIVO_MOCK);
            while (rs != null && rs.next()) {
                pkgEntity.DocumentoEntity doc = new pkgEntity.DocumentoEntity(
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
    public void goToStanze(ActionEvent event) {
        Router.getInstance().navigate("stanze.fxml", "ShareRoomAfam - Stanze");
    }

    @FXML
    public void goToProfili(ActionEvent event) {
        Router.getInstance().navigate("visualizza_profili.fxml", "ShareRoomAfam - Cerca Profili");
    }

    @FXML
    public void doLogout(ActionEvent event) {
        Router.getInstance().navigate("login.fxml", "ShareRoomAfam - Login");
    }

    @FXML
    public void selezionaFile(ActionEvent event) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Seleziona File da Caricare");
        java.io.File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                // Copia file in src/main/resources/images
                java.io.File destDir = new java.io.File("src/main/resources/images");
                if (!destDir.exists()) destDir.mkdirs();
                java.io.File destFile = new java.io.File(destDir, file.getName());
                java.nio.file.Files.copy(file.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                String path = "src/main/resources/images/" + file.getName();
                pkgBoundary.DBMSboundary.getInstance().queryDBMSInsertDocumenti(UTENTE_ATTIVO_MOCK, true, path);
                
                loadDocumenti();
                
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Successo");
                alert.setHeaderText(null);
                alert.setContentText("Documento caricato con successo!");
                alert.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
