package com.example.graphenprogramm;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Objects;

public class Graph
{
    public  ArrayList<Node> nodes = new ArrayList<>();

    public Node addNode(String nodeName) {
        Node newNode = new Node(nodeName);
        nodes.add(newNode);
        return  newNode;
    }

    public void removeNode(String nodeName) {
        nodes.remove(getNode(nodeName));
    }

    public Node getNode(String nodeName) {
        for (Node node : nodes) {
            if (Objects.equals(node.name, nodeName)) {
                return node;
            }
        }
        return  null;
    }

    public String getGraph() {
        String output = "";

        for (Node node : nodes) {
            for (Edge edge : node.edges) {
                output += edge.node1.getKey().name;

                if (edge.node1.getValue()) {
                    output += " <";
                }

                output += " - ";

                if (edge.node2.getValue()) {
                    output += "> ";
                }

                output += edge.node2.getKey().name;
            }

            output += "\n";
        }

        return output;
    }
}

