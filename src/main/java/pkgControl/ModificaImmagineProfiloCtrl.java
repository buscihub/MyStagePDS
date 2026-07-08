package pkgControl;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import pkgBoundary.ServerBoundary;
import pkgTextmessage.ErrorText;
import pkgTextmessage.SuccessfulText;
import pkgUtility.Router;
import pkgUtility.UserSession;

public class ModificaImmagineProfiloCtrl {

@FXML
    public void caricaAvatar(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona Immagine Profilo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File destFile = fileChooser.showOpenDialog(Router.getInstance().getStage());
        if (destFile != null) {
            try {
                File dir = new File("src/main/resources/images");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String path = "src/main/resources/images/" + destFile.getName();
                java.nio.file.Files.copy(destFile.toPath(), java.nio.file.Paths.get(path), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                ServerBoundary.getInstance().queryDBMSUpdateImmagineProfilo(path, UserSession.getInstance().getUtenteLoggato());
                new SuccessfulText("Immagine aggiornata con successo.").okay();
                Router.getInstance().navigate("gestione_dati_personali.fxml", "MyStage - Gestione Dati Personali");
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
            ServerBoundary.getInstance().queryDBMSUpdateImmagineProfilo("default.png", UserSession.getInstance().getUtenteLoggato());
            new SuccessfulText("Immagine rimossa con successo.").okay();
            Router.getInstance().navigate("gestione_dati_personali.fxml", "MyStage - Gestione Dati Personali");
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        Router.getInstance().navigate("gestione_dati_personali.fxml", "MyStage - Gestione Dati Personali");
    }
}
