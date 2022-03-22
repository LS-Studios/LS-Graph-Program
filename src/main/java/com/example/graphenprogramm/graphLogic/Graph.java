package com.example.graphenprogramm.graphLogic;

import com.example.graphenprogramm.graphLogic.Algorithm.Algorithm;
import com.example.graphenprogramm.graphLogic.Algorithm.Dijkstra;
import com.example.graphenprogramm.graphUI.NodeUI;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Objects;

public class Graph
{
    public ArrayList<Node> nodes = new ArrayList<>();
    public Dijkstra dijkstraAlgorithm;

    /**
     * Do create the dijkstra algorithm
     */
    public Dijkstra setDijkstraAlgorithmUp(Node startNode, Node endNode) {
        dijkstraAlgorithm = new Dijkstra(nodes, startNode, endNode);
        return dijkstraAlgorithm;
    }

    /**
     * Do add a node to the graph
     */
    public Node addNode(String nodeName) {
        Node newNode = new Node(nodeName);
        nodes.add(newNode);
        return  newNode;
    }

    /**
     * Do remove the given node from the graph
     */
    public void removeNode(Node nodeToRemove) {
        nodes.remove(nodeToRemove);
    }

    /**
     * Do remove all nodes form the graph
     */
    public void removeAllNodes() {
        nodes.clear();
    }

    /**
     * Returns a node based on the given name
     */
    public Node getNode(String nodeName) {
        for (Node node : nodes) {
            if (node.getName().equals(nodeName)) {
                return node;
            }
        }

        return null;
    }

    /**
     * Do add a edge to the graph
     */
    public Edge addEdge(Node node1, Node node2) {
        Edge newEdge = new Edge(new Pair<>(node1, true), new Pair<>(node2, true), 1);
        node1.getEdges().add(newEdge);
        node2.getEdges().add(newEdge);
        return newEdge;
    }

    /**
     * Do add a edge to the graph
     */
    public Edge addEdge(Node node1, Node node2, double length) {
        Edge newEdge = new Edge(new Pair<>(node1, true), new Pair<>(node2, true), 1);
        node1.getEdges().add(newEdge);
        node2.getEdges().add(newEdge);
        newEdge.setLength(length);
        return newEdge;
    }

    /**
     * Do remove the goven edge from the graph
     */
    public void removeEdge(Edge edgeToRemove) {
        for (Node node : nodes) {
            if (node.getEdges().contains(edgeToRemove)) {
                node.removeEdge(edgeToRemove);
            }
        }
    }

    @Override
    public String toString() {
        String output = "";

        for (Node node : nodes) {
            output += node.getName() + " | ";

            if (node.getEdges().size() > 0) {
                for (Edge edge : node.getEdges()) {
                    if (edge.getNode1().getKey().equals(node)) {
                        if (edge.getNode1().getValue()) {
                            output += " <";
                        }

                        output += " -(" + edge.getLength() + ")- ";

                        if (edge.getNode2().getValue()) {
                            output += "> ";
                        }

                        output += edge.getNode2().getKey().getName();
                    } else if (edge.getNode2().getKey().equals(node)) {

                        if (edge.getNode2().getValue()) {
                            output += " <";
                        }

                        output += " -(" + edge.getLength() + ")- ";

                        if (edge.getNode1().getValue()) {
                            output += "> ";
                        }

                        output += edge.getNode1().getKey().getName();
                    }

                    output += " | ";
                }
            }

            output += "\n";
        }
        return output;
    }
}

