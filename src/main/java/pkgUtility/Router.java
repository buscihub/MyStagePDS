package pkgUtility;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Router {
    private static Router instance;
    private Stage primaryStage;

    private Router() {}

    public static Router getInstance() {
        if (instance == null) {
            instance = new Router();
        }
        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public Stage getStage() {
        return primaryStage;
    }

    public void navigate(String fxmlFile, String title) {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage not set in Router.");
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxmlFile));
            Parent root = loader.load();
            primaryStage.setTitle(title);

            // Riutilizza la Scene esistente per non resettare le dimensioni dello Stage
            Scene currentScene = primaryStage.getScene();
            if (currentScene != null) {
                currentScene.setRoot(root);
            } else {
                primaryStage.setScene(new Scene(root));
                primaryStage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
