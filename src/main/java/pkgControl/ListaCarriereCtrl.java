package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import pkgBoundary.DBMSboundary;
import pkgTextmessage.ErrorText;
import pkgTextmessage.SuccessfulText;
import pkgUtility.Router;
import pkgUtility.UserSession;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ListaCarriereCtrl implements Initializable {

    @FXML
    private ListView<CheckBox> carriereListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        caricaCarriere();
    }

    private void caricaCarriere() {
        carriereListView.getItems().clear();
        String cf = UserSession.getInstance().getUtenteLoggato();
        try {
            ResultSet rs = DBMSboundary.getInstance().queryDBMSListaCarriere(cf);
            while (rs != null && rs.next()) {
                int idCarriera = rs.getInt("idCarriera");
                String tipologia = rs.getString("tipologia");
                int anni = rs.getInt("anni");

                CheckBox cb = new CheckBox(tipologia + " (" + anni + " anni)");
                cb.setUserData(idCarriera);
                carriereListView.getItems().add(cb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void conferma(ActionEvent event) {
        List<Integer> toRemove = new ArrayList<>();
        for (CheckBox cb : carriereListView.getItems()) {
            if (cb.isSelected()) {
                toRemove.add((Integer) cb.getUserData());
            }
        }

        if (toRemove.isEmpty()) {
            new ErrorText("Nessuna carriera selezionata per la rimozione.").okay();
            return;
        }

        pkgTextmessage.ConfirmText conferma = new pkgTextmessage.ConfirmText(
                "Vuoi davvero rimuovere le carriere selezionate?");
        if (conferma.si()) {
            for (Integer id : toRemove) {
                DBMSboundary.getInstance().removeDBMSCarriereSelezionate(id);
            }
            new SuccessfulText("Carriere rimosse con successo.").okay();
            Router.getInstance().navigate("modifica_carriera.fxml", "MyStage - Modifica Carriera");
        }
    }

    @FXML
    public void annulla(ActionEvent event) {
        Router.getInstance().navigate("modifica_carriera.fxml", "MyStage - Modifica Carriera");
    }
}
