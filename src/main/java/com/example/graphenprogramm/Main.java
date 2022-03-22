package com.example.graphenprogramm;

import com.example.graphenprogramm.graphLogic.Algorithm.Algorithm;
import com.example.graphenprogramm.graphLogic.Algorithm.Dijkstra;
import com.example.graphenprogramm.graphLogic.Edge;
import com.example.graphenprogramm.graphLogic.Graph;
import com.example.graphenprogramm.graphLogic.Node;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application {
    public static ArrayList<Pair<javafx.scene.Node, Node>> globalNodes = new ArrayList<>();
    public static ArrayList<Pair<javafx.scene.Node, Edge>> globalEdges = new ArrayList<>();

    public static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("EditScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Graphen-Programm");

        stage.setResizable(false);

        stage.setScene(scene);

        stage.show();

        mainStage = stage;
    }

    public static void main(String[] args) {
        launch();
    }
}