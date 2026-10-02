package com.distribuidora.servicio;

import com.distribuidora.dao.ProveedorDAO;
import com.distribuidora.modelo.Proveedor;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class ProveedorService {

    private static final Pattern EMAIL = Pattern.compile("[^\\s@]+@[^\\s@]+\\.[^\\s@]+");
    // Dígitos con espacios, guiones, paréntesis y un "+" inicial opcional
    private static final Pattern TELEFONO = Pattern.compile("\\+?[0-9 ()-]+");
    private static final int MIN_DIGITOS_TELEFONO = 6;

    private final ProveedorDAO proveedorDAO;

    public ProveedorService(ProveedorDAO proveedorDAO) {
        this.proveedorDAO = proveedorDAO;
    }

    /**
     * Valida los datos del formulario y guarda el proveedor.
     * Solo el nombre es obligatorio; los campos opcionales vacíos se guardan como null.
     * Rechaza nombres ya registrados, sin distinguir mayúsculas/minúsculas.
     */
    public Proveedor registrar(String nombre, String contacto, String telefono, String email)
            throws ValidacionException, SQLException {
        String nombreLimpio = limpiar(nombre);
        if (nombreLimpio == null) {
            throw new ValidacionException("El nombre es obligatorio.");
        }

        Proveedor proveedor = new Proveedor(
                nombreLimpio, limpiar(contacto), validarTelefono(telefono), validarEmail(email));

        if (proveedorDAO.existePorNombre(nombreLimpio)) {
            throw new ValidacionException("Ya existe un proveedor con el nombre \"" + nombreLimpio + "\".");
        }

        proveedorDAO.insertar(proveedor);
        return proveedor;
    }

    public List<Proveedor> listar() throws SQLException {
        return proveedorDAO.listar();
    }

    private String validarTelefono(String texto) throws ValidacionException {
        String limpio = limpiar(texto);
        if (limpio == null) {
            return null;
        }
        long digitos = limpio.chars().filter(Character::isDigit).count();
        if (!TELEFONO.matcher(limpio).matches() || digitos < MIN_DIGITOS_TELEFONO) {
            throw new ValidacionException("El teléfono no es válido.");
        }
        return limpio;
    }

    private String validarEmail(String texto) throws ValidacionException {
        String limpio = limpiar(texto);
        if (limpio == null) {
            return null;
        }
        if (!EMAIL.matcher(limpio).matches()) {
            throw new ValidacionException("El email no es válido.");
        }
        return limpio;
    }

    // Quita espacios al principio y al final; devuelve null si el texto queda vacío
    private static String limpiar(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return texto.trim();
    }
}
