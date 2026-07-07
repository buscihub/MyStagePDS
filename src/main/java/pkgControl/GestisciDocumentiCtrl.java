package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import pkgEntity.DocumentoEntity;
import pkgUtility.UserSession;
import pkgBoundary.DBMSboundary;

public class GestisciDocumentiCtrl {

    @FXML
    private Label nomeArteLabel;

    @FXML
    private TableView<DocumentoEntity> documentiTable;

    @FXML
    private TableColumn<DocumentoEntity, String> percorsoCol;

    @FXML
    private TableColumn<DocumentoEntity, Boolean> visibileCol;

    @FXML
    private TableColumn<DocumentoEntity, Void> azioneCol;

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

        azioneCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            private final javafx.scene.control.Button btn = new javafx.scene.control.Button("Elimina");
            {
                btn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                btn.setOnAction(e -> {
                    DocumentoEntity doc = getTableView().getItems().get(getIndex());
                    pkgTextmessage.ConfirmText conferma = new pkgTextmessage.ConfirmText("Vuoi davvero eliminare questo documento?");
                    if (conferma.si()) {
                        DBMSboundary.getInstance().queryDBMSRemoveDocumenti(doc.getIdDocumento());
                        loadDocumenti();
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        loadDocumenti();
        loadProfilo();
    }

    private void loadProfilo() {
        try {
            java.sql.ResultSet rs = DBMSboundary.getInstance().queryDBMSProfiloArtista(getUtenteCorrente());
            if (rs != null && rs.next()) {
                String nomeArte = rs.getString("nomeDarte");
                nomeArteLabel.setText(nomeArte != null ? nomeArte : "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void selezionaFile(ActionEvent event) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Seleziona File da Caricare");
        java.util.List<java.io.File> files = fileChooser.showOpenMultipleDialog(null);
        
        if (files != null && !files.isEmpty()) {
            javafx.scene.control.Dialog<java.util.Map<java.io.File, Boolean>> dialog = new javafx.scene.control.Dialog<>();
            dialog.setTitle("Imposta Visibilità Documenti");
            dialog.setHeaderText("Definisci lo stato (visibile/privato) per ciascun documento");

            javafx.scene.control.ButtonType confermaButtonType = new javafx.scene.control.ButtonType("Conferma", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(confermaButtonType, javafx.scene.control.ButtonType.CANCEL);

            javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
            java.util.Map<java.io.File, javafx.scene.control.CheckBox> checkBoxes = new java.util.HashMap<>();
            
            for (java.io.File file : files) {
                javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(10);
                javafx.scene.control.Label nameLabel = new javafx.scene.control.Label(file.getName());
                nameLabel.setPrefWidth(200);
                javafx.scene.control.CheckBox cb = new javafx.scene.control.CheckBox("Pubblico");
                cb.setSelected(true); // Default to visible
                checkBoxes.put(file, cb);
                hbox.getChildren().addAll(nameLabel, cb);
                vbox.getChildren().add(hbox);
            }
            dialog.getDialogPane().setContent(new javafx.scene.control.ScrollPane(vbox));
            
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == confermaButtonType) {
                    java.util.Map<java.io.File, Boolean> result = new java.util.HashMap<>();
                    for (java.util.Map.Entry<java.io.File, javafx.scene.control.CheckBox> entry : checkBoxes.entrySet()) {
                        result.put(entry.getKey(), entry.getValue().isSelected());
                    }
                    return result;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(resultMap -> {
                try {
                    java.io.File destDir = new java.io.File("src/main/resources/images");
                    if (!destDir.exists()) destDir.mkdirs();
                    
                    for (java.util.Map.Entry<java.io.File, Boolean> entry : resultMap.entrySet()) {
                        java.io.File file = entry.getKey();
                        boolean visibile = entry.getValue();
                        java.io.File destFile = new java.io.File(destDir, file.getName());
                        java.nio.file.Files.copy(file.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        
                        String path = "src/main/resources/images/" + file.getName();
                        DBMSboundary.getInstance().queryDBMSInsertDocumenti(getUtenteCorrente(), visibile, path);
                    }
                    
                    loadDocumenti();
                    
                    new pkgTextmessage.SuccessfulText("Documenti caricati con successo!").okay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
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
}
