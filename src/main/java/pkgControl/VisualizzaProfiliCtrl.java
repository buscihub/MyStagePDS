package pkgControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import pkgUtility.Router;

public class VisualizzaProfiliCtrl {

    @FXML
    private javafx.scene.control.TextField searchField;

    @FXML
    private javafx.scene.control.ListView<String> profiliList;

    @FXML
    public void goToProfilo(ActionEvent event) {
        Router.getInstance().navigate("profilo.fxml", "ShareRoomAfam - Profilo");
    }

    @FXML
    public void cercaProfili(ActionEvent event) {
        String keyword = searchField.getText();
        if (keyword == null) keyword = "";
        
        profiliList.getItems().clear();
        try {
            java.sql.ResultSet rs = pkgBoundary.DBMSboundary.getInstance().queryDBMSCercaArtista(keyword);
            while (rs != null && rs.next()) {
                String cf = rs.getString("codiceFiscale");
                String nome = rs.getString("nome");
                String cognome = rs.getString("cognome");
                String arte = rs.getString("nomeDarte");
                profiliList.getItems().add(cf + " - " + nome + " " + cognome + " (" + arte + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
            new textmessage.ErrorText("Errore durante la ricerca").okay();
        }
    }

    @FXML
    public void onProfiloClicked(javafx.scene.input.MouseEvent event) {
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
            java.sql.ResultSet rs = pkgBoundary.DBMSboundary.getInstance().queryDBMSListaDocumenti(cf);
            javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
            vbox.setAlignment(javafx.geometry.Pos.CENTER);
            boolean found = false;

            while (rs != null && rs.next()) {
                found = true;
                String path = rs.getString("percorso");
                try {
                    java.io.File file = new java.io.File(path);
                    if(file.exists()) {
                        javafx.scene.image.Image img = new javafx.scene.image.Image(file.toURI().toString());
                        javafx.scene.image.ImageView imgView = new javafx.scene.image.ImageView(img);
                        imgView.setFitWidth(300);
                        imgView.setPreserveRatio(true);
                        vbox.getChildren().add(imgView);
                    } else {
                        vbox.getChildren().add(new javafx.scene.control.Label("File non trovato: " + path));
                    }
                } catch (Exception ex) {
                    vbox.getChildren().add(new javafx.scene.control.Label("Errore caricamento: " + path));
                }
            }

            if (!found) {
                vbox.getChildren().add(new javafx.scene.control.Label("Nessun file presente per questo utente."));
            }

            javafx.scene.control.ScrollPane sp = new javafx.scene.control.ScrollPane(vbox);
            sp.setFitToWidth(true);

            javafx.scene.Scene scene = new javafx.scene.Scene(sp, 400, 500);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("File dell'utente");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
