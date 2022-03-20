package com.example.graphenprogramm.graphLogic;

import com.example.graphenprogramm.graphUI.NodeUI;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Objects;

public class Graph
{
    public ArrayList<Node> nodes = new ArrayList<>();

    public Node addNode(String nodeName) {
        Node newNode = new Node(nodeName);
        nodes.add(newNode);
        return  newNode;
    }

    public void removeNode(Node nodeToRemove) {
        nodes.remove(nodeToRemove);
    }

    public Edge addEdge(Node node1, Node node2) {
        Edge newEdge = new Edge(new Pair<>(node1, true), new Pair<>(node2, true), 1);
        node1.edges.add(newEdge);
        node2.edges.add(newEdge);
        return newEdge;
    }

    public void removeEdge(Edge edgeToRemove) {
        for (Node node : nodes) {
            if (node.edges.contains(edgeToRemove)) {
                node.removeEdge(edgeToRemove);
            }
        }
    }

    @Override
    public String toString() {
        String output = "";

        for (Node node : nodes) {
            output += node.name + " | ";

            if (node.edges.size() > 0) {
                for (Edge edge : node.edges) {
                    if (edge.node1.getKey().equals(node)) {
                        if (edge.node1.getValue()) {
                            output += " <";
                        }

                        output += " -(" + edge.length + ")- ";

                        if (edge.node2.getValue()) {
                            output += "> ";
                        }

                        output += edge.node2.getKey().name;
                    } else if (edge.node2.getKey().equals(node)) {

                        if (edge.node2.getValue()) {
                            output += " <";
                        }

                        output += " -(" + edge.length + ")- ";

                        if (edge.node1.getValue()) {
                            output += "> ";
                        }

                        output += edge.node1.getKey().name;
                    }

                    output += " | ";
                }
            }

            output += "\n";
        }
        return output;
    }
}

