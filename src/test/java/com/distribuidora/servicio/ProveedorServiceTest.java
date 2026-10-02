package com.distribuidora.servicio;

import com.distribuidora.dao.ProveedorDAO;
import com.distribuidora.modelo.Proveedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProveedorServiceTest {

    @TempDir
    Path carpetaTemporal;

    private ProveedorDAO dao;
    private ProveedorService servicio;

    @BeforeEach
    void preparar() throws SQLException {
        dao = new ProveedorDAO("jdbc:sqlite:" + carpetaTemporal.resolve("prueba.db"));
        servicio = new ProveedorService(dao);
    }

    @Test
    void registrarGuardaElProveedorConDatosLimpios() throws Exception {
        Proveedor proveedor = servicio.registrar("  Lácteos del Norte ", " Marta Gil ", " 600 123 456 ", " ventas@lacteos.es ");

        assertTrue(proveedor.getId() > 0);
        assertEquals("Lácteos del Norte", proveedor.getNombre());
        assertEquals("Marta Gil", proveedor.getContacto());
        assertEquals("600 123 456", proveedor.getTelefono());
        assertEquals("ventas@lacteos.es", proveedor.getEmail());
        assertEquals(1, dao.listar().size());
    }

    @Test
    void camposOpcionalesVaciosSeGuardanComoNull() throws Exception {
        Proveedor proveedor = servicio.registrar("Solo Nombre", "", "   ", null);

        assertNull(proveedor.getContacto());
        assertNull(proveedor.getTelefono());
        assertNull(proveedor.getEmail());
    }

    @ParameterizedTest
    @ValueSource(strings = {"600123456", "+34 600 123 456", "(91) 555-12-34"})
    void aceptaFormatosDeTelefonoHabituales(String telefono) throws Exception {
        Proveedor proveedor = servicio.registrar("Proveedor", null, telefono, null);

        assertEquals(telefono, proveedor.getTelefono());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "'',        '', '',        '',             El nombre es obligatorio.",
            "'   ',     '', '',        '',             El nombre es obligatorio.",
            "Proveedor, '', abc123,    '',             El teléfono no es válido.",
            "Proveedor, '', 12345,     '',             El teléfono no es válido.",
            "Proveedor, '', 600+123456, '',            El teléfono no es válido.",
            "Proveedor, '', '',        ventas,         El email no es válido.",
            "Proveedor, '', '',        ventas@,        El email no es válido.",
            "Proveedor, '', '',        ventas@lacteos, El email no es válido.",
            "Proveedor, '', '',        'a b@c.es',     El email no es válido."
    })
    void rechazaDatosInvalidosSinGuardar(String nombre, String contacto, String telefono, String email,
                                         String mensajeEsperado) throws SQLException {
        ValidacionException error = assertThrows(ValidacionException.class,
                () -> servicio.registrar(nombre, contacto, telefono, email));

        assertEquals(mensajeEsperado, error.getMessage());
        assertTrue(dao.listar().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Lácteos del Norte", "lácteos del norte", "LÁCTEOS DEL NORTE", "  Lácteos del Norte  "})
    void rechazaNombreDuplicadoSinDistinguirMayusculas(String nombreRepetido) throws Exception {
        servicio.registrar("Lácteos del Norte", null, null, null);

        ValidacionException error = assertThrows(ValidacionException.class,
                () -> servicio.registrar(nombreRepetido, "Otro contacto", null, null));

        assertEquals("Ya existe un proveedor con el nombre \"" + nombreRepetido.trim() + "\".", error.getMessage());
        assertEquals(1, dao.listar().size());
    }

    @Test
    void listarDevuelveLosProveedoresRegistrados() throws Exception {
        servicio.registrar("Bebidas Sur", null, null, null);
        servicio.registrar("Aceites Olivar", null, null, null);

        assertEquals(2, servicio.listar().size());
    }
}
