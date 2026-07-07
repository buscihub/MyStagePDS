package pkgControl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import pkgBoundary.DBMSboundary;
import pkgTextmessage.ErrorText;
import pkgTextmessage.SuccessfulText;
import pkgEntity.CarrieraEntity;
import pkgUtility.UserSession;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class ModificaCarrieraCtrl implements Initializable {

    @FXML private TableView<CarrieraEntity> carriereTable;
    @FXML private TableColumn<CarrieraEntity, String> colTipoCarriera;
    @FXML private TableColumn<CarrieraEntity, Integer> colAnniCarriera;
    @FXML private TableColumn<CarrieraEntity, Void> colAzioneCarriera;
    
    @FXML private TextField nuovaCarrieraField;
    @FXML private TextField anniCarrieraField;
    
    private final ObservableList<CarrieraEntity> carriereList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colTipoCarriera.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colAnniCarriera.setCellValueFactory(new PropertyValueFactory<>("anniDiCarriera"));
        
        colAzioneCarriera.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Rimuovi");
            {
                btn.getStyleClass().add("secondary-button");
                btn.setOnAction(event -> {
                    CarrieraEntity c = getTableView().getItems().get(getIndex());
                    pkgTextmessage.ConfirmText conferma = new pkgTextmessage.ConfirmText("Vuoi davvero rimuovere questa qualifica di carriera?");
                    if (conferma.si()) {
                        DBMSboundary.getInstance().removeDBMSCarriereSelezionate(c.getIdCarriera());
                        caricaCarriere();
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(btn);
            }
        });
        
        caricaCarriere();
    }

    private void caricaCarriere() {
        carriereList.clear();
        String cf = UserSession.getInstance().getUtenteLoggato();
        try {
            ResultSet rs = DBMSboundary.getInstance().queryDBMSListaCarriere(cf);
            while (rs != null && rs.next()) {
                carriereList.add(new CarrieraEntity(
                        rs.getInt("idCarriera"),
                        rs.getString("codiceFiscaleArtist"),
                        rs.getString("tipo"),
                        rs.getInt("anniDiCarriera")
                ));
            }
            carriereTable.setItems(carriereList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void aggiungiCarriera(ActionEvent event) {
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
            new SuccessfulText("Carriera aggiunta!").okay();
            nuovaCarrieraField.clear();
            anniCarrieraField.clear();
            caricaCarriere();
        } else {
            new ErrorText("Errore durante l'aggiunta della carriera.").okay();
        }
    }
}
