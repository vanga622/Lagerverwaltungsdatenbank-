module de.beispiel.demo {


    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;

    opens de.beispiel.demo to javafx.fxml;
    exports de.beispiel.demo;
    exports de.beispiel.demo.dbUtil;
    opens de.beispiel.demo.dbUtil to javafx.fxml;


}
