package com.example.graphenprogramm.graphLogic;

import com.example.graphenprogramm.graphLogic.Algorithm.Dijkstra;
import com.example.graphenprogramm.graphUI.NodeUI;

import java.util.ArrayList;
import java.util.List;

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
     * Do add a node with given name to the graph
     */
    public Node addNode(String nodeName) {
        Node newNode = new Node(nodeName);
        nodes.add(newNode);
        return  newNode;
    }

    /**
     * Do add a existing node to this graph
     */
    public void addNode(Node node) {
        nodes.add(node);
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
     * Returns a node based on the given id
     */
    public Node getNode(int nodeID) {
        for (Node node : nodes) {
            if (node.getID() == nodeID) {
                return node;
            }
        }

        return null;
    }

    /**
     * Do add a edge to the graph
     */
    public Edge addEdge(Node node1, Node node2) {
        Edge newEdge = new Edge(node1, true, node2, true, 1);
        node1.getEdges().add(newEdge);
        node2.getEdges().add(newEdge);
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

    /**
     * Returns a string with the debugged graph
     */
    public static String debugGraph(ArrayList<NodeUI> nodes) {
        List<Node> nodes_ = new ArrayList<>();
        nodes.forEach(nodeUI -> nodes_.add(nodeUI.NODE));
        return debugGraph(nodes_);
    }

    /**
     * Returns a string with the debugged graph
     */
    public static String debugGraph(List<Node> nodes) {
        String graphString = "";

        for (Node node : nodes) {
            graphString += node.getName() + " (" + node.getID() + ") " + " | ";

            if (node.getEdges().size() > 0) {
                for (Edge edge : node.getEdges()) {
                    if (edge.getNode1().equals(node)) {
                        if (edge.isPointToNode1()) {
                            graphString += " <";
                        }

                        graphString += " -(" + edge.getLength() + ")- ";

                        if (edge.isPointToNode2()) {
                            graphString += "> ";
                        }

                        graphString += edge.getNode2().getName();
                    } else if (edge.getNode2().equals(node)) {

                        if (edge.isPointToNode2()) {
                            graphString += " <";
                        }

                        graphString += " -(" + edge.getLength() + ")- ";

                        if (edge.isPointToNode1()) {
                            graphString += "> ";
                        }

                        graphString += edge.getNode1().getName();
                    }

                    graphString += " | ";
                }
            }

            graphString += "\n";
        }

        return graphString;
    }

    @Override
    public String toString() {
        return debugGraph(nodes);
    }
}

