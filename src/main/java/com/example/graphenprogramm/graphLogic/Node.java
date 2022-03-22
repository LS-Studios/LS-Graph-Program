package com.example.graphenprogramm.graphLogic;

import com.example.graphenprogramm.graphLogic.Algorithm.Way;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private String name;
    private ArrayList<Edge> edges = new ArrayList<>();

    private Node shortestNode;
    private double distance = 0;

    public Node(String name) {
        this.name = name;
    }

    /**
     * Get all child nodes who are connected to this node
     */
    public List<Way> getChildNodes() {
        List<Way> childNodes = new ArrayList<>();

        for (Edge edge : edges) {
            if (edge.getNode1().getKey().equals(this)) {
                if (edge.getNode2().getValue())
                    childNodes.add(new Way(edge.getNode2().getKey(), edge));
            }
            else if (edge.getNode2().getKey().equals(this)) {
                if (edge.getNode1().getValue())
                    childNodes.add(new Way(edge.getNode1().getKey(), edge));
            }
        }

        return childNodes;
    }

    /**
     * Do remove an edge from the node
     */
    public void removeEdge(Edge edgeToRemove) {
        edges.remove(edgeToRemove);
    }

    //region Getter and setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public void setEdges(ArrayList<Edge> edges) {
        this.edges = edges;
    }

    public Node getShortestNode() {
        return shortestNode;
    }

    public void setShortestNode(Node shortestNode) {
        this.shortestNode = shortestNode;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
    //endregion
}
