package com.distribuidora.dao;

import com.distribuidora.modelo.Proveedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProveedorDAO {

    private final String url;

    public ProveedorDAO() throws SQLException {
        this(ConexionDB.URL_POR_DEFECTO);
    }

    // Permite indicar otra base de datos, p. ej. un archivo temporal en las pruebas
    public ProveedorDAO(String url) throws SQLException {
        this.url = url;
        crearTablaSiNoExiste();
    }

    public void crearTablaSiNoExiste() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS proveedores (
                    id       INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre   TEXT NOT NULL,
                    contacto TEXT,
                    telefono TEXT,
                    email    TEXT
                )
                """;
        try (Connection conn = ConexionDB.obtenerConexion(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void insertar(Proveedor proveedor) throws SQLException {
        String sql = "INSERT INTO proveedores (nombre, contacto, telefono, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionDB.obtenerConexion(url);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getContacto());
            ps.setString(3, proveedor.getTelefono());
            ps.setString(4, proveedor.getEmail());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    proveedor.setId(keys.getInt(1));
                }
            }
        }
    }

    public Optional<Proveedor> consultarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, contacto, telefono, email FROM proveedores WHERE id = ?";
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
        String sql = "SELECT 1 FROM proveedores WHERE minusculas(nombre) = minusculas(?) LIMIT 1";
        try (Connection conn = ConexionDB.obtenerConexion(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Ordenados alfabéticamente, para mostrarlos en listas desplegables
    public List<Proveedor> listar() throws SQLException {
        String sql = "SELECT id, nombre, contacto, telefono, email FROM proveedores ORDER BY minusculas(nombre)";
        List<Proveedor> proveedores = new ArrayList<>();
        try (Connection conn = ConexionDB.obtenerConexion(url);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                proveedores.add(mapear(rs));
            }
        }
        return proveedores;
    }

    private Proveedor mapear(ResultSet rs) throws SQLException {
        return new Proveedor(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("contacto"),
                rs.getString("telefono"),
                rs.getString("email"));
    }
}
