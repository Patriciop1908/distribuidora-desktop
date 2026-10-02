package com.distribuidora.controlador;

import com.distribuidora.dao.ProductoDAO;
import com.distribuidora.dao.ProveedorDAO;
import com.distribuidora.modelo.Producto;
import com.distribuidora.modelo.Proveedor;
import com.distribuidora.servicio.ProductoService;
import com.distribuidora.servicio.ProveedorService;
import com.distribuidora.servicio.ValidacionException;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.sql.SQLException;
import java.util.regex.Pattern;

public class ProductoFormController {

    // Filtros de escritura: solo impiden teclear caracteres no válidos; la validación real está en el servicio
    private static final Pattern DECIMAL = Pattern.compile("\\d*([.,]\\d{0,2})?");
    private static final Pattern ENTERO = Pattern.compile("\\d*");
    private static final String SIN_PROVEEDOR = "Sin proveedor";

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtStock;
    @FXML
    private ComboBox<Proveedor> cmbProveedor;
    @FXML
    private Label lblMensaje;

    private ProductoService productoService;
    private ProveedorService proveedorService;

    @FXML
    private void initialize() {
        txtPrecio.setTextFormatter(new TextFormatter<String>(c ->
                DECIMAL.matcher(c.getControlNewText()).matches() ? c : null));
        txtStock.setTextFormatter(new TextFormatter<String>(c ->
                ENTERO.matcher(c.getControlNewText()).matches() ? c : null));

        // La opción null de la lista representa "Sin proveedor"
        cmbProveedor.setCellFactory(lista -> new CeldaProveedor());
        cmbProveedor.setButtonCell(new CeldaProveedor());

        try {
            productoService = new ProductoService(new ProductoDAO());
            proveedorService = new ProveedorService(new ProveedorDAO());
            cargarProveedores();
        } catch (SQLException e) {
            Mensajes.mostrarError(lblMensaje, "No se pudo inicializar la base de datos: " + e.getMessage());
        }
    }

    /**
     * Recarga la lista de proveedores, conservando la selección actual si sigue existiendo.
     * Se llama al volver a esta pantalla, por si se registraron proveedores nuevos.
     */
    public void cargarProveedores() {
        if (proveedorService == null) {
            return;
        }
        Integer idSeleccionado = cmbProveedor.getValue() == null ? null : cmbProveedor.getValue().getId();
        try {
            cmbProveedor.getItems().setAll((Proveedor) null);
            cmbProveedor.getItems().addAll(proveedorService.listar());
        } catch (SQLException e) {
            Mensajes.mostrarError(lblMensaje, "No se pudieron cargar los proveedores: " + e.getMessage());
        }
        cmbProveedor.setValue(cmbProveedor.getItems().stream()
                .filter(p -> p != null && idSeleccionado != null && p.getId() == idSeleccionado)
                .findFirst()
                .orElse(null));
    }

    @FXML
    private void onGuardar() {
        if (productoService == null) {
            Mensajes.mostrarError(lblMensaje, "La base de datos no está disponible.");
            return;
        }

        Proveedor proveedor = cmbProveedor.getValue();
        try {
            Producto producto = productoService.registrar(
                    txtNombre.getText(), txtPrecio.getText(), txtStock.getText(),
                    proveedor == null ? null : proveedor.getId());
            limpiarFormulario();
            Mensajes.mostrarExito(lblMensaje,
                    "Producto \"" + producto.getNombre() + "\" guardado (ID " + producto.getId() + ").");
        } catch (ValidacionException e) {
            Mensajes.mostrarError(lblMensaje, e.getMessage());
        } catch (SQLException e) {
            Mensajes.mostrarError(lblMensaje, "Error al guardar: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtPrecio.clear();
        txtStock.clear();
        cmbProveedor.setValue(null);
        txtNombre.requestFocus();
    }

    private static class CeldaProveedor extends ListCell<Proveedor> {
        @Override
        protected void updateItem(Proveedor proveedor, boolean vacia) {
            super.updateItem(proveedor, vacia);
            setText(proveedor == null ? SIN_PROVEEDOR : proveedor.getNombre());
        }
    }
}
