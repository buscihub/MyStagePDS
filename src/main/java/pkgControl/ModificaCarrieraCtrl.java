package pkgControl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import pkgBoundary.DBMSboundary;
import pkgTextmessage.ErrorText;
import pkgTextmessage.SuccessfulText;
import pkgUtility.Router;
import pkgUtility.UserSession;

public class ModificaCarrieraCtrl {

    @FXML
    public void initialize() {
        try { init_ListaCarriereCtrl(); } catch(Exception e) { /* ignore */ }
    }

@FXML
    public void goToAggiungiCarriera(ActionEvent event) {
        Router.getInstance().navigate("inserisci_dati_carriera.fxml", "MyStage - Aggiungi Carriera");
    }

    @FXML
    public void goToRimuoviCarriera(ActionEvent event) {
        Router.getInstance().navigate("lista_carriere.fxml", "MyStage - Rimuovi Carriere");
    }

    @FXML
    public void goBack(ActionEvent event) {
        Router.getInstance().navigate("gestione_dati_personali.fxml", "MyStage - Gestione Dati Personali");
    }

@FXML private TextField nuovaCarrieraField;
    @FXML private TextField anniCarrieraField;

    @FXML
    public void salvaModifiche(ActionEvent event) {
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
            new SuccessfulText("Carriera aggiunta con successo!").okay();
            nuovaCarrieraField.clear();
            anniCarrieraField.clear();
            Router.getInstance().navigate("modifica_carriera.fxml", "MyStage - Modifica Carriera");
        } else {
            new ErrorText("Errore durante l'aggiunta della carriera.").okay();
        }
    }

    @FXML
    public void annulla(ActionEvent event) {
        Router.getInstance().navigate("modifica_carriera.fxml", "MyStage - Modifica Carriera");
    }

@FXML
    private ListView<CheckBox> carriereListView;

    private void init_ListaCarriereCtrl() {
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
    public void annulla_ListaCarriereCtrl(ActionEvent event) {
        Router.getInstance().navigate("modifica_carriera.fxml", "MyStage - Modifica Carriera");
    }
}
