package pkgControl;

import pkgBoundary.ResultDto;
import pkgBoundary.ServerBoundary;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import pkgEntity.DocumentoEntity;
import pkgEntity.DocumentoStanzaDto;
import pkgTextmessage.ErrorText;
import pkgTextmessage.SuccessfulText;
import pkgUtility.Router;
import pkgUtility.UserSession;

public class ModificaStanzaCtrl {

    @FXML
    public void initialize() {
        try {
            init_AggiungiDocStanzaCtrl();
        } catch (Exception e) {
            /* ignore */ }
        try {
            init_RimuoviDocStanzaCtrl();
        } catch (Exception e) {
            /* ignore */ }
        try {
            init_PermessiDocStanzaCtrl();
        } catch (Exception e) {
            /* ignore */ }
        try {
            init_ModificaNomeStanzaCtrl();
        } catch (Exception e) {
            /* ignore */ }
    }

    @FXML
    public void goToModificaNome(ActionEvent event) {
        pkgUtility.Router.getInstance().navigate("modifica_nome_stanza.fxml", "MyStage - Modifica Nome Stanza");
    }

    @FXML
    public void goToAggiungiDocumenti(ActionEvent event) {
        pkgUtility.Router.getInstance().navigate("aggiungi_doc_stanza.fxml", "MyStage - Aggiungi Documenti Stanza");
    }

    @FXML
    public void goToRimuoviDocumenti(ActionEvent event) {
        pkgUtility.Router.getInstance().navigate("rimuovi_doc_stanza.fxml", "MyStage - Rimuovi Documenti Stanza");
    }

    @FXML
    public void goToPermessiDocumenti(ActionEvent event) {
        pkgUtility.Router.getInstance().navigate("permessi_doc_stanza.fxml", "MyStage - Permessi Documenti Stanza");
    }

    @FXML
    public void goToStanze(ActionEvent event) {
        pkgUtility.UserSession.getInstance().setStanzaSelezionata(null);
        pkgUtility.Router.getInstance().navigate("stanze.fxml", "MyStage - Gestione Stanze");
    }

    @FXML
    private TableView<DocumentoEntity> documentiDisponibiliTable;
    @FXML
    private TableColumn<DocumentoEntity, String> colNomeDisponibile;
    @FXML
    private TableColumn<DocumentoEntity, Void> colAggiungi;

    private ObservableList<DocumentoEntity> documentiDisponibiliList = FXCollections.observableArrayList();
    private Integer idStanzaCorrente;

