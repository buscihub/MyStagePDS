package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import pkgUtility.Router;

public class GestioneProfiloCtrl {

    private String getUtenteCorrente() {
        return pkgUtility.UserSession.getInstance().getUtenteLoggato();
    }

    @FXML
    private javafx.scene.control.TableView<pkgEntity.DocumentoEntity> documentiTable;
    @FXML
    private javafx.scene.control.TableColumn<pkgEntity.DocumentoEntity, String> percorsoCol;
    @FXML
    private javafx.scene.control.TableColumn<pkgEntity.DocumentoEntity, Boolean> visibileCol;
    @FXML
    private javafx.scene.control.TableColumn<pkgEntity.DocumentoEntity, Void> azioneCol;

    @FXML
    private javafx.scene.control.Label nomeArteLabel;
    @FXML
    private javafx.scene.image.ImageView avatarImageView;
    @FXML
    private javafx.scene.control.TextField nuovoNomeArteField;
    @FXML
    private javafx.scene.control.PasswordField nuovaPasswordField;
    @FXML
    private javafx.scene.control.PasswordField confermaPasswordField;
    
    @FXML
    private javafx.scene.control.TextField nuovaCarrieraField;
    @FXML
    private javafx.scene.control.TextField anniCarrieraField;
    
    @FXML
    private javafx.scene.control.TableView<pkgEntity.CarrieraEntity> carriereTable;
    @FXML
    private javafx.scene.control.TableColumn<pkgEntity.CarrieraEntity, String> colTipoCarriera;
    @FXML
    private javafx.scene.control.TableColumn<pkgEntity.CarrieraEntity, Integer> colAnniCarriera;
    @FXML
    private javafx.scene.control.TableColumn<pkgEntity.CarrieraEntity, Void> colAzioneCarriera;

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

