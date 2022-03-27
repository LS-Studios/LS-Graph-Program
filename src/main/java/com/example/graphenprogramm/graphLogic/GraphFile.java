package com.example.graphenprogramm.graphLogic;

import com.example.graphenprogramm.graphUI.EdgeUI;
import com.example.graphenprogramm.graphUI.NodeUI;
import com.example.graphenprogramm.graphUI.Position;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GraphFile implements Serializable {
    private List<Node> savedNodes;
    private final boolean saveExtraInformation;

    public GraphFile(List<NodeUI> nodes, boolean saveExtraInformation) {
        this.saveExtraInformation = saveExtraInformation;
        this.savedNodes = createSavedNodes(nodes);
    }

    /**
     * Convert the given nodeUI list to independent node data
     */
    private List<Node> createSavedNodes(List<NodeUI> nodes) {
        List<Node> savedNodes = new ArrayList<>();

        List<Pair<Node, Integer>> workNodes = new ArrayList<>();

        //region Convert the nodeUIs to normal nodes and add them to the work list and save the id
        int count = 0;
        for (NodeUI nodeUI : nodes) {
            Node saveNode = new Node(nodeUI.NODE.getName());
            saveNode.setPosition(new Position(nodeUI.getLayoutX(), nodeUI.getLayoutY()));
            saveNode.setID(count++);
            if (saveExtraInformation) {
                saveNode.setSelected(nodeUI.NODE.isSelected());
                saveNode.setStartNode(nodeUI.NODE.isStartNode());
                saveNode.setEndNode(nodeUI.NODE.isEndNode());
            }
            workNodes.add(new Pair<>(saveNode, nodeUI.NODE.getID()));
        }
        //endregion

        //region Convert the edgeUIs to normal edges
        nodes.forEach(node -> node.edges.forEach(edge -> {
            NodeUI node1 = edge.getNode1();
            NodeUI node2 = edge.getNode2();

            Node workNode1 = null;
            Node workNode2 = null;

            //region Find child nodes of edge by the id
            for (Pair<Node, Integer> workNode : workNodes) {
                if (workNode.getValue() == node1.NODE.getID()) {
                    workNode1 = workNode.getKey();
                }
                if (workNode.getValue() == node2.NODE.getID()) {
                    workNode2 = workNode.getKey();
                }
            }
            //endregion

            //region Add the edge to the nodes if the connection is not already there
            if (workNode1 != null && workNode2 != null) {
                if (!workNode1.isConnectedTo(workNode2) && !workNode2.isConnectedTo(workNode1)) {
                    Edge saveNodeEdge = new Edge(workNode1, edge.EDGE.isPointToNode1(),
                                                 workNode2, edge.EDGE.isPointToNode2(),
                                                 edge.EDGE.getLength());
                    saveNodeEdge.setEdgeSide1Pos(new Position(edge.edge.getPoints().get(0), edge.edge.getPoints().get(1)));
                    saveNodeEdge.setEdgeSide2Pos(new Position(edge.edge.getPoints().get(2), edge.edge.getPoints().get(3)));
                    if (saveExtraInformation)
                        saveNodeEdge.setSelected(edge.EDGE.isSelected());
                    workNode1.getEdges().add(saveNodeEdge);
                    workNode2.getEdges().add(saveNodeEdge);
                }
            }
            //endregion
        }));
        //endregion

        //Add the nodes to the saved node list
        workNodes.forEach(workNode -> savedNodes.add(workNode.getKey()));

        return savedNodes;
    }

    /**
     * Convert the independent data back to ui data
     */
    public List<NodeUI> createUIList() {
        List<NodeUI> nodeUIs = new ArrayList<>();

        List<Pair<NodeUI, Integer>> workNodes = new ArrayList<>();

        //region Convert the nodes to nodeUIs and add them to the work list and save the id
        for (Node node : savedNodes) {
            NodeUI nodeUI = new NodeUI(node.getPosition().getX(), node.getPosition().getY());
            nodeUI.setText(node.getName());
            nodeUI.NODE = new Node(node.getName());
            nodeUI.NODE.setPosition(node.getPosition());
            nodeUI.NODE.setSelected(node.isSelected());
            nodeUI.NODE.setStartNode(node.isStartNode());
            nodeUI.NODE.setEndNode(node.isEndNode());
            workNodes.add(new Pair<>(nodeUI, node.getID()));
        }
        //endregion

        //region Convert the edges to edgeUIs
        savedNodes.forEach(node -> node.getEdges().forEach(edge -> {
            Node node1 = edge.getNode1();
            Node node2 = edge.getNode2();

            NodeUI workNode1 = null;
            NodeUI workNode2 = null;

            //region Find child nodes of edge by the id
            for (Pair<NodeUI, Integer> workNode : workNodes) {
                if (workNode.getValue() == node1.getID()) {
                    workNode1 = workNode.getKey();
                }
                if (workNode.getValue() == node2.getID()) {
                    workNode2 = workNode.getKey();
                }
            }
            //endregion

            //region Add the edge to the nodes and edgeUI if the connection is not already there
            if (workNode1 != null && workNode2 != null) {
                if (!workNode1.NODE.isConnectedTo(workNode2.NODE) && !workNode2.NODE.isConnectedTo(workNode1.NODE)) {
                    EdgeUI edgeUI = new EdgeUI(workNode1, workNode2);
                    Edge workEdge = new Edge(workNode1.NODE, edge.isPointToNode1(),
                                             workNode2.NODE, edge.isPointToNode2(),
                                             edge.getLength());
                    edgeUI.EDGE = workEdge;
                    edgeUI.EDGE.setSelected(edge.isSelected());

                    //Set weight text with check if the length is decimal or not
                    String length = String.valueOf(workEdge.getLength());
                    StringBuilder holeNumber = new StringBuilder();
                    boolean reachedComma = false;
                    StringBuilder commaNumber = new StringBuilder();
                    for (int i = 0; i < length.length(); i++) {
                        if (reachedComma) {
                            commaNumber.append(length.charAt(i));
                        } else if (length.charAt(i) != '.') {
                            holeNumber.append(length.charAt(i));
                        } else if (length.charAt(i) == '.') {
                            reachedComma = true;
                        }
                    }
                    if (Double.parseDouble(commaNumber.toString()) > 0)
                        edgeUI.contentBtn.setText(length);
                    else
                        edgeUI.contentBtn.setText(holeNumber.toString());

                    //Set arrow visibility
                    edgeUI.setArrowAVisible(edgeUI.EDGE.isPointToNode1());
                    edgeUI.setArrowBVisible(edgeUI.EDGE.isPointToNode2());

                    //Set the position of the edge points
                    edgeUI.edge.getPoints().setAll(edge.getEdgeSide1Pos().getX(), edge.getEdgeSide1Pos().getY(), edge.getEdgeSide2Pos().getX(), edge.getEdgeSide2Pos().getY());

                    //Set the position of the arrows
                    edgeUI.setArrowPositions(edge.getEdgeSide1Pos().getX(), edge.getEdgeSide1Pos().getY(), edge.getEdgeSide2Pos().getX(), edge.getEdgeSide2Pos().getY());

                    //edgeUI.update();

                    workNode1.edges.add(edgeUI);
                    workNode1.NODE.getEdges().add(edgeUI.EDGE);

                    workNode2.edges.add(edgeUI);
                    workNode2.NODE.getEdges().add(edgeUI.EDGE);
                }
            }
            //endregion
        }));
        //endregion

        //Add the nodes to the nodeUIs list
        workNodes.forEach(workNode -> nodeUIs.add(workNode.getKey()));

        return nodeUIs;
    }

    @Override
    public String toString() {
        return Graph.debugGraph(savedNodes);
    }

    //region Getter and setter
    public List<Node> getSavedNodes() {
        return savedNodes;
    }

    public void setSavedNodes(List<Node> savedNodes) {
        this.savedNodes = savedNodes;
    }
    //endregion
}
