package pkgControl;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import pkgBoundary.DBMSboundary;
import pkgTextmessage.ErrorText;
import pkgUtility.Router;
import pkgUtility.UserSession;

public class VisualizzaProfiliCtrl {

    @FXML
    public void initialize() {
        try { init_ListaArtistiCtrl(); } catch(Exception e) { /* ignore */ }
        try { init_ProfiloPubblicoCtrl(); } catch(Exception e) { /* ignore */ }
    }

@FXML
    private TextField searchField;

    @FXML
    public void cercaProfili(ActionEvent event) {
        String keyword = searchField.getText();
        if (keyword == null) keyword = "";

        List<String> risultati = new ArrayList<>();
        try {
            ResultSet rs = DBMSboundary.getInstance().queryDBMSCercaArtista(keyword);
            while (rs != null && rs.next()) {
                String cf = rs.getString("codiceFiscale");
                String nome = rs.getString("nome");
                String cognome = rs.getString("cognome");
                String arte = rs.getString("nomeDarte");
                risultati.add(cf + " - " + nome + " " + cognome + " (" + arte + ")");
            }
            
            if (risultati.isEmpty()) {
                new ErrorText("Nessun artista trovato corrispondente ai criteri").okay();
            } else {
                UserSession.getInstance().saveToCache("risultati_ricerca", risultati);
                Router.getInstance().navigate("lista_artisti.fxml", "MyStage - Lista Artisti Trovati");
            }
        } catch (Exception e) {
            e.printStackTrace();
            new ErrorText("Errore durante la ricerca").okay();
        }
    }

    @FXML
    public void goToFiltri(ActionEvent event) {
        Router.getInstance().navigate("campi_filtri.fxml", "MyStage - Filtri Ricerca");
    }

    @FXML
    public void goBack(ActionEvent event) {
        if (UserSession.getInstance().getUtenteLoggato() != null) {
            Router.getInstance().navigate("home.fxml", "MyStage - Home Artista");
        } else {
            Router.getInstance().navigate("login.fxml", "MyStage - Login");
        }
    }

@FXML
    private ListView<String> profiliList;

