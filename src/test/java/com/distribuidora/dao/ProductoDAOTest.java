package com.distribuidora.dao;

import com.distribuidora.modelo.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductoDAOTest {

    @TempDir
    Path carpetaTemporal;

    private String url;
    private ProductoDAO dao;

    @BeforeEach
    void prepararBaseDeDatos() throws SQLException {
        // Cada prueba usa su propia base de datos, nunca distribuidora.db
        url = "jdbc:sqlite:" + carpetaTemporal.resolve("prueba.db");
        dao = new ProductoDAO(url);
    }

    @Test
    void insertarAsignaIdYSePuedeConsultar() throws SQLException {
        Producto producto = new Producto("Aceite 1L", new BigDecimal("3.45"), 20);

        dao.insertar(producto);

        assertTrue(producto.getId() > 0);
        Producto guardado = dao.consultarPorId(producto.getId()).orElseThrow();
        assertEquals("Aceite 1L", guardado.getNombre());
        assertEquals(new BigDecimal("3.45"), guardado.getPrecio());
        assertEquals(20, guardado.getStock());
    }

    @Test
    void conservaElPrecioExactoSinErroresDeComaFlotante() throws SQLException {
        Producto producto = new Producto("Galletas", new BigDecimal("0.10"), 1);

        dao.insertar(producto);

        assertEquals(new BigDecimal("0.10"), dao.consultarPorId(producto.getId()).orElseThrow().getPrecio());
    }

    @Test
    void consultarIdInexistenteDevuelveVacio() throws SQLException {
        Optional<Producto> resultado = dao.consultarPorId(999);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarDevuelveTodosEnOrdenDeInsercion() throws SQLException {
        dao.insertar(new Producto("Arroz", new BigDecimal("1.20"), 50));
        dao.insertar(new Producto("Azúcar", new BigDecimal("0.95"), 30));

        List<Producto> productos = dao.listar();

        assertEquals(2, productos.size());
        assertEquals("Arroz", productos.get(0).getNombre());
        assertEquals("Azúcar", productos.get(1).getNombre());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Aceite 1L", "aceite 1l", "ACEITE 1L", "AcEiTe 1L"})
    void existePorNombreIgnoraMayusculasYMinusculas(String nombreBuscado) throws SQLException {
        dao.insertar(new Producto("Aceite 1L", new BigDecimal("3.45"), 20));

        assertTrue(dao.existePorNombre(nombreBuscado));
    }

    @ParameterizedTest
    @CsvSource({
            "Azúcar, AZÚCAR",
            "Ñoquis, ñoquis",
            "Café molido, CAFÉ MOLIDO"
    })
    void existePorNombreIgnoraMayusculasEnLetrasAcentuadasYEnie(String guardado, String buscado)
            throws SQLException {
        dao.insertar(new Producto(guardado, new BigDecimal("1.00"), 1));

        assertTrue(dao.existePorNombre(buscado));
    }

    @Test
    void existePorNombreDevuelveFalsoSiNoHayCoincidencia() throws SQLException {
        dao.insertar(new Producto("Aceite 1L", new BigDecimal("3.45"), 20));

        assertFalse(dao.existePorNombre("Aceite 5L"));
        assertFalse(dao.existePorNombre("Aceite"));
    }

    @Test
    void existePorNombreEnTablaVaciaDevuelveFalso() throws SQLException {
        assertFalse(dao.existePorNombre("Cualquiera"));
    }

    @Test
    void crearTablaEsIdempotenteYNoBorraDatos() throws SQLException {
        dao.insertar(new Producto("Sal", new BigDecimal("0.50"), 10));

        ProductoDAO otroDao = new ProductoDAO(url);

        assertEquals(1, otroDao.listar().size());
    }
}
