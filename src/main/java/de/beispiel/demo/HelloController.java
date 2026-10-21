package de.beispiel.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.application.Platform;


import java.io.IOException;
import java.sql.SQLException;

import static de.beispiel.demo.LoginController.*;

public class HelloController {

    // tabelle
    @FXML private TableView<Produkt> produktTabelle;
    @FXML private TableColumn<Produkt, String> idColumn;
    @FXML private TableColumn<Produkt, String> nameColumn;
    @FXML private TableColumn<Produkt, String> kategorieColumn;
    @FXML private TableColumn<Produkt, Integer> lagerColumn;
    @FXML private TableColumn<Produkt, Double> einkaufColumn;
    @FXML private TableColumn<Produkt, Double> verkaufColumn;
    @FXML private TableColumn<Produkt, Integer> verkauftColumn;


    // textfelder
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField lagerField;
    @FXML private TextField einkaufField;
    @FXML private TextField verkaufField;
    @FXML private TextField searchField;
    @FXML private TextField verkaufenMengeField;



    //buttons
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button abmeldenButton;
    @FXML private Button verkaufenButton;

    @FXML private ComboBox<String> kategorieComboBox;

    @FXML private Label userLabel;
    @FXML private Label gewinnLabel;

    // list für Produkte welche automatisch updatet
    private ObservableList<Produkt> produktListe = FXCollections.observableArrayList();

    // Initialisierung
    @FXML
    public void initialize() {

        userLabel.setText(Session.user.getUsername());

        gewinnLabel.setText("Gewinn: " + Database.getGewinn());

        verkauftColumn.setCellValueFactory(cellData ->
                cellData.getValue().verkauftProperty().asObject()
        );



        if(Session.user.getRole() == option.SELLER){
            addButton.setDisable(true);
            updateButton.setDisable(true);
            deleteButton.setDisable(true);
            verkaufenMengeField.setText("1");
            verkaufenMengeField.setDisable(false);
            verkaufenButton.setDisable(false);
        }
        else{
            addButton.setDisable(false);
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
            verkaufenMengeField.setDisable(true);
            verkaufenButton.setDisable(true);
            Platform.runLater(this::checkLagerbestand);
        }

        // Spalten mit Properties verbinden
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        kategorieColumn.setCellValueFactory(cellData -> cellData.getValue().kategorieProperty());
        lagerColumn.setCellValueFactory(cellData -> cellData.getValue().lagerProperty().asObject());
        einkaufColumn.setCellValueFactory(cellData -> cellData.getValue().einkaufProperty().asObject());
        verkaufColumn.setCellValueFactory(cellData -> cellData.getValue().verkaufProperty().asObject());

        // Tabelle mit Liste verbinden
        produktTabelle.setItems(produktListe);

        // Klick auf Tabelle füllt Formular
        produktTabelle.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> fillForm(newSelection)
        );

        // datenbank initialisierun und produkte laden
        Database.init();
        produktListe.addAll(Database.loadProdukte());

        // zuweisung der kategorien
        kategorieComboBox.getItems().addAll(
                "Essen",
                "Getränke",
                "Haushalt",
                "Elektronik",
                "Kleidung"
        );

