package de.beispiel.demo;

import javafx.beans.property.*;

public class Produkt {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty kategorie;
    private final IntegerProperty lager;
    private final DoubleProperty einkauf;
    private final DoubleProperty verkauf;
    private final IntegerProperty verkauft;

    public Produkt(String id, String name, String kategorie, int lager, double einkauf, double verkauf) {
        this(id, name,kategorie, lager, einkauf, verkauf, 0);
    }


    public Produkt(String id, String name, String kategorie, int lager, double einkauf, double verkauf, int verkauft) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.kategorie = new SimpleStringProperty(kategorie);
        this.lager = new SimpleIntegerProperty(lager);
        this.einkauf = new SimpleDoubleProperty(einkauf);
        this.verkauf = new SimpleDoubleProperty(verkauf);
        this.verkauft = new SimpleIntegerProperty(verkauft);
    }


    public StringProperty idProperty() {return id;}
    public StringProperty nameProperty() {return name;}
    public StringProperty kategorieProperty() {return kategorie;}
    public IntegerProperty lagerProperty() {return lager;}
    public DoubleProperty einkaufProperty() {return einkauf;}
    public DoubleProperty verkaufProperty() {return verkauf;}
    public IntegerProperty verkauftProperty() {return verkauft;}

    public String getId() {
        return id.get();
    }
    public void setId(String id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }
    public void setName(String name) {
        this.name.set(name);
    }

    public String getKategorie() {
        return kategorie.get();
    }
    public void setKategorie(String kategorie) {
        this.kategorie.set(kategorie);
    }

    public int getLager() {
        return lager.get();
    }
    public void setLager(int lager) {
        this.lager.set(lager);
    }

    public double getEinkauf() {
        return einkauf.get();
    }
    public void setEinkauf(double einkauf) {
        this.einkauf.set(einkauf);
    }

    public double getVerkauf() {
        return verkauf.get();
    }
    public void setVerkauf(double verkauf) {
        this.verkauf.set(verkauf);
    }

    public int getVerkauft() {
        return verkauft.get();
    }
    public void setVerkauft(int verkauft) {
        this.verkauft.set(verkauft);
    }
}
