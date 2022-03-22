package com.example.graphenprogramm.graphLogic.Algorithm;

import com.example.graphenprogramm.graphLogic.Edge;
import com.example.graphenprogramm.graphLogic.Node;
import javafx.beans.NamedArg;

public class Way {
    private Node node;
    private Edge edge;

    public Node getNode() {
        return this.node;
    }

    public Edge getEdge() {
        return this.edge;
    }

    public Way(Node node, Edge edge) {
        this.node = node;
        this.edge = edge;
    }
}
