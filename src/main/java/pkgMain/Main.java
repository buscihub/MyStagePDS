package pkgMain;

import javafx.application.Application;
import javafx.stage.Stage;
import pkgServer.WebServerManager;
import pkgUtility.Router;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Avvia il server HTTP backend in un thread separato o in background
        WebServerManager.getInstance().startServer();

        // Inizializza il Router JavaFX con lo stage primario
        Router.getInstance().setPrimaryStage(primaryStage);
        
        // Naviga alla prima schermata
        Router.getInstance().navigate("login.fxml", "MyStage - Login");
    }

    @Override
    public void stop() throws Exception {
        // Stoppa il server quando si chiude l'app
        WebServerManager.getInstance().stopServer();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
