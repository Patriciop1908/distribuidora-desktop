package com.distribuidora.dao;

import org.sqlite.Function;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public final class ConexionDB {

    public static final String URL_POR_DEFECTO = "jdbc:sqlite:distribuidora.db";

    private ConexionDB() {
    }

    public static Connection obtenerConexion(String url) throws SQLException {
        Connection conn = DriverManager.getConnection(url);
        activarClavesForaneas(conn);
        registrarFuncionMinusculas(conn);
        return conn;
    }

    // SQLite no comprueba las claves foráneas salvo que se active en cada conexión
    private static void activarClavesForaneas(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
    }

    // LOWER() y COLLATE NOCASE de SQLite solo convierten letras ASCII ("Ú" o "Ñ" no cambian),
    // así que se registra minusculas(), que usa la conversión de Java y soporta acentos y eñes
    private static void registrarFuncionMinusculas(Connection conn) throws SQLException {
        Function.create(conn, "minusculas", new Function() {
            @Override
            protected void xFunc() throws SQLException {
                String texto = value_text(0);
                result(texto == null ? null : texto.toLowerCase(Locale.ROOT));
            }
        });
    }
}
