package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import pkgUtility.Router;

public class GestioneStanzeCtrl {

    @FXML
    public void goToProfilo(ActionEvent event) {
        Router.getInstance().navigate("profilo.fxml", "ShareRoomAfam - Profilo");
    }

    @FXML
    public void creaNuovaStanza(ActionEvent event) {
        // Logica mock per creare una stanza
        new textmessage.ErrorText("Stanza creata con successo!").okay();
    }
}
