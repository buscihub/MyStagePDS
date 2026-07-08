package pkgMain;

import javafx.application.Application;
import javafx.stage.Stage;
import pkgUtility.Router;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Il server HTTP gira in un nodo separato, non viene più avviato dal Client.

        // Inizializza il Router JavaFX con lo stage primario
        Router.getInstance().setPrimaryStage(primaryStage);

        // Naviga alla prima schermata
        Router.getInstance().navigate("login.fxml", "MyStage - Login");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