    @FXML
    private void init_AggiungiDocStanzaCtrl() {
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
            ResultDto rsDisp = ServerBoundary.getInstance().queryDocumentiNonInStanza(idStanzaCorrente, cf);
            while (rsDisp != null && rsDisp.next()) {
                documentiDisponibiliList.add(new DocumentoEntity(
                        rsDisp.getInt("idDocumento"),
                        rsDisp.getString("codiceFiscaleArtist"),
                        rsDisp.getBoolean("visibile"),
                        rsDisp.getString("percorso")));
            }
            documentiDisponibiliTable.setItems(documentiDisponibiliList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void caricaDatiStanza() {
        if (documentiStanzaTable == null)
            return;
        documentiStanzaList.clear();
        try {
            ResultDto rs = ServerBoundary.getInstance().queryDBMSListaDocumentiStanza(idStanzaCorrente);
            while (rs != null && rs.next()) {
                documentiStanzaList.add(new DocumentoStanzaDto(
                        rs.getInt("idDocumento"),
                        rs.getString("codiceFiscaleArtist"),
                        rs.getBoolean("visibile"),
                        rs.getString("percorso"),
                        rs.getBoolean("scaricabile")));
            }
            documentiStanzaTable.setItems(documentiStanzaList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void aggiungiDocumento(DocumentoEntity doc) {
        ServerBoundary.getInstance().insertDocumentiDBMSStanza(idStanzaCorrente, doc.getIdDocumento(), false);
        caricaDati();
    }

    @FXML
    public void goBack(ActionEvent event) {
        Router.getInstance().navigate("modifica_stanza.fxml", "MyStage - Modifica Stanza");
    }

    @FXML
    private TableView<DocumentoStanzaDto> documentiStanzaTable;
    @FXML
    private TableColumn<DocumentoStanzaDto, String> colNomeStanza;
    @FXML
    private TableColumn<DocumentoStanzaDto, Void> colRimuovi;

    private ObservableList<DocumentoStanzaDto> documentiStanzaList = FXCollections.observableArrayList();

    @FXML
    private void init_RimuoviDocStanzaCtrl() {
        idStanzaCorrente = UserSession.getInstance().getStanzaSelezionata();
        if (idStanzaCorrente == null) {
            goBack_RimuoviDocStanzaCtrl(null);
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

        caricaDatiStanza();
    }

    private void rimuoviDocumento(DocumentoStanzaDto doc) {
        pkgTextmessage.ConfirmText conferma = new pkgTextmessage.ConfirmText(
                "Vuoi davvero rimuovere questo documento dalla stanza?");
        if (conferma.si()) {
            ServerBoundary.getInstance().queryDBMSRemoveDocumentiStanza(idStanzaCorrente, doc.getIdDocumento());
            caricaDatiStanza();
        }
    }

    @FXML
    public void goBack_RimuoviDocStanzaCtrl(ActionEvent event) {
        Router.getInstance().navigate("modifica_stanza.fxml", "MyStage - Modifica Stanza");
    }

    // deleted duplicate line
    @FXML
    private TableColumn<DocumentoStanzaDto, Void> colScaricabile;

    // deleted duplicate line

    @FXML
    private void init_PermessiDocStanzaCtrl() {
        idStanzaCorrente = UserSession.getInstance().getStanzaSelezionata();
        if (idStanzaCorrente == null) {
            goBack_PermessiDocStanzaCtrl(null);
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

        caricaDatiStanza();
    }

    private void aggiornaScaricabile(DocumentoStanzaDto doc, boolean scaricabile) {
        ServerBoundary.getInstance().queryDBMSUpdateScaricabiliENonScaricabiliDocumentiStanza(idStanzaCorrente,
                doc.getIdDocumento(), scaricabile);
        doc.setScaricabile(scaricabile);
    }

    @FXML
    public void goBack_PermessiDocStanzaCtrl(ActionEvent event) {
        Router.getInstance().navigate("modifica_stanza.fxml", "MyStage - Modifica Stanza");
    }

    @FXML
    private TextField nuovoNomeStanzaField;

    @FXML
    private void init_ModificaNomeStanzaCtrl() {
        idStanzaCorrente = UserSession.getInstance().getStanzaSelezionata();
    }

    @FXML
    public void rinominaStanza(ActionEvent event) {
        if (idStanzaCorrente == null)
            return;

        String nuovoNome = nuovoNomeStanzaField.getText();
        if (nuovoNome == null || nuovoNome.trim().isEmpty()) {
            new ErrorText("Inserisci un nuovo nome valido").okay();
            return;
        }

        String cf = UserSession.getInstance().getUtenteLoggato();
        if (ServerBoundary.getInstance().queryDBMSVerificaNomeStanza(cf, nuovoNome)) {
            new ErrorText("Hai già una stanza con questo nome.").okay();
            return;
        }

        int res = ServerBoundary.getInstance().updateDBMSNomeStanza(idStanzaCorrente, nuovoNome);
        if (res > 0) {
            new SuccessfulText("Stanza rinominata con successo!").okay();
            nuovoNomeStanzaField.clear();
        } else {
            new ErrorText("Errore durante l'aggiornamento.").okay();
        }
    }

    @FXML
    public void goBack_ModificaNomeStanzaCtrl(ActionEvent event) {
        Router.getInstance().navigate("modifica_stanza.fxml", "MyStage - Modifica Stanza");
    }
}
