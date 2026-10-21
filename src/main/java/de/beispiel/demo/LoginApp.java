package de.beispiel.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class LoginApp extends Application {
    public void start(Stage stage) throws IOException {

        Database.init();

        FXMLLoader root = new FXMLLoader(LoginApp.class.getResource("login.fxml"));
        Scene scene = new Scene(root.load());

        scene.getStylesheets().add(
                LoginApp.class.getResource("style.css").toExternalForm()
        );

        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }
}
