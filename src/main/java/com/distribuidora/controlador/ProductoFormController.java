package com.distribuidora.controlador;

import com.distribuidora.dao.ProductoDAO;
import com.distribuidora.modelo.Producto;
import com.distribuidora.servicio.ProductoService;
import com.distribuidora.servicio.ValidacionException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.sql.SQLException;
import java.util.regex.Pattern;

public class ProductoFormController {

    // Filtros de escritura: solo impiden teclear caracteres no válidos; la validación real está en el servicio
    private static final Pattern DECIMAL = Pattern.compile("\\d*([.,]\\d{0,2})?");
    private static final Pattern ENTERO = Pattern.compile("\\d*");

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtStock;
    @FXML
    private Label lblMensaje;

    private ProductoService productoService;

    @FXML
    private void initialize() {
        txtPrecio.setTextFormatter(new TextFormatter<String>(c ->
                DECIMAL.matcher(c.getControlNewText()).matches() ? c : null));
        txtStock.setTextFormatter(new TextFormatter<String>(c ->
                ENTERO.matcher(c.getControlNewText()).matches() ? c : null));

        try {
            productoService = new ProductoService(new ProductoDAO());
        } catch (SQLException e) {
            mostrarError("No se pudo inicializar la base de datos: " + e.getMessage());
        }
    }

    @FXML
    private void onGuardar() {
        if (productoService == null) {
            mostrarError("La base de datos no está disponible.");
            return;
        }

        try {
            Producto producto = productoService.registrar(
                    txtNombre.getText(), txtPrecio.getText(), txtStock.getText());
            limpiarFormulario();
            mostrarExito("Producto \"" + producto.getNombre() + "\" guardado (ID " + producto.getId() + ").");
        } catch (ValidacionException e) {
            mostrarError(e.getMessage());
        } catch (SQLException e) {
            mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtPrecio.clear();
        txtStock.clear();
        txtNombre.requestFocus();
    }

    private void mostrarExito(String mensaje) {
        lblMensaje.setStyle("-fx-text-fill: #2e7d32;");
        lblMensaje.setText(mensaje);
    }

    private void mostrarError(String mensaje) {
        lblMensaje.setStyle("-fx-text-fill: #c62828;");
        lblMensaje.setText(mensaje);
    }
}
