package de.beispiel.demo;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


import java.net.URL;
import java.util.ResourceBundle;

import static de.beispiel.demo.Database.saveUser;

public class LoginController implements Initializable {

    LoginModel loginModel = new LoginModel();
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<option> comboBox;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    public void initialize(URL location, ResourceBundle resources) {
        this.comboBox.setItems(FXCollections.observableArrayList(option.values()));
    }

    @FXML
    public void loginButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        option opt = comboBox.getValue();

        if (opt == null) {
            showAlertError("Fehler", "Bitte eine Feld ausfüllen!");
            return;
        }

        if (username.isEmpty() || password.isEmpty()) {
            showAlertError("Fehler", "Bitte Username und Passwort eingeben!");
            return;
        }

        User loginSuccess;
        try {
            loginSuccess = this.loginModel.isLogin(this.usernameField.getText(), this.passwordField.getText(), (option)this.comboBox.getValue());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        if (loginSuccess != null) {
            Session.user = loginSuccess;
            //Session.role  = opt;
            //Session.username = username;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
                Scene scene = new Scene(loader.load(), 1000, 700);

                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Produktverwaltung");

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlertError("Login fehlgeschlagen", "Benutzername, Passwort oder Division falsch!");
        }

    }

    @FXML
    public void registerButtonAction(ActionEvent event){

        String username = usernameField.getText();
        String password = passwordField.getText();
        option opt = comboBox.getValue();

        if (opt == null) {
            showAlertError("Fehler", "Bitte eine Feld ausfüllen!");
            return;
        }

        if (username.isEmpty() || password.isEmpty()) {
            showAlertError("Fehler", "Bitte Username und Passwort eingeben!");
            return;
        }

        if(!saveUser(username, password, opt)) showAlertError("User existiert bereits", "Bitte anderen username wählen");

    }


    //Damit man einfache fehlermeldung anzeigen kann
    public static void showAlertError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //Damit man eine einfache Infomation anzeigen kann
    public static void showAlertInformation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //Damit man eine einfache Warnung anzeigen kann
    public static void showAlertWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



}