        //suchfeld
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.isEmpty()) {
                produktTabelle.setItems(produktListe); // alle Produkte
            } else {
                produktTabelle.setItems(produktListe.filtered(p ->
                        p.getName().toLowerCase().contains(newText.toLowerCase()) ||
                        p.getKategorie().toLowerCase().contains(newText.toLowerCase())
                ));
            }
        });

    }


    private void checkLagerbestand() {
        for (Produkt p : produktListe) {
            if (p.getLager() == 0) {
                String ausverkauft = "Produkt" + p.getName() + "ist nicht mehr im Lager verfügbar";
                showAlertWarning(ausverkauft, "Bitte Bestand aktualisieren!");

            }
        }
    }



    // Button: produkt hinzufügen
    @FXML
    public void handleAddProdukt() {
        try {

            if (Database.produktIdExistiert(idField.getText())) {
                showAlertError("Fehler", "Die Produkt-ID existiert bereits");
                return;
            }

            if( Integer.parseInt(lagerField.getText()) < 0 ||
                Double.parseDouble(einkaufField.getText()) < 0 ||
                Double.parseDouble(verkaufField.getText()) < 0
            ){
                showAlertError("Fehler", "Lagerbestand, Einkaufs- und Verkaufspreis müssen positiv sein");
                return;
            }

            if(nameField.getText().isBlank() || kategorieComboBox.getValue() == null){
                showAlertError("Fehler", "Name oder Kategorie ist leer");
                return;
            }

            Produkt p = new Produkt(
                    idField.getText(),
                    nameField.getText(),
                    kategorieComboBox.getValue(),
                    Integer.parseInt(lagerField.getText()),
                    Double.parseDouble(einkaufField.getText()),
                    Double.parseDouble(verkaufField.getText()),
                    0
            );
            produktListe.add(p);
            clearFields();

            Database.saveProdukt(p);
            System.out.println("produkte in DB: " + Database.loadProdukte().size());
        } catch (NumberFormatException e) {
            showAlertError("Fehler", "Bitte gültige Zahlen für Lagerbestand und Preise eingeben.");
        }
    }

    // Button: Produkt aktuallisieren
    @FXML
    public void handleUpdateProdukt() {
        Produkt selected = produktTabelle.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {

                if (!idField.getText().equals(selected.getId())) {
                    showAlertError("Fehler", "Die ID darf nicht geändert werden!");
                    return;
                }


                if( Integer.parseInt(lagerField.getText()) < 0 ||
                        Double.parseDouble(einkaufField.getText()) < 0 ||
                        Double.parseDouble(verkaufField.getText()) < 0
                ){
                    showAlertError("Fehler", "Lagerbestand, Einkaufs- und Verkaufspreis müssen positiv sein");
                    return;
                }

                if(nameField.getText().isBlank() || kategorieComboBox.getValue() == null){
                    showAlertError("Fehler", "Name oder Kategorie ist leer");
                    return;
                }


                selected.setName(nameField.getText());
                selected.setKategorie(kategorieComboBox.getValue());
                selected.setLager(Integer.parseInt(lagerField.getText()));
                selected.setEinkauf(Double.parseDouble(einkaufField.getText()));
                selected.setVerkauf(Double.parseDouble(verkaufField.getText()));

                Database.updateProdukt(selected);

                produktTabelle.refresh();
                produktTabelle.getSelectionModel().clearSelection();
                produktTabelle.requestFocus();
                clearFields();
            } catch (NumberFormatException e) {
                showAlertError("Fehler", "Bitte gültige Zahlen für Lagerbestand und Preise eingeben.");
            }
        } else {
            showAlertError("Fehler", "Kein Produkt ausgewählt.");
        }
    }

    // Button: Produkt löschen
    @FXML
    public void handleDeleteProdukt() {
        Produkt selected = produktTabelle.getSelectionModel().getSelectedItem();
        if (selected != null) {

            Database.deleteProdukt(selected);
            produktListe.remove(selected);
            clearFields();
        } else {
            showAlertError("Fehler", "Kein Produkt ausgewählt.");
        }
    }


    //textfelder ausfüllen
    private void fillForm(Produkt p) {
        if (p != null) {
            idField.setText(p.getId());
            nameField.setText(p.getName());
            kategorieComboBox.setValue(p.getKategorie());
            lagerField.setText(String.valueOf(p.getLager()));
            einkaufField.setText(String.valueOf(p.getEinkauf()));
            verkaufField.setText(String.valueOf(p.getVerkauf()));
        }
    }

    //formular leeren
    private void clearFields() {
        idField.clear();
        nameField.clear();
        lagerField.clear();
        einkaufField.clear();
        verkaufField.clear();

        kategorieComboBox.getSelectionModel().clearSelection();
        kategorieComboBox.setValue(null);

    }

    //Button : abmelden
    @FXML
    public void handleAbmelden(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene scene = null;
            scene = new Scene(loader.load(), 500, 500);

            Stage stage = (Stage) abmeldenButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // button: export
    @FXML
    public void handleExport(){
        try {
            Database.exportTable("tabelle.csv");

            showAlertInformation("Erfolg", "Datei wurde erfolgreich erstellt/überschrieben");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    @FXML
    public void handleVerkaufen() {

        int menge = Integer.parseInt(verkaufenMengeField.getText());

        Produkt selectedProdukt = produktTabelle.getSelectionModel().getSelectedItem();

        if (selectedProdukt == null) {
            showAlertError("Fehler", "Produkt muss ausgewählt werden");
            return;
        }

        boolean verkauft = Database.sellProdukt(selectedProdukt.getId(), menge);

        if (!verkauft) {
            showAlertError("Fehler", "Lagerbestand muss ausreichen");
            return;
        }

        selectedProdukt.setLager(selectedProdukt.getLager() - menge);
        selectedProdukt.setVerkauft(selectedProdukt.getVerkauft() + menge);

        verkaufenMengeField.setText("1");
        produktTabelle.refresh();

        gewinnLabel.setText("Gewinn: " + Database.getGewinn());
    }





}
