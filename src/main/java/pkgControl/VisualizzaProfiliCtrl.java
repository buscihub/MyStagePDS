package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import pkgUtility.Router;

public class VisualizzaProfiliCtrl {

    @FXML
    public void goToProfilo(ActionEvent event) {
        Router.getInstance().navigate("profilo.fxml", "ShareRoomAfam - Profilo");
    }

    @FXML
    public void cercaProfili(ActionEvent event) {
        new textmessage.ErrorText("Ricerca completata (mock). Nessun risultato trovato.").okay();
    }
}
