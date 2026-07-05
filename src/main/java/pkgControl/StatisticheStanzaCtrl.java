package pkgControl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pkgBoundary.DBMSboundary;
import pkgEntity.VisualizzazioneDto;
import pkgUtility.Router;
import pkgUtility.UserSession;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class StatisticheStanzaCtrl implements Initializable {

    @FXML private Label titoloLabel;
    @FXML private TableView<VisualizzazioneDto> statisticheTable;
    @FXML private TableColumn<VisualizzazioneDto, String> colNome;
    @FXML private TableColumn<VisualizzazioneDto, String> colCognome;
    @FXML private TableColumn<VisualizzazioneDto, String> colEmail;
    @FXML private TableColumn<VisualizzazioneDto, String> colData;

    private ObservableList<VisualizzazioneDto> statisticheList = FXCollections.observableArrayList();
    private Integer idStanzaCorrente;
    private String linkStanza;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idStanzaCorrente = UserSession.getInstance().getStanzaSelezionata();
        if (idStanzaCorrente == null) {
            goToStanze(null);
            return;
        }

        colNome.setCellValueFactory(new PropertyValueFactory<>("nomeVisualizzatore"));
        colCognome.setCellValueFactory(new PropertyValueFactory<>("cognomeVisualizzatore"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("emailVisualizzatore"));
        colData.setCellValueFactory(new PropertyValueFactory<>("dataVisualizzazione"));

        recuperaLinkStanza();
        caricaStatistiche();
    }

    private void recuperaLinkStanza() {
        try {
            ResultSet rs = DBMSboundary.getInstance().queryDBMSLinkStanza(idStanzaCorrente);
            if (rs != null && rs.next()) {
                linkStanza = rs.getString("link");
                titoloLabel.setText("Statistiche Accessi: " + linkStanza);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void caricaStatistiche() {
        if (linkStanza == null) return;
        
        statisticheList.clear();
        try {
            ResultSet rs = DBMSboundary.getInstance().queryDBMSListaVisualizzatori(linkStanza);
            while (rs != null && rs.next()) {
                statisticheList.add(new VisualizzazioneDto(
                        rs.getString("nomeVisualizzatore"),
                        rs.getString("cognomeVisualizzatore"),
                        rs.getString("emailVisualizzatore"),
                        rs.getString("dataVisualizzazione")
                ));
            }
            statisticheTable.setItems(statisticheList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToProfilo(ActionEvent event) {
        Router.getInstance().navigate("profilo.fxml", "ShareRoomAfam - Profilo");
    }

    @FXML
    public void goToStanze(ActionEvent event) {
        UserSession.getInstance().setStanzaSelezionata(null);
        Router.getInstance().navigate("gestione_stanze.fxml", "ShareRoomAfam - Gestione Stanze");
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        UserSession.getInstance().logout();
        Router.getInstance().navigate("login.fxml", "ShareRoomAfam - Login");
    }
}
