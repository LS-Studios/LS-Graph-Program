package com.example.graphenprogramm.graphLogic.Algorithm;

import com.example.graphenprogramm.Controller;
import com.example.graphenprogramm.graphLogic.Node;
import com.example.graphenprogramm.graphUI.EdgeUI;
import com.example.graphenprogramm.graphUI.NodeUI;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.*;

public class Dijkstra extends Algorithm {

    public Dijkstra(List<Node> nodes, Node startNode, Node endNode) {
        super(nodes, startNode, endNode);
    }

    /**
     * Do calculate the path from the start to the end node
     */
    public void calculate() {
        //Set the distance of the start node to 0
        startNode.setDistance(0);

        //Reset the data of all nodes
        nodes.forEach(node -> {
            node.setDistance(0);
            node.setShortestNode(null);
        });

        //Add the start node to in progress
        inProcess.add(startNode);

        //region Set the values for all nodes by looping through them and allay get the shortest possible way
        while (!inProcess.isEmpty()) {
            //region Find the shortest node in process list

            Node shortestNode = null;
            double shortestDistance = -1;
            for (Node node : inProcess) {
                if (node.getDistance() < shortestDistance || shortestDistance == -1) {
                    shortestDistance = node.getDistance();
                    shortestNode = node;
                }

            }
            //endregion

            //Remove the shortest node from the progress list
            moveToChecked(shortestNode, false);

            //region Loop though the children of the shortest node
            for (Way way : shortestNode.getChildNodes()) {
                //Only edit the children if It's not already checked
                if (!checked.contains(way.getNode())) {
                    //Calculate the new distance
                    double newDistance = shortestNode.getDistance() + way.getEdge().getLength();

                    //Add all child nodes to in progress if they not already in there and set their data
                    if (!inProcess.contains(way.getNode())) {
                        inProcess.add(way.getNode());
                        way.getNode().setDistance(newDistance);
                        way.getNode().setShortestNode(shortestNode);
                    }
                    //If the node is already contained in progress than update the data if necessary
                    else {
                        if (way.getNode().getDistance() > newDistance) {
                            way.getNode().setDistance(newDistance);
                            way.getNode().setShortestNode(shortestNode);
                        }
                    }
                }
            }
            //endregion
        }
        //endregion
    }

    /**
     * Do show the path animation
     */
    public void showPath(long animationSpeed, boolean debug) {
        animationIsPlaying = true;

        calculate();

        //List for debug
        List<String> wayString = new ArrayList<>();

        //Lists for visual animation
        List<NodeUI> nodesOnWay = new ArrayList<>();
        List<EdgeUI> edgesOnWay = new ArrayList<>();

        Node node = endNode;

        //region Add the nodes and edges on the pathway to the lists starting at the end node
        while (node != startNode && node.getShortestNode() != null) {
            //Add node to debug list
            wayString.add(node.getName());

            //Get all nodes and edges on the way and add them
            Node finalNode = node;
            Controller.nodes.forEach(nodeUI -> {
                if (nodeUI.NODE.equals(finalNode))
                    nodesOnWay.add(nodeUI);

                //Get the edges from the nodes in the path and add them to the list
                nodeUI.edges.forEach(edge -> {
                    if ((edge.EDGE.isConnectedTo(finalNode, finalNode.getShortestNode()))) {
                        if (!edgesOnWay.contains(edge))
                            edgesOnWay.add(edge);
                    }
                });
            });

            //Set the current base node as the shortest node from the current
            node = node.getShortestNode();
        }
        //endregion

        List<Object> objectsOnWay = new ArrayList<>();

        //Combine visual lists to one
        for (int i = 0; i < edgesOnWay.size(); i++) {
            objectsOnWay.add(nodesOnWay.get(i));
            objectsOnWay.add(edgesOnWay.get(i));
        }

        //Reverse the visual list
        Collections.reverse(objectsOnWay);

        //Thread to show the progress animation
        Thread animationThread = new Thread(() -> {
            for (Object nextObject : objectsOnWay) {
                NodeUI nodeUI = null;
                EdgeUI edgeUI = null;
                //region Find out if the current object is a nodeUi or edgeUi
                try {
                    nodeUI = (NodeUI) nextObject;
                } catch (Exception ignored) {
                }

                try {
                    edgeUI = (EdgeUI) nextObject;
                } catch (Exception ignored) {
                }
                //endregion

                //region Set the style of the edge/node to the path style
                if (nodeUI != null && nodeUI.NODE != endNode) {
                    nodeUI.getStyleClass().removeAll("select", "rename");
                    nodeUI.getStyleClass().add("path");
                } else if (edgeUI != null) {
                    edgeUI.contentBtn.getStyleClass().removeAll("select", "rename");
                    edgeUI.contentBtn.getStyleClass().add("path");
                    edgeUI.edge.getStyleClass().add("path");
                    edgeUI.arrowA.getStyleClass().add("path");
                    edgeUI.arrowB.getStyleClass().add("path");
                }
                //endregion

                if (objectsOnWay.get(objectsOnWay.size()-1).equals(nextObject))
                    animationIsPlaying = false;

                //Add an delay
                try {
                    Thread.sleep(animationSpeed);
                } catch (InterruptedException e) {
                }
            }
        });
        animationThread.setDaemon(true);

        if (objectsOnWay.size() > 0)
            animationThread.start();
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No valid path found!", ButtonType.OK);
            alert.showAndWait();
            animationIsPlaying = false;
            return;
        }


