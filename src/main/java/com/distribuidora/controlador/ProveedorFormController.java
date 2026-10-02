package com.distribuidora.controlador;

import com.distribuidora.dao.ProveedorDAO;
import com.distribuidora.modelo.Proveedor;
import com.distribuidora.servicio.ProveedorService;
import com.distribuidora.servicio.ValidacionException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class ProveedorFormController {

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtContacto;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtEmail;
    @FXML
    private Label lblMensaje;

    private ProveedorService proveedorService;

    @FXML
    private void initialize() {
        try {
            proveedorService = new ProveedorService(new ProveedorDAO());
        } catch (SQLException e) {
            Mensajes.mostrarError(lblMensaje, "No se pudo inicializar la base de datos: " + e.getMessage());
        }
    }

    @FXML
    private void onGuardar() {
        if (proveedorService == null) {
            Mensajes.mostrarError(lblMensaje, "La base de datos no está disponible.");
            return;
        }

        try {
            Proveedor proveedor = proveedorService.registrar(
                    txtNombre.getText(), txtContacto.getText(), txtTelefono.getText(), txtEmail.getText());
            limpiarFormulario();
            Mensajes.mostrarExito(lblMensaje,
                    "Proveedor \"" + proveedor.getNombre() + "\" guardado (ID " + proveedor.getId() + ").");
        } catch (ValidacionException e) {
            Mensajes.mostrarError(lblMensaje, e.getMessage());
        } catch (SQLException e) {
            Mensajes.mostrarError(lblMensaje, "Error al guardar: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtContacto.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtNombre.requestFocus();
    }
}
