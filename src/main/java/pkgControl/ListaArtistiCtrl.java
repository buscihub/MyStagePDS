package pkgControl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.control.Label;
import pkgUtility.Router;
import pkgUtility.UserSession;

import java.util.List;

public class ListaArtistiCtrl {

    @FXML
    private ListView<String> profiliList;

    @SuppressWarnings("unchecked")
    @FXML
    public void initialize() {
        List<String> risultati = (List<String>) UserSession.getInstance().retrieveFromCache("risultati_ricerca");
        if (risultati != null) {
            ObservableList<String> items = FXCollections.observableArrayList(risultati);
            profiliList.setItems(items);
            
            // Custom cell per avere il bottone "Visualizza"
            profiliList.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        HBox root = new HBox();
                        Label lbl = new Label(item);
                        Region spacer = new Region();
                        HBox.setHgrow(spacer, Priority.ALWAYS);
                        Button btn = new Button("Visualizza");
                        btn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 2 10 2 10;");
                        btn.setOnAction(e -> mostraDocumenti(item));
                        
                        root.getChildren().addAll(lbl, spacer, btn);
                        setGraphic(root);
                    }
                }
            });
        }
    }

    private void mostraDocumenti(String item) {
        String cf = item.split(" - ")[0];
        try {
            UserSession.getInstance().saveToCache("artistaDaVisualizzare", cf);
            Router.getInstance().navigate("profilo_pubblico.fxml", "MyStage - Profilo Pubblico");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void nuovaRicerca(ActionEvent event) {
        Router.getInstance().navigate("cerca_artista.fxml", "MyStage - Cerca Artista");
    }
}
