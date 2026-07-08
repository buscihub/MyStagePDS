package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import pkgBoundary.ResultDto;
import pkgBoundary.ServerBoundary;
import pkgEntity.DocumentoEntity;
import pkgUtility.UserSession;

public class GestisciDocumentiCtrl {

    @FXML
    public void initialize() {
        try { init_EliminaDocumentiCtrl(); } catch(Exception e) { /* ignore */ }
        try { init_CambiaStatoDocumentiCtrl(); } catch(Exception e) { /* ignore */ }
    }

private String getUtenteCorrente() {
        return UserSession.getInstance().getUtenteLoggato();
    }

    @FXML
    public void selezionaFile(ActionEvent event) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Seleziona File da Caricare");
        java.util.List<java.io.File> files = fileChooser.showOpenMultipleDialog(pkgUtility.Router.getInstance().getStage());

        if (files != null && !files.isEmpty()) {
            javafx.scene.control.Dialog<java.util.Map<java.io.File, Boolean>> dialog = new javafx.scene.control.Dialog<>();
            dialog.initOwner(pkgUtility.Router.getInstance().getStage());
            dialog.setTitle("Imposta Visibilità Documenti");
            dialog.setHeaderText("Definisci lo stato (visibile/privato) per ciascun documento");

            javafx.scene.control.ButtonType confermaButtonType = new javafx.scene.control.ButtonType("Conferma",
                    javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
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
                    for (java.util.Map.Entry<java.io.File, javafx.scene.control.CheckBox> entry : checkBoxes
                            .entrySet()) {
                        result.put(entry.getKey(), entry.getValue().isSelected());
                    }
                    return result;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(resultMap -> {
                try {
                    java.io.File destDir = new java.io.File("src/main/resources/images");
                    if (!destDir.exists())
                        destDir.mkdirs();

                    for (java.util.Map.Entry<java.io.File, Boolean> entry : resultMap.entrySet()) {
                        java.io.File file = entry.getKey();
                        boolean visibile = entry.getValue();
                        java.io.File destFile = new java.io.File(destDir, file.getName());
                        java.nio.file.Files.copy(file.toPath(), destFile.toPath(),
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                        String path = "src/main/resources/images/" + file.getName();
                        ServerBoundary.getInstance().queryDBMSInsertDocumenti(getUtenteCorrente(), visibile, path);
                    }

                    new pkgTextmessage.SuccessfulText("Documenti caricati con successo!").okay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @FXML
    public void goToEliminaDocumenti(ActionEvent event) {
        pkgUtility.Router.getInstance().navigate("elimina_documenti.fxml", "MyStage - Elimina Documenti");
    }

    @FXML
    public void goToCambiaStato(ActionEvent event) {
        pkgUtility.Router.getInstance().navigate("cambia_stato_documenti.fxml", "MyStage - Cambia Stato Documenti");
    }

    @FXML
    public void goBack(ActionEvent event) {
        pkgUtility.Router.getInstance().navigate("profilo.fxml", "MyStage - Profilo");
    }

@FXML
    private TableView<DocumentoEntity> documentiTable;

    @FXML
    private TableColumn<DocumentoEntity, String> percorsoCol;

    @FXML
    private TableColumn<DocumentoEntity, Void> selezioneCol;

    private java.util.Set<Integer> selectedDocIds = new java.util.HashSet<>();


    @FXML
    private void init_EliminaDocumentiCtrl() {
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
            ResultDto rs = ServerBoundary.getInstance().queryDBMSListaDocumenti(getUtenteCorrente());
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
                ServerBoundary.getInstance().queryDBMSRemoveDocumenti(id);
            }
            new pkgTextmessage.SuccessfulText("Documenti eliminati con successo.").okay();
            loadDocumenti();
        }
    }

    @FXML
    public void goBack_EliminaDocumentiCtrl(ActionEvent event) {
        pkgUtility.Router.getInstance().navigate("gestisci_documenti.fxml", "MyStage - Gestisci Documenti");
    }


// deleted duplicate FXML annotation
// deleted duplicate line

    @FXML
    private TableColumn<DocumentoEntity, Boolean> visibileCol;


    @FXML
    private void init_CambiaStatoDocumentiCtrl() {
        percorsoCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("percorso"));
        
        visibileCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("visibile"));
        visibileCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            private final javafx.scene.control.CheckBox checkBox = new javafx.scene.control.CheckBox();
            {
                checkBox.setOnAction(e -> {
                    DocumentoEntity doc = getTableView().getItems().get(getIndex());
                    doc.setVisibile(checkBox.isSelected());
                    ServerBoundary.getInstance().queryDBMSUpdateStatoDocumenti(doc.getIdDocumento(), checkBox.isSelected());
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


    @FXML
    public void goBack_CambiaStatoDocumentiCtrl(ActionEvent event) {
        pkgUtility.Router.getInstance().navigate("gestisci_documenti.fxml", "MyStage - Gestisci Documenti");
    }
}