        colTipoCarriera.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("tipo"));
        colAnniCarriera.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("anniDiCarriera"));
        colAzioneCarriera.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            private final javafx.scene.control.Button btn = new javafx.scene.control.Button("Rimuovi");
            {
                btn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                btn.setOnAction(e -> {
                    pkgEntity.CarrieraEntity car = getTableView().getItems().get(getIndex());
                    pkgBoundary.DBMSboundary.getInstance().removeDBMSCarriereSelezionate(car.getIdCarriera());
                    loadCarriere();
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
        loadCarriere();
    }

    private void loadProfilo() {
        try {
            java.sql.ResultSet rs = pkgBoundary.DBMSboundary.getInstance().queryDBMSProfiloArtista(getUtenteCorrente());
            if (rs != null && rs.next()) {
                String nomeArte = rs.getString("nomeDarte");
                nomeArteLabel.setText(nomeArte != null ? nomeArte : "");
                String urlImmagine = rs.getString("urlImmagineProfilo");
                if (urlImmagine != null && !urlImmagine.isEmpty()) {
                    try {
                        java.io.File file = new java.io.File(urlImmagine);
                        if (file.exists()) {
                            avatarImageView.setImage(new javafx.scene.image.Image(file.toURI().toString()));
                        } else {
                            // If it's just "default.png", load from resources if needed, or leave blank
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCarriere() {
        try {
            carriereTable.getItems().clear();
            java.sql.ResultSet rs = pkgBoundary.DBMSboundary.getInstance().queryDBMSListaCarriere(getUtenteCorrente());
            while (rs != null && rs.next()) {
                pkgEntity.CarrieraEntity car = new pkgEntity.CarrieraEntity(
                    rs.getInt("idCarriera"),
                    rs.getString("codiceFiscaleArtist"),
                    rs.getString("tipo"),
                    rs.getInt("anniDiCarriera")
                );
                carriereTable.getItems().add(car);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDocumenti() {
        try {
            documentiTable.getItems().clear();
            java.sql.ResultSet rs = pkgBoundary.DBMSboundary.getInstance().queryDBMSListaDocumenti(getUtenteCorrente());
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
        pkgUtility.UserSession.getInstance().logout();
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
                pkgBoundary.DBMSboundary.getInstance().queryDBMSInsertDocumenti(getUtenteCorrente(), true, path);
                
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

    @FXML
    public void caricaAvatar(ActionEvent event) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Seleziona Immagine Profilo");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        java.io.File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                java.io.File destDir = new java.io.File("src/main/resources/images");
                if (!destDir.exists()) destDir.mkdirs();
                java.io.File destFile = new java.io.File(destDir, "avatar_" + getUtenteCorrente() + "_" + file.getName());
                java.nio.file.Files.copy(file.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                String path = destFile.getAbsolutePath();
                pkgBoundary.DBMSboundary.getInstance().queryDBMSUpdateImmagineProfilo(path, getUtenteCorrente());
                
                loadProfilo();
                
                new pkgBoundary.SuccessfulText("Immagine profilo aggiornata!").okay();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void rimuoviAvatar(ActionEvent event) {
        try {
            pkgBoundary.DBMSboundary.getInstance().updateDBMSDefaultImmagineProfilo(getUtenteCorrente());
            avatarImageView.setImage(null);
            loadProfilo();
            new pkgBoundary.SuccessfulText("Immagine profilo rimossa!").okay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void cambiaNomeArte(ActionEvent event) {
        String nuovoNome = nuovoNomeArteField.getText();
        if (nuovoNome == null || nuovoNome.trim().isEmpty()) {
            new pkgBoundary.ErrorText("Inserisci un nome d'arte valido.").okay();
            return;
        }
        try {
            boolean giaInUso = pkgBoundary.DBMSboundary.getInstance().queryDBMSVerificaNomeArte(nuovoNome.trim());
            if (giaInUso) {
                new pkgBoundary.ErrorText("Nome d'arte non disponibile.").okay();
            } else {
                pkgBoundary.DBMSboundary.getInstance().updateDBMSNomeArte(getUtenteCorrente(), nuovoNome.trim());
                new pkgBoundary.SuccessfulText("Nome d'arte aggiornato con successo!").okay();
                nuovoNomeArteField.clear();
                loadProfilo();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void cambiaPassword(ActionEvent event) {
        String pwd1 = nuovaPasswordField.getText();
        String pwd2 = confermaPasswordField.getText();
        if (pwd1 == null || pwd1.isEmpty() || pwd2 == null || pwd2.isEmpty()) {
            new pkgBoundary.ErrorText("Inserisci la nuova password in entrambi i campi.").okay();
            return;
        }
        if (!pwd1.equals(pwd2)) {
            new pkgBoundary.ErrorText("Le password non coincidono.").okay();
            return;
        }
        try {
            boolean pwdInUso = pkgBoundary.DBMSboundary.getInstance().queryDBMSVerificaPassword(getUtenteCorrente(), pwd1);
            if (pwdInUso) {
                new pkgBoundary.ErrorText("Attenzione: la nuova password deve essere diversa dalla precedente.").okay();
            } else {
                pkgBoundary.DBMSboundary.getInstance().updateDBMSPassword(getUtenteCorrente(), pwd1);
                new pkgBoundary.SuccessfulText("Password aggiornata con successo!").okay();
                nuovaPasswordField.clear();
                confermaPasswordField.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void aggiungiCarriera(ActionEvent event) {
        String tipo = nuovaCarrieraField.getText();
        String anniStr = anniCarrieraField.getText();
        if (tipo == null || tipo.trim().isEmpty() || anniStr == null || anniStr.trim().isEmpty()) {
            new pkgBoundary.ErrorText("Inserisci tipologia e anni di carriera.").okay();
            return;
        }
        try {
            int anni = Integer.parseInt(anniStr.trim());
            pkgBoundary.DBMSboundary.getInstance().insertDBMSCarriera(getUtenteCorrente(), tipo.trim(), anni);
            new pkgBoundary.SuccessfulText("Carriera aggiunta!").okay();
            nuovaCarrieraField.clear();
            anniCarrieraField.clear();
            loadCarriere();
        } catch (NumberFormatException e) {
            new pkgBoundary.ErrorText("Gli anni devono essere un numero intero.").okay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void cancellaProfilo(ActionEvent event) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancella Profilo");
        alert.setHeaderText("Azione irreversibile");
        alert.setContentText("Vuoi davvero cancellare il tuo profilo e tutti i dati (stanze, documenti) ad esso associati?");
        
        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            try {
                pkgBoundary.DBMSboundary.getInstance().removeDBMSProfiloArtista(getUtenteCorrente());
                new pkgBoundary.SuccessfulText("Profilo cancellato con successo.").okay();
                doLogout(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
