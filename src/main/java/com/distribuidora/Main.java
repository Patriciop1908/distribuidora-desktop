package com.distribuidora;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent principal = FXMLLoader.load(Main.class.getResource("vista/principal.fxml"));

        Scene scene = new Scene(principal, 800, 600);

        stage.setTitle("Gestión Distribuidora");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
