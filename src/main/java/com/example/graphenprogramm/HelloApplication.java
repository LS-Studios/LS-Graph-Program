package com.example.graphenprogramm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.GregorianCalendar;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Graph graph = new Graph();

        Node a = graph.addNode("A");
        Node b = graph.addNode("B");
        Node c = graph.addNode("C");

        a.addEdge(new Edge(new Pair<>(a, true), new Pair<>(b, true), 0));
        a.addEdge(new Edge(new Pair<>(a, true), new Pair<>(c, true), 0));
        b.addEdge(new Edge(new Pair<>(b, false), new Pair<>(c, true), 0));
        c.addEdge(new Edge(new Pair<>(c, false), new Pair<>(a, true), 0));

        graph.removeNode("A");

        //ToDo when remove node, it should get removed from other edges too (see C that is still connected to A)

        System.out.println(graph.getGraph());

        launch();
    }
}