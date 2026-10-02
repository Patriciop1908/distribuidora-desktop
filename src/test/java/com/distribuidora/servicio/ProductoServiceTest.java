package com.distribuidora.servicio;

import com.distribuidora.dao.ProductoDAO;
import com.distribuidora.dao.ProveedorDAO;
import com.distribuidora.modelo.Producto;
import com.distribuidora.modelo.Proveedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductoServiceTest {

    @TempDir
    Path carpetaTemporal;

    private String url;
    private ProductoDAO dao;
    private ProductoService servicio;

    @BeforeEach
    void preparar() throws SQLException {
        url = "jdbc:sqlite:" + carpetaTemporal.resolve("prueba.db");
        dao = new ProductoDAO(url);
        servicio = new ProductoService(dao);
    }

    @Test
    void registrarGuardaElProductoConDatosNormalizados() throws Exception {
        Producto producto = servicio.registrar("  Harina 1kg  ", "2,5", "15");

        assertTrue(producto.getId() > 0);
        assertEquals("Harina 1kg", producto.getNombre());
        assertEquals(new BigDecimal("2.50"), producto.getPrecio());
        assertEquals(15, producto.getStock());
        assertEquals(1, dao.listar().size());
    }

    @Test
    void registrarSinProveedorDejaProveedorNull() throws Exception {
        Producto producto = servicio.registrar("Harina 1kg", "2.50", "15");

        assertNull(producto.getProveedorId());
    }

    @Test
    void registrarConProveedorGuardaLaRelacion() throws Exception {
        Proveedor proveedor = new Proveedor("Molinos Unidos", null, null, null);
        new ProveedorDAO(url).insertar(proveedor);

        Producto producto = servicio.registrar("Harina 1kg", "2.50", "15", proveedor.getId());

        assertEquals(proveedor.getId(), dao.consultarPorId(producto.getId()).orElseThrow().getProveedorId());
    }

    @Test
    void aceptaPrecioYStockCero() throws Exception {
        Producto producto = servicio.registrar("Muestra gratis", "0", "0");

        assertEquals(new BigDecimal("0.00"), producto.getPrecio());
        assertEquals(0, producto.getStock());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Harina 1kg", "harina 1kg", "HARINA 1KG", "  Harina 1kg  "})
    void rechazaNombreDuplicadoSinDistinguirMayusculas(String nombreRepetido) throws Exception {
        servicio.registrar("Harina 1kg", "2.50", "15");

        ValidacionException error = assertThrows(ValidacionException.class,
                () -> servicio.registrar(nombreRepetido, "3.00", "5"));

        assertEquals("Ya existe un producto con el nombre \"" + nombreRepetido.trim() + "\".", error.getMessage());
        assertEquals(1, dao.listar().size());
    }

    @Test
    void rechazaNombreDuplicadoConAcentosEnOtraCapitalizacion() throws Exception {
        servicio.registrar("Azúcar", "0.95", "30");

        assertThrows(ValidacionException.class, () -> servicio.registrar("AZÚCAR", "1.00", "10"));
        assertEquals(1, dao.listar().size());
    }

    @Test
    void permiteNombresDistintosAunqueSeParezcan() throws Exception {
        servicio.registrar("Aceite 1L", "3.45", "20");
        servicio.registrar("Aceite 5L", "15.90", "8");

        assertEquals(2, dao.listar().size());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "'',       1.00, 5,   El nombre es obligatorio.",
            "'   ',    1.00, 5,   El nombre es obligatorio.",
            "Leche,    '',   5,   El precio es obligatorio.",
            "Leche,    abc,  5,   El precio no es un número válido.",
            "Leche,    -1,   5,   El precio no puede ser negativo.",
            "Leche,    1.999, 5,  El precio admite como máximo 2 decimales.",
            "Leche,    1.00, '',  El stock es obligatorio.",
            "Leche,    1.00, 2.5, El stock debe ser un número entero.",
            "Leche,    1.00, -3,  El stock no puede ser negativo."
    })
    void rechazaDatosInvalidosSinGuardar(String nombre, String precio, String stock, String mensajeEsperado)
            throws SQLException {
        ValidacionException error = assertThrows(ValidacionException.class,
                () -> servicio.registrar(nombre, precio, stock));

        assertEquals(mensajeEsperado, error.getMessage());
        assertTrue(dao.listar().isEmpty());
    }
}
