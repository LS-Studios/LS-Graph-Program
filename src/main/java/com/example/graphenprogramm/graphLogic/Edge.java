package com.example.graphenprogramm.graphLogic;

import com.example.graphenprogramm.graphUI.Position;
import javafx.util.Pair;

import java.io.Serializable;

public class Edge implements Serializable {
    private Node node1;
    private boolean pointToNode1;

    private Node node2;
    private boolean pointToNode2;

    private double length;

    private Position edgeSide1Pos = new Position(0, 0);
    private Position edgeSide2Pos = new Position(0, 0);

    public Edge(Node node1, boolean pointToNode1, Node node2, boolean pointToNode2, double length) {
        this.node1 = node1;
        this.node2 = node2;
        this.pointToNode1 = pointToNode1;
        this.pointToNode2 = pointToNode2;
        this.length = length;
    }

    public boolean isConnectedTo(Node node1, Node node2) {
        boolean isConnected = false;
        if ((this.node1.equals(node1) && this.node2.equals(node2)) || (this.node1.equals(node2) && this.node2.equals(node1))) {
            isConnected = true;
        }

        return isConnected;
    }


    //region Getter and setter

    public Position getEdgeSide1Pos() {
        return edgeSide1Pos;
    }

    public void setEdgeSide1Pos(Position edgeSide1Pos) {
        this.edgeSide1Pos = edgeSide1Pos;
    }

    public Position getEdgeSide2Pos() {
        return edgeSide2Pos;
    }

    public void setEdgeSide2Pos(Position edgeSide2Pos) {
        this.edgeSide2Pos = edgeSide2Pos;
    }

    public Node getNode1() {
        return node1;
    }

    public void setNode1(Node node1) {
        this.node1 = node1;
    }

    public boolean isPointToNode1() {
        return pointToNode1;
    }

    public void setPointToNode1(boolean pointToNode1) {
        this.pointToNode1 = pointToNode1;
    }

    public Node getNode2() {
        return node2;
    }

    public void setNode2(Node node2) {
        this.node2 = node2;
    }

    public boolean isPointToNode2() {
        return pointToNode2;
    }

    public void setPointToNode2(boolean pointToNode2) {
        this.pointToNode2 = pointToNode2;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }
    //endregion
}
