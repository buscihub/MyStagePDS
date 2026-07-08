package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import pkgUtility.Router;
import pkgBoundary.DBMSboundary;
import pkgTextmessage.ErrorText;
import java.io.File;
import java.sql.ResultSet;

public class VisualizzaProfiliCtrl {

    @FXML
    private TextField searchField;

    @FXML
    private ListView<String> profiliList;

    @FXML
    private TextField carrieraFilterField;

    @FXML
    private TextField anniFilterField;

    @FXML
    private Label nomeArtistaLabel;

    @FXML
    private ListView<String> documentiPubbliciList;

    @FXML
    public void goToProfilo(ActionEvent event) {
        Router.getInstance().navigate("profilo.fxml", "ShareRoomAfam - Profilo");
    }

    @FXML
    public void cercaProfili(ActionEvent event) {
        String keyword = searchField.getText();
        if (keyword == null)
            keyword = "";

        profiliList.getItems().clear();
        try {
            ResultSet rs = DBMSboundary.getInstance().queryDBMSCercaArtista(keyword);
            while (rs != null && rs.next()) {
                String cf = rs.getString("codiceFiscale");
                String nome = rs.getString("nome");
                String cognome = rs.getString("cognome");
                String arte = rs.getString("nomeDarte");
                profiliList.getItems().add(cf + " - " + nome + " " + cognome + " (" + arte + ")");
            }
            if (profiliList.getItems().isEmpty()) {
                new pkgTextmessage.ErrorText("Nessun artista trovato corrispondente ai criteri").okay();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new ErrorText("Errore durante la ricerca").okay();
        }
    }

    @FXML
    public void filtraProfili(ActionEvent event) {
        String carriera = carrieraFilterField.getText();
        String anniStr = anniFilterField.getText();

        if (carriera == null || carriera.trim().isEmpty() || anniStr == null || anniStr.trim().isEmpty()) {
            new pkgTextmessage.ErrorText("Inserire sia la tipologia di carriera che gli anni minimi di esperienza.")
                    .okay();
            return;
        }

        int anni = 0;
        try {
            anni = Integer.parseInt(anniStr.trim());
        } catch (NumberFormatException e) {
            new pkgTextmessage.ErrorText("Gli anni di esperienza devono essere un numero intero.").okay();
            return;
        }

        profiliList.getItems().clear();
        try {
            ResultSet rs = DBMSboundary.getInstance().queryDBMSFiltraArtisti(carriera.trim(), anni);
            while (rs != null && rs.next()) {
                String cf = rs.getString("codiceFiscale");
                String nome = rs.getString("nome");
                String cognome = rs.getString("cognome");
                String arte = rs.getString("nomeDarte");
                profiliList.getItems().add(cf + " - " + nome + " " + cognome + " (" + arte + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
            new ErrorText("Errore durante il filtraggio").okay();
        }
    }

    @FXML
    public void onProfiloClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String selected = profiliList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String cf = selected.split(" - ")[0];
                mostraDocumenti(cf);
            }
        }
    }

    private void mostraDocumenti(String cf) {
        try {
            pkgUtility.UserSession.getInstance().saveToCache("artistaDaVisualizzare", cf);
            Router.getInstance().navigate("profilo_pubblico.fxml", "ShareRoomAfam - Profilo Pubblico");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        String cf = (String) pkgUtility.UserSession.getInstance().retrieveFromCache("artistaDaVisualizzare");
        if (cf != null && nomeArtistaLabel != null && documentiPubbliciList != null) {
            try {
                ResultSet rs = DBMSboundary.getInstance().queryDBMSProfiloArtista(cf);
                if (rs != null && rs.next()) {
                    nomeArtistaLabel.setText(rs.getString("nome") + " " + rs.getString("cognome") + " ("
                            + rs.getString("nomeDarte") + ")");
                }

                ResultSet rsDocs = DBMSboundary.getInstance().queryDBMSListaDocumenti(cf);
                while (rsDocs != null && rsDocs.next()) {
                    if (rsDocs.getBoolean("visibile")) {
                        documentiPubbliciList.getItems().add(rsDocs.getString("percorso"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void apriDocumentoPubblico(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String path = documentiPubbliciList.getSelectionModel().getSelectedItem();
            if (path != null) {
                try {
                    java.awt.Desktop.getDesktop().open(new File(path));
                } catch (Exception e) {
                    new ErrorText("Impossibile aprire il file o file non trovato.").okay();
                }
            }
        }
    }
}
