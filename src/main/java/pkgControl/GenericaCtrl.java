package pkgControl;

import pkgBoundary.ResultDto;
import pkgBoundary.ServerBoundary;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import pkgEntity.DocumentoStanzaDto;
import pkgUtility.Router;
import pkgUtility.UserSession;

public class GenericaCtrl {

    @FXML
    public void initialize() {
        try {
            init_VistaScouterCtrl();
        } catch (Exception e) {
            /* ignore */ }
    }

    @FXML
    private Label titoloStanzaLabel;
    @FXML
    private TableView<DocumentoStanzaDto> documentiTable;
    @FXML
    private TableColumn<DocumentoStanzaDto, String> colNomeFile;
    @FXML
    private TableColumn<DocumentoStanzaDto, Void> colAzioni;

    @FXML
    private ImageView imgProfilo;
    @FXML
    private Label nomeLabel;
    @FXML
    private Label dataNascitaLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private VBox carriereContainer;

    private final ObservableList<DocumentoStanzaDto> documentiList = FXCollections.observableArrayList();
    private Integer idStanzaCorrente;

    private void init_VistaScouterCtrl() {
        idStanzaCorrente = UserSession.getInstance().getStanzaSelezionata();
        if (idStanzaCorrente == null) {
            handleEsci(null);
            return;
        }

        colNomeFile.setCellValueFactory(new PropertyValueFactory<>("percorso"));

        setupAzioniColumn();
        caricaDati();
    }

    private void setupAzioniColumn() {
        colAzioni.setCellFactory(param -> new TableCell<>() {
            private final Button btnVisualizza = new Button("Visualizza");
            private final Button btnScarica = new Button("Scarica");
            private final HBox pane = new HBox(10); // Spacing 10

            {
                btnVisualizza.setOnAction(event -> {
                    DocumentoStanzaDto doc = getTableView().getItems().get(getIndex());
                    visualizzaDocumento(doc);
                });
                btnScarica.setOnAction(event -> {
                    DocumentoStanzaDto doc = getTableView().getItems().get(getIndex());
                    scaricaDocumento(doc);
                });

                // Style per i bottoni
                btnVisualizza.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");
                btnScarica.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    DocumentoStanzaDto doc = getTableView().getItems().get(getIndex());
                    pane.getChildren().clear();
                    pane.getChildren().add(btnVisualizza);
                    if (doc.isScaricabile()) {
                        pane.getChildren().add(btnScarica);
                    }
                    setGraphic(pane);
                }
            }
        });
    }

    private void caricaDati() {
        documentiList.clear();
        try {
            // Ottieni info della stanza e dell'artista
            ResultDto rsStanza = ServerBoundary.getInstance().queryDBMSStanzaById(idStanzaCorrente);
            if (rsStanza != null && rsStanza.next()) {
                String nomeStanza = rsStanza.getString("nomeStanza");
                String cfArtista = rsStanza.getString("codiceFiscaleArtist");
                titoloStanzaLabel.setText(nomeStanza);

                ResultDto rsArtista = ServerBoundary.getInstance().queryDBMSProfiloArtista(cfArtista);
                if (rsArtista != null && rsArtista.next()) {
                    String nome = rsArtista.getString("nome");
                    String cognome = rsArtista.getString("cognome");
                    String nomeDarte = rsArtista.getString("nomeDarte");
                    String urlImg = rsArtista.getString("urlImmagineProfilo");
                    String dataNascita = rsArtista.getString("dataDiNascita");
                    String email = rsArtista.getString("email");

                    String nameToDisplay = (nomeDarte != null && !nomeDarte.isEmpty()) ? nomeDarte
                            : nome + " " + cognome;
                    nomeLabel.setText(nameToDisplay);

                    if (dataNascita != null && !dataNascita.isEmpty()) {
                        dataNascitaLabel.setText(dataNascita.split(" ")[0]);
                    } else {
                        dataNascitaLabel.setText("ND");
                    }

                    emailLabel.setText(email != null ? email : "ND");

                    carriereContainer.getChildren().clear();
                    ResultDto rsCarriera = ServerBoundary.getInstance().queryDBMSListaCarriere(cfArtista);
                    while (rsCarriera != null && rsCarriera.next()) {
                        String tipo = rsCarriera.getString("tipologia");
                        int anni = rsCarriera.getInt("anni");
                        Label l = new Label("• " + tipo + " (" + anni + " anni)");
                        l.setStyle("-fx-text-fill: #555555; -fx-font-size: 14px;");
                        carriereContainer.getChildren().add(l);
                    }

                    try {
                        if (urlImg != null && !urlImg.isEmpty()) {
                            Image img = new Image("file:" + urlImg, true);
                            imgProfilo.setImage(img);
                        } else {
                            imgProfilo
                                    .setImage(new Image(getClass().getResourceAsStream("/images/default_profile.png")));
                        }
                    } catch (Exception e) {
                        System.err.println("Immagine profilo non trovata: " + urlImg);
                    }
                }
            }

            ResultDto rs = ServerBoundary.getInstance().queryDBMSListaDocumentiStanza(idStanzaCorrente);
            while (rs != null && rs.next()) {
                documentiList.add(new DocumentoStanzaDto(
                        rs.getInt("idDocumento"),
                        rs.getString("codiceFiscaleArtist"),
                        rs.getBoolean("visibile"),
                        rs.getString("percorso"),
                        rs.getBoolean("scaricabile")));
            }
            documentiTable.setItems(documentiList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void visualizzaDocumento(DocumentoStanzaDto doc) {
        try {
            java.io.File file = new java.io.File(doc.getPercorso());
            if (file.exists()) {
                if (java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop.getDesktop().open(file);
                } else {
                    new pkgTextmessage.ErrorText("Il sistema non supporta l'apertura automatica dei file.").okay();
                }
            } else {
                new pkgTextmessage.ErrorText("File non trovato nel percorso: " + doc.getPercorso()).okay();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new pkgTextmessage.ErrorText("Impossibile aprire the file.").okay();
        }
    }

    private void scaricaDocumento(DocumentoStanzaDto doc) {
        try {
            java.io.File sourceFile = new java.io.File(doc.getPercorso());
            if (!sourceFile.exists()) {
                new pkgTextmessage.ErrorText("File sorgente non trovato.").okay();
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Salva Documento");
            fileChooser.setInitialFileName(sourceFile.getName());

            // Per ottenere la Window corrente, usiamo la tabella come riferimento
            Window window = documentiTable.getScene().getWindow();
            java.io.File destFile = fileChooser.showSaveDialog(window);

            if (destFile != null) {
                java.nio.file.Files.copy(sourceFile.toPath(), destFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                new pkgTextmessage.SuccessfulText("Download completato con successo in:\n" + destFile.getAbsolutePath())
                        .okay();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new pkgTextmessage.ErrorText("Errore durante il download del file.").okay();
        }
    }

    @FXML
    public void handleEsci(ActionEvent event) {
        UserSession.getInstance().setStanzaSelezionata(null);
        Router.getInstance().navigate("login.fxml", "MyStage - Login");
    }
}