        //Debug the debug list if debug is true
        if (debug) {
            double overallDistance = endNode.getDistance();
            System.out.println("From " + startNode.getName() + " to " + endNode.getName());
            System.out.println("Lenght: " + overallDistance);

            Collections.reverse(wayString);

            for (int i = 0; i < wayString.size(); i++) {
                System.out.println(i + ". " + wayString.get(i));
            }
        }
    }

    /**
     * Do show to progress animation
     */
    public void showProgress(long animationSpeed) {
        animationIsPlaying = true;

        //Thread to show the progress animation
        Thread animationThread = new Thread(() -> {
            //Set the distance of the start node to 0
            startNode.setDistance(0);

            //Reset the data of all nodes
            nodes.forEach(node ->
            {
                node.setDistance(0);
                node.setShortestNode(null);
            });

            //Add the start node to in progress
            inProcess.add(startNode);

            //Set node color
            Controller.getNodeUIByNode(startNode).setNewStyle("inProgress", "button", "nodeStyle", "startNode", "endNode");

            try {
                Thread.sleep(animationSpeed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Set the values for all nodes by looping through them and allay get the shortest possible way
            while (!inProcess.isEmpty()) {
                //region Find the shortest node in process list

                Node shortestNode = null;
                double shortestDistance = -1;
                for (Node node : inProcess) {
                    if (node.getDistance() < shortestDistance || shortestDistance == -1) {
                        shortestDistance = node.getDistance();
                        shortestNode = node;
                    }

                }
                //endregion

                //Remove the shortest node from the progress list
                moveToChecked(shortestNode, true);

                //Loop though the children of the shortest node
                for (Way way : shortestNode.getChildNodes()) {
                    //Only edit the children if It's not already checked
                    if (!checked.contains(way.getNode())) {
                        //Calculate the new distance
                        double newDistance = shortestNode.getDistance() + way.getEdge().getLength();

                        //Add all child nodes to in progress if they not already in there and set their data
                        if (!inProcess.contains(way.getNode())) {
                            inProcess.add(way.getNode());
                            way.getNode().setDistance(newDistance);
                            way.getNode().setShortestNode(shortestNode);
                        }
                        //If the node is already contained in progress than update the data if necessary
                        else {
                            if (way.getNode().getDistance() > newDistance) {
                                way.getNode().setDistance(newDistance);
                                way.getNode().setShortestNode(shortestNode);
                            }
                        }
                        Controller.getNodeUIByNode(way.getNode()).setNewStyle("inProgress", "button", "nodeStyle", "startNode", "endNode");
                    }
                }
                try {
                    Thread.sleep(animationSpeed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            animationIsPlaying = false;
        });
        animationThread.setDaemon(true);
        animationThread.start();
    }

    /**
     * Moves a node that's currently in the progress list to checked
     */
    public void moveToChecked(Node nodeToMove, boolean animate) {
        if (animate)
            Controller.getNodeUIByNode(nodeToMove).setNewStyle("checked","button", "nodeStyle", "startNode", "endNode");

        checked.add(nodeToMove);
        inProcess.remove(nodeToMove);
    }
}
