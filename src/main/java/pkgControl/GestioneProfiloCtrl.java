package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import pkgUtility.Router;

public class GestioneProfiloCtrl {

@FXML
    public void goToHome(ActionEvent event) {
        Router.getInstance().navigate("home.fxml", "MyStage - Home");
    }

    @FXML
    public void goToProfili(ActionEvent event) {
        Router.getInstance().navigate("cerca_artista.fxml", "MyStage - Cerca Profili");
    }

    @FXML
    public void doLogout(ActionEvent event) {
        pkgTextmessage.ConfirmText conferma = new pkgTextmessage.ConfirmText("Vuoi davvero disconnetterti?");
        if (conferma.si()) {
            pkgUtility.UserSession.getInstance().logout();
            Router.getInstance().navigate("login.fxml", "MyStage - Login");
        }
    }
    @FXML
    public void cancellaProfilo(ActionEvent event) {
        pkgTextmessage.ConfirmText conferma = new pkgTextmessage.ConfirmText(
                "Stai per cancellare definitivamente il tuo profilo!\nSei sicuro? Questa operazione non può essere annullata.");
        if (conferma.si()) {
            pkgBoundary.ServerBoundary.getInstance().removeDBMSProfiloArtista(pkgUtility.UserSession.getInstance().getUtenteLoggato());
            new pkgTextmessage.SuccessfulText("Profilo cancellato con successo.").okay();
            pkgUtility.UserSession.getInstance().logout();
            try {
                Router.getInstance().navigate("login.fxml", "MyStage - Login");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void goToGestioneDatiPersonali(ActionEvent event) {
        Router.getInstance().navigate("gestione_dati_personali.fxml", "MyStage - Gestione Dati Personali");
    }

    @FXML
    public void goToGestisciDocumenti(ActionEvent event) {
        Router.getInstance().navigate("gestisci_documenti.fxml", "MyStage - Gestisci Documenti");
    }
}
