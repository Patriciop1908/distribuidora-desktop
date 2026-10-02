package com.distribuidora.servicio;

// Error de datos introducidos por el usuario; el mensaje se muestra tal cual en la interfaz
public class ValidacionException extends Exception {

    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
