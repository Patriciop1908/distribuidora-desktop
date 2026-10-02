module com.distribuidora {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.distribuidora to javafx.fxml;
    opens com.distribuidora.controlador to javafx.fxml;
    exports com.distribuidora;
}
