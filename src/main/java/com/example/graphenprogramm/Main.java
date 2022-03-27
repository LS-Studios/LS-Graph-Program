package com.example.graphenprogramm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        //load and create the scene with the edit screen
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("EditScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        //Prepare the stage
        stage.setTitle("Graph-Program");
        stage.getIcons().add(new Image(String.valueOf(Main.class.getResource("Icon.png"))));
        stage.setResizable(false);
        stage.setScene(scene);

        stage.show();

        mainStage = stage;
    }

    public static void main(String[] args) {
        launch();
    }
}