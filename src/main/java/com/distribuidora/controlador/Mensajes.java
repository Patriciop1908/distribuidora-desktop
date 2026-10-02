package com.distribuidora.controlador;

import javafx.scene.control.Label;

// Muestra mensajes de confirmación o error en los formularios, con las clases de estilos.css
final class Mensajes {

    private static final String EXITO = "mensaje-exito";
    private static final String ERROR = "mensaje-error";

    private Mensajes() {
    }

    static void mostrarExito(Label etiqueta, String mensaje) {
        mostrar(etiqueta, mensaje, EXITO);
    }

    static void mostrarError(Label etiqueta, String mensaje) {
        mostrar(etiqueta, mensaje, ERROR);
    }

    private static void mostrar(Label etiqueta, String mensaje, String clase) {
        etiqueta.getStyleClass().removeAll(EXITO, ERROR);
        etiqueta.getStyleClass().add(clase);
        etiqueta.setText(mensaje);
    }
}
