package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import pkgBoundary.DBMSboundary;
import pkgUtility.Router;
import pkgUtility.UserSession;

import java.sql.ResultSet;

public class HomeCtrl {

    @FXML
    private Label nomeArteLabel;

    @FXML
    public void initialize() {
        String cf = UserSession.getInstance().getUtenteLoggato();
        if (cf == null) return;
        
        try {
            ResultSet rs = DBMSboundary.getInstance().queryDBMSProfiloArtista(cf);
            if (rs != null && rs.next()) {
                String nomeArte = rs.getString("nomeDarte");
                if (nomeArte != null && !nomeArte.isEmpty()) {
                    nomeArteLabel.setText(nomeArte);
                } else {
                    nomeArteLabel.setText("Nessun nome d'arte");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToGestioneProfilo(ActionEvent event) {
        Router.getInstance().navigate("profilo.fxml", "MyStage - Gestione Profilo");
    }

    @FXML
    public void goToGestioneStanze(ActionEvent event) {
        Router.getInstance().navigate("stanze.fxml", "MyStage - Gestione Stanze");
    }
}
