package com.distribuidora.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;

public class PrincipalController {

    @FXML
    private Tab tabProductos;
    // Controller del formulario incluido con fx:id="formProductos"
    @FXML
    private ProductoFormController formProductosController;

    @FXML
    private void initialize() {
        // Al volver a Productos se recargan los proveedores, por si se registró alguno en la otra pestaña
        tabProductos.setOnSelectionChanged(evento -> {
            if (tabProductos.isSelected()) {
                formProductosController.cargarProveedores();
            }
        });
    }
}
