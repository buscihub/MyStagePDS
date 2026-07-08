package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import pkgUtility.Router;

/**
 * Controller principale per la schermata del profilo.
 * Ora funge principalmente da contenitore per la barra superiore di navigazione,
 * mentre il contenuto dei Tab è delegato a GestisciDocumentiCtrl e GestioneDatiPersonaliCtrl.
 */
public class GestioneProfiloCtrl {

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
        pkgTextmessage.ConfirmText conferma = new pkgTextmessage.ConfirmText("Vuoi davvero disconnetterti?");
        if (conferma.si()) {
            pkgUtility.UserSession.getInstance().logout();
            Router.getInstance().navigate("login.fxml", "ShareRoomAfam - Login");
        }
    }
    @FXML
    public void cancellaProfilo(ActionEvent event) {
        pkgTextmessage.ConfirmText conferma = new pkgTextmessage.ConfirmText(
                "Stai per cancellare definitivamente il tuo profilo!\nSei sicuro? Questa operazione non può essere annullata.");
        if (conferma.si()) {
            pkgBoundary.DBMSboundary.getInstance().removeDBMSProfiloArtista(pkgUtility.UserSession.getInstance().getUtenteLoggato());
            new pkgTextmessage.SuccessfulText("Profilo cancellato con successo.").okay();
            pkgUtility.UserSession.getInstance().logout();
            try {
                Router.getInstance().navigate("login.fxml", "ShareRoomAfam - Login");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
