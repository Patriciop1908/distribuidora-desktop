package com.distribuidora;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Label bienvenida = new Label("Bienvenido al sistema de Gestión Distribuidora");
        bienvenida.setStyle("-fx-font-size: 20px;");

        StackPane root = new StackPane(bienvenida);
        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("Gestión Distribuidora");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
