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

public class CercaArtistaCtrl {

    @FXML
    private TextField searchField;

    @FXML
    public void cercaProfili(ActionEvent event) {
        String keyword = searchField.getText();
        if (keyword == null) keyword = "";

        List<String> risultati = new ArrayList<>();
        try {
            ResultSet rs = DBMSboundary.getInstance().queryDBMSCercaArtista(keyword);
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
            new ErrorText("Errore durante la ricerca").okay();
        }
    }

    @FXML
    public void goToFiltri(ActionEvent event) {
        Router.getInstance().navigate("campi_filtri.fxml", "MyStage - Filtri Ricerca");
    }

    @FXML
    public void goBack(ActionEvent event) {
        if (UserSession.getInstance().getUtenteLoggato() != null) {
            Router.getInstance().navigate("home.fxml", "MyStage - Home Artista");
        } else {
            Router.getInstance().navigate("login.fxml", "MyStage - Login");
        }
    }
}
