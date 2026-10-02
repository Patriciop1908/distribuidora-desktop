package com.distribuidora;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent formulario = FXMLLoader.load(Main.class.getResource("vista/producto-form.fxml"));

        Scene scene = new Scene(new StackPane(formulario), 800, 600);

        stage.setTitle("Gestión Distribuidora");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
