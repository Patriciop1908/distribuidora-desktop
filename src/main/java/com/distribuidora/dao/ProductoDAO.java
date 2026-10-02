package com.distribuidora.dao;

import com.distribuidora.modelo.Producto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductoDAO {

    private final String url;

    public ProductoDAO() throws SQLException {
        this(ConexionDB.URL_POR_DEFECTO);
    }

    // Permite indicar otra base de datos, p. ej. un archivo temporal en las pruebas
    public ProductoDAO(String url) throws SQLException {
        this.url = url;
        crearTablaSiNoExiste();
    }

    public void crearTablaSiNoExiste() throws SQLException {
        // El precio se guarda como TEXT para conservar el valor exacto del BigDecimal
        String sql = """
                CREATE TABLE IF NOT EXISTS productos (
                    id     INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT    NOT NULL,
                    precio TEXT    NOT NULL,
                    stock  INTEGER NOT NULL
                )
                """;
        try (Connection conn = ConexionDB.obtenerConexion(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void insertar(Producto producto) throws SQLException {
        String sql = "INSERT INTO productos (nombre, precio, stock) VALUES (?, ?, ?)";
        try (Connection conn = ConexionDB.obtenerConexion(url);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getPrecio().toPlainString());
            ps.setInt(3, producto.getStock());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    producto.setId(keys.getInt(1));
                }
            }
        }
    }

    public Optional<Producto> consultarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, precio, stock FROM productos WHERE id = ?";
        try (Connection conn = ConexionDB.obtenerConexion(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    // Compara sin distinguir mayúsculas/minúsculas, incluidas letras acentuadas y la ñ
    public boolean existePorNombre(String nombre) throws SQLException {
        String sql = "SELECT 1 FROM productos WHERE minusculas(nombre) = minusculas(?) LIMIT 1";
        try (Connection conn = ConexionDB.obtenerConexion(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Producto> listar() throws SQLException {
        String sql = "SELECT id, nombre, precio, stock FROM productos ORDER BY id";
        List<Producto> productos = new ArrayList<>();
        try (Connection conn = ConexionDB.obtenerConexion(url);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productos.add(mapear(rs));
            }
        }
        return productos;
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id"),
                rs.getString("nombre"),
                new BigDecimal(rs.getString("precio")),
                rs.getInt("stock"));
    }
}