    @SuppressWarnings("unchecked")
    @FXML
    private void init_ListaArtistiCtrl() {
        List<String> risultati = (List<String>) UserSession.getInstance().retrieveFromCache("risultati_ricerca");
        if (risultati != null) {
            ObservableList<String> items = FXCollections.observableArrayList(risultati);
            profiliList.setItems(items);
            
            // Custom cell per avere il bottone "Visualizza"
            profiliList.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        HBox root = new HBox();
                        Label lbl = new Label(item);
                        Region spacer = new Region();
                        HBox.setHgrow(spacer, Priority.ALWAYS);
                        Button btn = new Button("Visualizza");
                        btn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 2 10 2 10;");
                        btn.setOnAction(e -> mostraDocumenti(item));
                        
                        root.getChildren().addAll(lbl, spacer, btn);
                        setGraphic(root);
                    }
                }
            });
        }
    }

    private void mostraDocumenti(String item) {
        String cf = item.split(" - ")[0];
        try {
            UserSession.getInstance().saveToCache("artistaDaVisualizzare", cf);
            Router.getInstance().navigate("profilo_pubblico.fxml", "MyStage - Profilo Pubblico");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void nuovaRicerca(ActionEvent event) {
        Router.getInstance().navigate("cerca_artista.fxml", "MyStage - Cerca Artista");
    }

@FXML private ImageView avatarImage;
    @FXML private Label nomeArteLabel;
    @FXML private Label nomeLabel;
    @FXML private Label cognomeLabel;
    @FXML private Label sessoLabel;
    @FXML private Label emailLabel;
    @FXML private Label carriereLabel;
    @FXML private ListView<String> documentiPubbliciList;

    @FXML
    private void init_ProfiloPubblicoCtrl() {
        String cf = (String) UserSession.getInstance().retrieveFromCache("artistaDaVisualizzare");
        if (cf != null) {
            try {
                ResultSet rs = DBMSboundary.getInstance().queryDBMSProfiloArtista(cf);
                if (rs != null && rs.next()) {
                    nomeArteLabel.setText(rs.getString("nomeDarte"));
                    nomeLabel.setText(rs.getString("nome"));
                    cognomeLabel.setText(rs.getString("cognome"));
                    sessoLabel.setText(rs.getString("sesso"));
                    emailLabel.setText(rs.getString("email"));
                    
                    String urlImg = rs.getString("urlImmagineProfilo");
                    if (urlImg != null && !urlImg.isEmpty()) {
                        File file = new File(urlImg);
                        if (file.exists()) {
                            avatarImage.setImage(new Image(file.toURI().toString()));
                        }
                    }
                }

                // Carriere
                ResultSet rsCarriere = DBMSboundary.getInstance().queryDBMSListaCarriere(cf);
                StringBuilder carriereStr = new StringBuilder();
                while (rsCarriere != null && rsCarriere.next()) {
                    if (carriereStr.length() > 0) carriereStr.append(", ");
                    carriereStr.append(rsCarriere.getString("tipologia"))
                               .append(" (").append(rsCarriere.getInt("anni")).append(" anni)");
                }
                carriereLabel.setText(carriereStr.toString());

                // Documenti
                ResultSet rsDocs = DBMSboundary.getInstance().queryDBMSListaDocumentiVisibili(cf);
                while (rsDocs != null && rsDocs.next()) {
                    documentiPubbliciList.getItems().add(rsDocs.getString("percorso"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void apriDocumentoPubblico(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String path = documentiPubbliciList.getSelectionModel().getSelectedItem();
            if (path != null) {
                try {
                    java.awt.Desktop.getDesktop().open(new File(path));
                } catch (Exception e) {
                    new ErrorText("Impossibile aprire il file o file non trovato.").okay();
                }
            }
        }
    }

    @FXML
    public void goToRicerca(ActionEvent event) {
        Router.getInstance().navigate("lista_artisti.fxml", "MyStage - Lista Artisti Trovati");
    }

@FXML
    private TextField carrieraFilterField;

    @FXML
    private TextField anniFilterField;

    @FXML
    public void filtraProfili(ActionEvent event) {
        String carriera = carrieraFilterField.getText();
        String anniStr = anniFilterField.getText();

        if (carriera == null || carriera.trim().isEmpty() || anniStr == null || anniStr.trim().isEmpty()) {
            new ErrorText("Inserire sia la tipologia di carriera che gli anni minimi di esperienza.").okay();
            return;
        }

        int anni = 0;
        try {
            anni = Integer.parseInt(anniStr.trim());
        } catch (NumberFormatException e) {
            new ErrorText("Gli anni di esperienza devono essere un numero intero.").okay();
            return;
        }

        List<String> risultati = new ArrayList<>();
        try {
            ResultSet rs = DBMSboundary.getInstance().queryDBMSFiltraArtisti(carriera.trim(), anni);
            while (rs != null && rs.next()) {
                String cf = rs.getString("codiceFiscale");
                String nome = rs.getString("nome");
                String cognome = rs.getString("cognome");
                String arte = rs.getString("nomeDarte");
                risultati.add(cf + " - " + nome + " " + cognome + " (" + arte + ")");
            }
            
            if (risultati.isEmpty()) {
                new ErrorText("Nessun artista trovato corrispondente ai criteri").okay();
            } else {
                UserSession.getInstance().saveToCache("risultati_ricerca", risultati);
                Router.getInstance().navigate("lista_artisti.fxml", "MyStage - Lista Artisti Trovati");
            }
        } catch (Exception e) {
            e.printStackTrace();
            new ErrorText("Errore durante il filtraggio").okay();
        }
    }

    @FXML
    public void goBack_CampiFiltriCtrl(ActionEvent event) {
        Router.getInstance().navigate("cerca_artista.fxml", "MyStage - Cerca Artista");
    }
}
