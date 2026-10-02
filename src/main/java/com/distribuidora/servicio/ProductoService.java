package com.distribuidora.servicio;

import com.distribuidora.dao.ProductoDAO;
import com.distribuidora.modelo.Producto;

import java.math.BigDecimal;
import java.sql.SQLException;

public class ProductoService {

    private static final int DECIMALES_PRECIO = 2;

    private final ProductoDAO productoDAO;

    public ProductoService(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    /**
     * Valida los datos del formulario y guarda el producto.
     * Acepta coma o punto como separador decimal en el precio.
     * Rechaza nombres ya registrados, sin distinguir mayúsculas/minúsculas.
     */
    public Producto registrar(String nombre, String precioTexto, String stockTexto)
            throws ValidacionException, SQLException {
        return registrar(nombre, precioTexto, stockTexto, null);
    }

    /**
     * Igual que {@link #registrar(String, String, String)}, asociando el producto a un proveedor.
     * proveedorId puede ser null si el producto no tiene proveedor.
     */
    public Producto registrar(String nombre, String precioTexto, String stockTexto, Integer proveedorId)
            throws ValidacionException, SQLException {
        String nombreLimpio = nombre == null ? "" : nombre.trim();
        if (nombreLimpio.isEmpty()) {
            throw new ValidacionException("El nombre es obligatorio.");
        }

        Producto producto = new Producto(
                nombreLimpio, validarPrecio(precioTexto), validarStock(stockTexto), proveedorId);

        if (productoDAO.existePorNombre(nombreLimpio)) {
            throw new ValidacionException("Ya existe un producto con el nombre \"" + nombreLimpio + "\".");
        }

        productoDAO.insertar(producto);
        return producto;
    }

    private BigDecimal validarPrecio(String texto) throws ValidacionException {
        String limpio = texto == null ? "" : texto.trim().replace(',', '.');
        if (limpio.isEmpty()) {
            throw new ValidacionException("El precio es obligatorio.");
        }

        BigDecimal precio;
        try {
            precio = new BigDecimal(limpio);
        } catch (NumberFormatException e) {
            throw new ValidacionException("El precio no es un número válido.");
        }
        if (precio.signum() < 0) {
            throw new ValidacionException("El precio no puede ser negativo.");
        }
        if (precio.stripTrailingZeros().scale() > DECIMALES_PRECIO) {
            throw new ValidacionException("El precio admite como máximo " + DECIMALES_PRECIO + " decimales.");
        }
        return precio.setScale(DECIMALES_PRECIO);
    }

    private int validarStock(String texto) throws ValidacionException {
        String limpio = texto == null ? "" : texto.trim();
        if (limpio.isEmpty()) {
            throw new ValidacionException("El stock es obligatorio.");
        }

        int stock;
        try {
            stock = Integer.parseInt(limpio);
        } catch (NumberFormatException e) {
            throw new ValidacionException("El stock debe ser un número entero.");
        }
        if (stock < 0) {
            throw new ValidacionException("El stock no puede ser negativo.");
        }
        return stock;
    }
}
