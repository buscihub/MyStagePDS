package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import pkgBoundary.DBMSboundary;
import pkgTextmessage.ErrorText;
import pkgUtility.Router;
import pkgUtility.UserSession;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CampiFiltriCtrl {

    @FXML
    private TextField carrieraFilterField;

    @FXML
    private TextField anniFilterField;

    @FXML
    public void filtraProfili(ActionEvent event) {
        String carriera = carrieraFilterField.getText();
        String anniStr = anniFilterField.getText();

        if (carriera == null || carriera.trim().isEmpty() || anniStr == null || anniStr.trim().isEmpty()) {
            new ErrorText("Inserire sia la tipologia di carriera che gli anni minimi di esperienza.").okay();
            return;
        }

        int anni = 0;
        try {
            anni = Integer.parseInt(anniStr.trim());
        } catch (NumberFormatException e) {
            new ErrorText("Gli anni di esperienza devono essere un numero intero.").okay();
            return;
        }

        List<String> risultati = new ArrayList<>();
        try {
            ResultSet rs = DBMSboundary.getInstance().queryDBMSFiltraArtisti(carriera.trim(), anni);
            while (rs != null && rs.next()) {
                String cf = rs.getString("codiceFiscale");
                String nome = rs.getString("nome");
                String cognome = rs.getString("cognome");
                String arte = rs.getString("nomeDarte");
                risultati.add(cf + " - " + nome + " " + cognome + " (" + arte + ")");
            }
            
            if (risultati.isEmpty()) {
                new ErrorText("Nessun artista trovato corrispondente ai criteri").okay();
            } else {
                UserSession.getInstance().saveToCache("risultati_ricerca", risultati);
                Router.getInstance().navigate("lista_artisti.fxml", "MyStage - Lista Artisti Trovati");
            }
        } catch (Exception e) {
            e.printStackTrace();
            new ErrorText("Errore durante il filtraggio").okay();
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        Router.getInstance().navigate("cerca_artista.fxml", "MyStage - Cerca Artista");
    }
}
