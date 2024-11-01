package hu.unideb.interscope;

import hu.unideb.interscope.core.InterScopeApp;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.json.simple.JSONObject;

import java.util.Objects;

public class InterScopeLauncher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        JSONObject emailData = InterScopeApp.getEmailData("alexis@reddit.com");
        String formattedEmailData = InterScopeApp.getFormattedEmailData(emailData);
        System.out.println(formattedEmailData);
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("interscope.fxml")));
        Scene scene = new Scene(root, 888, 500);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/menu.css")).toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
