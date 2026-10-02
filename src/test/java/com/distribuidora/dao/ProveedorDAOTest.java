package com.distribuidora.dao;

import com.distribuidora.modelo.Proveedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProveedorDAOTest {

    @TempDir
    Path carpetaTemporal;

    private String url;
    private ProveedorDAO dao;

    @BeforeEach
    void prepararBaseDeDatos() throws SQLException {
        // Cada prueba usa su propia base de datos, nunca distribuidora.db
        url = "jdbc:sqlite:" + carpetaTemporal.resolve("prueba.db");
        dao = new ProveedorDAO(url);
    }

    @Test
    void insertarAsignaIdYSePuedeConsultar() throws SQLException {
        Proveedor proveedor = new Proveedor("Lácteos del Norte", "Marta Gil", "+34 600 123 456", "ventas@lacteos.es");

        dao.insertar(proveedor);

        assertTrue(proveedor.getId() > 0);
        Proveedor guardado = dao.consultarPorId(proveedor.getId()).orElseThrow();
        assertEquals("Lácteos del Norte", guardado.getNombre());
        assertEquals("Marta Gil", guardado.getContacto());
        assertEquals("+34 600 123 456", guardado.getTelefono());
        assertEquals("ventas@lacteos.es", guardado.getEmail());
    }

    @Test
    void guardaLosCamposOpcionalesComoNull() throws SQLException {
        Proveedor proveedor = new Proveedor("Solo Nombre", null, null, null);

        dao.insertar(proveedor);

        Proveedor guardado = dao.consultarPorId(proveedor.getId()).orElseThrow();
        assertNull(guardado.getContacto());
        assertNull(guardado.getTelefono());
        assertNull(guardado.getEmail());
    }

    @Test
    void consultarIdInexistenteDevuelveVacio() throws SQLException {
        assertTrue(dao.consultarPorId(999).isEmpty());
    }

    @Test
    void listarDevuelveTodosOrdenadosPorNombreSinDistinguirMayusculas() throws SQLException {
        dao.insertar(new Proveedor("bebidas Sur", null, null, null));
        dao.insertar(new Proveedor("Conservas Ríos", null, null, null));
        dao.insertar(new Proveedor("Aceites Olivar", null, null, null));

        List<String> nombres = dao.listar().stream().map(Proveedor::getNombre).toList();

        assertEquals(List.of("Aceites Olivar", "bebidas Sur", "Conservas Ríos"), nombres);
    }

    @Test
    void listarEnTablaVaciaDevuelveListaVacia() throws SQLException {
        assertTrue(dao.listar().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Distribuciones Pérez", "distribuciones pérez", "DISTRIBUCIONES PÉREZ"})
    void existePorNombreIgnoraMayusculasYMinusculas(String nombreBuscado) throws SQLException {
        dao.insertar(new Proveedor("Distribuciones Pérez", null, null, null));

        assertTrue(dao.existePorNombre(nombreBuscado));
    }

    @ParameterizedTest
    @CsvSource({
            "Ñandú Alimentos, ñandú alimentos",
            "Café Selecto, CAFÉ SELECTO"
    })
    void existePorNombreIgnoraMayusculasEnLetrasAcentuadasYEnie(String guardado, String buscado)
            throws SQLException {
        dao.insertar(new Proveedor(guardado, null, null, null));

        assertTrue(dao.existePorNombre(buscado));
    }

    @Test
    void existePorNombreDevuelveFalsoSiNoHayCoincidencia() throws SQLException {
        dao.insertar(new Proveedor("Lácteos del Norte", null, null, null));

        assertFalse(dao.existePorNombre("Lácteos del Sur"));
        assertFalse(dao.existePorNombre("Lácteos"));
    }

    @Test
    void crearTablaEsIdempotenteYNoBorraDatos() throws SQLException {
        dao.insertar(new Proveedor("Lácteos del Norte", null, null, null));

        ProveedorDAO otroDao = new ProveedorDAO(url);

        assertEquals(1, otroDao.listar().size());
    }
}
