package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import pkgBoundary.DBMSboundary;
import pkgTextmessage.ErrorText;
import pkgTextmessage.SuccessfulText;
import pkgUtility.Router;
import pkgUtility.UserSession;

public class InserisciDatiCarrieraCtrl {

    @FXML private TextField nuovaCarrieraField;
    @FXML private TextField anniCarrieraField;

    @FXML
    public void salvaModifiche(ActionEvent event) {
        String tipo = nuovaCarrieraField.getText();
        String anniStr = anniCarrieraField.getText();
        
        if (tipo == null || tipo.trim().isEmpty() || anniStr == null || anniStr.trim().isEmpty()) {
            new ErrorText("Inserire sia la tipologia che gli anni di esperienza.").okay();
            return;
        }
        
        int anni = 0;
        try {
            anni = Integer.parseInt(anniStr.trim());
        } catch (NumberFormatException e) {
            new ErrorText("Gli anni devono essere un numero intero.").okay();
            return;
        }
        
        String cf = UserSession.getInstance().getUtenteLoggato();
        int res = DBMSboundary.getInstance().insertDBMSCarriera(cf, tipo, anni);
        if (res > 0) {
            new SuccessfulText("Carriera aggiunta con successo!").okay();
            nuovaCarrieraField.clear();
            anniCarrieraField.clear();
            Router.getInstance().navigate("modifica_carriera.fxml", "MyStage - Modifica Carriera");
        } else {
            new ErrorText("Errore durante l'aggiunta della carriera.").okay();
        }
    }

    @FXML
    public void annulla(ActionEvent event) {
        Router.getInstance().navigate("modifica_carriera.fxml", "MyStage - Modifica Carriera");
    }
}
