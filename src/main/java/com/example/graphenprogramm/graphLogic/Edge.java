package com.example.graphenprogramm.graphLogic;

import javafx.util.Pair;

public class Edge {
    private Pair<Node, Boolean> node1;
    private Pair<Node, Boolean> node2;
    private double length;

    public Edge(Pair<Node, Boolean> node1, Pair<Node, Boolean> node2, float length) {
        this.node1 = node1;
        this.node2 = node2;
        this.length = length;
    }

    /**
     * Do change the connection to the given node
     */
    public void changeConnectionToNode(Node node, boolean newConnection) {
        if (node1.getKey().equals(node)) {
            node1 = new Pair<>(node, newConnection);
        } else if (node2.getKey().equals(node)) {
            node2 = new Pair<>(node, newConnection);
        }
    }


    //region Getter and setter
    public Pair<Node, Boolean> getNode1() {
        return node1;
    }

    public void setNode1(Pair<Node, Boolean> node1) {
        this.node1 = node1;
    }

    public Pair<Node, Boolean> getNode2() {
        return node2;
    }

    public void setNode2(Pair<Node, Boolean> node2) {
        this.node2 = node2;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }
    //endregion
}
