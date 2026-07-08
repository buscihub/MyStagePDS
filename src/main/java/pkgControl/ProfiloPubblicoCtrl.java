package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import pkgBoundary.DBMSboundary;
import pkgTextmessage.ErrorText;
import pkgUtility.Router;
import pkgUtility.UserSession;

import java.io.File;
import java.sql.ResultSet;

public class ProfiloPubblicoCtrl {

    @FXML private ImageView avatarImage;
    @FXML private Label nomeArteLabel;
    @FXML private Label nomeLabel;
    @FXML private Label cognomeLabel;
    @FXML private Label sessoLabel;
    @FXML private Label emailLabel;
    @FXML private Label carriereLabel;
    @FXML private ListView<String> documentiPubbliciList;

    @FXML
    public void initialize() {
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
                ResultSet rsDocs = DBMSboundary.getInstance().queryDBMSListaDocumenti(cf);
                while (rsDocs != null && rsDocs.next()) {
                    if (rsDocs.getBoolean("visibile")) {
                        documentiPubbliciList.getItems().add(rsDocs.getString("percorso"));
                    }
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
}
