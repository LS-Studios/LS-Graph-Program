package com.example.graphenprogramm.graphLogic;

import javafx.util.Pair;

public class Edge {
    public Pair<Node, Boolean> node1;
    public Pair<Node, Boolean> node2;
    public double length;

    public Edge(Pair<Node, Boolean> node1, Pair<Node, Boolean> node2, float length) {
        this.node1 = node1;
        this.node2 = node2;
        this.length = length;
    }

    public void changeConnectionToNode(Node node, boolean newConnection) {
        if (node1.getKey().equals(node)) {
            node1 = new Pair<>(node, newConnection);
        } else if (node2.getKey().equals(node)) {
            node2 = new Pair<>(node, newConnection);
        }
    }
}
