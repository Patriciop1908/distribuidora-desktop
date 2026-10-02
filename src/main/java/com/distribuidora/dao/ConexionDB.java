package com.distribuidora.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionDB {

    public static final String URL_POR_DEFECTO = "jdbc:sqlite:distribuidora.db";

    private ConexionDB() {
    }

    public static Connection obtenerConexion(String url) throws SQLException {
        return DriverManager.getConnection(url);
    }
}
