package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
            ResultSet rs = DBMSboundary.getInstance().queryDBMSListaDocumentiVisibili(cf);
            VBox vbox = new VBox(10);
            vbox.setAlignment(Pos.CENTER);
            boolean found = false;

            while (rs != null && rs.next()) {
                found = true;
                String path = rs.getString("percorso");
                try {
                    File file = new File(path);
                    if (file.exists()) {
                        Image img = new Image(file.toURI().toString());
                        ImageView imgView = new ImageView(img);
                        imgView.setFitWidth(300);
                        imgView.setPreserveRatio(true);
                        vbox.getChildren().add(imgView);
                    } else {
                        vbox.getChildren().add(new Label("File non trovato: " + path));
                    }
                } catch (Exception ex) {
                    vbox.getChildren().add(new Label("Errore caricamento: " + path));
                }
            }

            if (!found) {
                vbox.getChildren().add(new Label("Nessun file presente per questo utente."));
            }

            ScrollPane sp = new ScrollPane(vbox);
            sp.setFitToWidth(true);

            Scene scene = new Scene(sp, 400, 500);
            Stage stage = new Stage();
            stage.setTitle("File dell'utente");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
