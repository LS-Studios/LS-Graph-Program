package com.example.graphenprogramm;

import javafx.util.Pair;

class Edge {
    public Pair<Node, Boolean> node1;
    public Pair<Node, Boolean> node2;
    public float length;

    public Edge(Pair<Node, Boolean> node1, Pair<Node, Boolean> node2, float length) {
        this.node1 = node1;
        this.node2 = node2;
        this.length = length;
    }
}
