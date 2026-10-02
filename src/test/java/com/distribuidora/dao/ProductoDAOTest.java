package com.distribuidora.dao;

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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void productoSinProveedorSeGuardaConProveedorNull() throws SQLException {
        Producto producto = new Producto("Arroz", new BigDecimal("1.20"), 50);

        dao.insertar(producto);

        assertNull(dao.consultarPorId(producto.getId()).orElseThrow().getProveedorId());
    }

    @Test
    void productoConProveedorGuardaLaRelacion() throws SQLException {
        Proveedor proveedor = new Proveedor("Lácteos del Norte", null, null, null);
        new ProveedorDAO(url).insertar(proveedor);
        Producto producto = new Producto("Leche 1L", new BigDecimal("0.89"), 100, proveedor.getId());

        dao.insertar(producto);

        assertEquals(proveedor.getId(), dao.consultarPorId(producto.getId()).orElseThrow().getProveedorId());
        assertEquals(proveedor.getId(), dao.listar().get(0).getProveedorId());
    }

    @Test
    void rechazaProveedorInexistente() {
        Producto producto = new Producto("Leche 1L", new BigDecimal("0.89"), 100, 999);

        assertThrows(SQLException.class, () -> dao.insertar(producto));
    }

    @Test
    void migraUnaTablaAntiguaSinColumnaProveedorConservandoLosDatos() throws SQLException {
        // Simula una distribuidora.db creada antes de existir los proveedores
        String urlAntigua = "jdbc:sqlite:" + carpetaTemporal.resolve("antigua.db");
        try (Connection conn = DriverManager.getConnection(urlAntigua);
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
                    CREATE TABLE productos (
                        id     INTEGER PRIMARY KEY AUTOINCREMENT,
                        nombre TEXT    NOT NULL,
                        precio TEXT    NOT NULL,
                        stock  INTEGER NOT NULL
                    )
                    """);
            stmt.execute("INSERT INTO productos (nombre, precio, stock) VALUES ('Sal', '0.50', 10)");
        }

        ProductoDAO daoMigrado = new ProductoDAO(urlAntigua);

        Producto existente = daoMigrado.listar().get(0);
        assertEquals("Sal", existente.getNombre());
        assertNull(existente.getProveedorId());

        Proveedor proveedor = new Proveedor("Salinas del Mar", null, null, null);
        new ProveedorDAO(urlAntigua).insertar(proveedor);
        Producto nuevo = new Producto("Sal gruesa", new BigDecimal("0.70"), 5, proveedor.getId());
        daoMigrado.insertar(nuevo);
        assertEquals(proveedor.getId(), daoMigrado.consultarPorId(nuevo.getId()).orElseThrow().getProveedorId());
    }

    @Test
    void crearTablaEsIdempotenteYNoBorraDatos() throws SQLException {
        dao.insertar(new Producto("Sal", new BigDecimal("0.50"), 10));

        ProductoDAO otroDao = new ProductoDAO(url);

        assertEquals(1, otroDao.listar().size());
    }
}
