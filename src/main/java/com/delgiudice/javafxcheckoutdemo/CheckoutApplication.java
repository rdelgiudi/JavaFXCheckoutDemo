package com.delgiudice.javafxcheckoutdemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CheckoutApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader(CheckoutApplication.class.getResource("checkout-view.fxml"));
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/checkout-view.fxml")));
        Scene scene = new Scene(root);
        stage.setTitle("Kasa");
        stage.setScene(scene);
        stage.setMinWidth(1280);
        stage.setMinHeight(720);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}