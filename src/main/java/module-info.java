module com.distribuidora {
    // transitive: Main (paquete exportado) expone Stage de javafx.graphics en su API pública
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.distribuidora to javafx.fxml;
    opens com.distribuidora.controlador to javafx.fxml;
    exports com.distribuidora;
}
