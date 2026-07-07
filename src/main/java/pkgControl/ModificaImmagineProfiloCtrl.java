package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import pkgBoundary.DBMSboundary;
import pkgTextmessage.ErrorText;
import pkgTextmessage.SuccessfulText;
import pkgUtility.UserSession;

import java.io.File;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class ModificaImmagineProfiloCtrl implements Initializable {

    // Controller configuration
    @FXML private ImageView avatarImageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        caricaAvatarUtente();
    }

    private void caricaAvatarUtente() {
        String cf = UserSession.getInstance().getUtenteLoggato();
        try {
            ResultSet rs = DBMSboundary.getInstance().queryDBMSProfiloArtista(cf);
            if (rs != null && rs.next()) {
                String path = rs.getString("urlImmagineProfilo");
                if (path != null && !path.equals("default.png")) {
                    File file = new File(path);
                    if (file.exists()) {
                        avatarImageView.setImage(new Image(file.toURI().toString()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void caricaAvatar(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona Immagine Profilo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File destFile = fileChooser.showOpenDialog(null);
        if (destFile != null) {
            try {
                String path = "src/main/resources/images/" + destFile.getName();
                DBMSboundary.getInstance().queryDBMSUpdateImmagineProfilo(path, UserSession.getInstance().getUtenteLoggato());
                caricaAvatarUtente();
                new SuccessfulText("Immagine aggiornata.").okay();
            } catch (Exception e) {
                e.printStackTrace();
                new ErrorText("Errore durante l'aggiornamento dell'immagine.").okay();
            }
        }
    }

    @FXML
    public void rimuoviAvatar(ActionEvent event) {
        pkgTextmessage.ConfirmText conferma = new pkgTextmessage.ConfirmText("Vuoi davvero rimuovere l'immagine del profilo?");
        if (conferma.si()) {
            DBMSboundary.getInstance().queryDBMSUpdateImmagineProfilo("default.png", UserSession.getInstance().getUtenteLoggato());
            avatarImageView.setImage(null);
            new SuccessfulText("Immagine rimossa.").okay();
        }
    }
}
