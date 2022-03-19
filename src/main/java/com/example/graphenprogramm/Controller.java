package com.example.graphenprogramm;

import com.example.graphenprogramm.graphLogic.Graph;
import com.example.graphenprogramm.graphUI.EdgeUI;
import com.example.graphenprogramm.graphUI.NodeUI;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    public Pane pane;
    public static Pane paneReference;

    @FXML
    public ScrollPane scrollPane;

    @FXML
    public GridPane bgGrid;

    //Variables
    private double x,y;
    public static Graph graph;

    public static ArrayList<NodeUI> nodes = new ArrayList<>();

    private NodeUI node1;
    private NodeUI node2;
    private EdgeUI edgeUI;
    private NodeUI nodeToDelete;

    private NodeUI dragOverNode;

    private NodeUI selectedNode;

    private boolean removeText = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        paneReference = pane;

        pane.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case DELETE -> {
                    //Remove all nodes
                    for (NodeUI node : nodes) {
                        pane.getChildren().remove(node);
                        for (EdgeUI edge : node.edges) {
                            pane.getChildren().remove(edge);
                        }
                        node.edges.clear();
                    }
                    NodeUI.count = 0;
                    nodes.clear();
                }
            }
        });

        //Add graph
        graph = new Graph();

        //Add grid
        final int numCols = 100;
        final int numRows = 100;
        for (int i = 0; i < numCols; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(numCols);
            bgGrid.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < numRows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(numRows);
            bgGrid.getRowConstraints().add(rowConst);
        }
    }

    //Graph
    public void onGraphPressed(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            node1 = createNode(event.getX(), event.getY());
        }

        deselectSelectedNode();
        EdgeUI.deselectSelectedEdge();
        graph.getGraph();
    }

    public void onGraphDragDetected(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            node2 = createNode(event.getX(), event.getY());
            node2.toBack();
            node2.startFullDrag();
            edgeUI = createEdge(node1, node2);
        }
    }

    public void onGraphDragged(MouseEvent event) {
        if (node2 != null) {
            node2.setLayoutX(event.getX());
            node2.setLayoutY(event.getY());
        }
    }

    public void onGraphReleased(MouseEvent event) {
        if (node2 != null) {
            node2.toFront();
            node2.setVisible(true);
        }

        if (event.getButton() == MouseButton.PRIMARY && dragOverNode != null) {
            removeNode(node2);
            node2 = dragOverNode;
            node2.getStyleClass().remove("draggedOver");
            edgeUI = createEdge(node1, node2);
            dragOverNode = null;
        }
        node1 = null;
        node2 = null;
    }

    //Node events
    private void onNodePressed(MouseEvent mouseEvent, NodeUI node) {
        if (mouseEvent.isPrimaryButtonDown()) {
            selectNode(node);
        }
        if (mouseEvent.isSecondaryButtonDown())
            nodeToDelete = node;
    }

    private void onNodeReleased(MouseEvent mouseEvent, NodeUI node) {
        if (nodeToDelete != null) {
            removeNode(nodeToDelete);
        }

        if (node2 != null) {
            node2.toFront();
            node2.setVisible(true);
        }

        if (mouseEvent.getButton() == MouseButton.PRIMARY && dragOverNode != null) {
            removeNode(node2);
            dragOverNode.getStyleClass().remove("draggedOver");

            createEdge(dragOverNode, node);
            dragOverNode = null;
        }

        node2 = null;
        nodeToDelete = null;
    }

    private void onNodeDragDetected(MouseEvent e, NodeUI node) {
        if (e.isPrimaryButtonDown()) {
            node1 = node;
            node2 = createNode(node.getLayoutX() + e.getX() + node.getTranslateX(),
                                node.getLayoutY() + e.getY() + node.getTranslateY());
            node2.toBack();
            edgeUI = createEdge(node, node2);
        } else if (e.isSecondaryButtonDown()) {
            nodeToDelete = null;
        }

        node.startFullDrag();
    }

    private void onNodeDragEntered(MouseDragEvent dragEvent, NodeUI node) {
        if (node2 != null && node2 != node) {
            node2.setVisible(false);

            boolean canConnect = true;

            for (EdgeUI edge : node.edges) {
                if (edge.getNode1().equals(node1)
                    || edge.getNode2().equals(node1)) {
                    canConnect = false;
                }
            }

            if (canConnect) {
                node.getStyleClass().add("draggedOver");
                dragOverNode = node;
            } else {
                dragOverNode = null;
                node.getStyleClass().add("draggedOverError");
            }

        }
    }

    private void onNodeDragExited(MouseDragEvent dragEvent, NodeUI node) {
        if (node2 != null)
            node2.setVisible(true);

        dragOverNode = null;

        node.getStyleClass().remove("draggedOver");
        node.getStyleClass().remove("draggedOverError");
    }

    private void onNodeDragged(MouseEvent e, Button node) {
        nodeToDelete = null;

        if (node2 != null) {
            //tempNode2.toFront();
            node2.setLayoutX(node.getLayoutX() + e.getX() + node.getTranslateX());
            node2.setLayoutY(node.getLayoutY() + e.getY() + node.getTranslateY());
        }
        if (e.isSecondaryButtonDown()) {
            node.setLayoutX(node.getLayoutX() + e.getX() + node.getTranslateX());
            node.setLayoutY(node.getLayoutY() + e.getY() + node.getTranslateY());
        }
    }

    //Node actions

    private void selectNode(NodeUI nodeToSelect) {
        removeText = true;

        if (selectedNode != null) {
            selectedNode.setOnKeyPressed(null);
            selectedNode.getStyleClass().remove("selected");
        }

        selectedNode = nodeToSelect;
        selectedNode.getStyleClass().add("selected");

        nodeToSelect.setOnKeyPressed(keyEvent -> setNodeText(keyEvent, nodeToSelect));
    }

    private void deselectSelectedNode() {
        if (selectedNode != null) {
            selectedNode.setOnKeyPressed(null);
            selectedNode.getStyleClass().remove("selected");
            selectedNode = null;
        }
    }

    private void setNodeText(KeyEvent keyEvent, NodeUI node) {
        String text = node.getText();

        if (removeText) {
            node.setText("");
            removeText = false;
            setNodeText(keyEvent, node);
            return;
        }

        switch (keyEvent.getCode()) {
            case BACK_SPACE -> text = deleteChar(text);
            case SPACE -> text += " ";
            default -> {
                if (keyEvent.getCode().isLetterKey() || keyEvent.getCode().isDigitKey()) {
                    if (keyEvent.isShiftDown())
                        text += keyEvent.getCode().getChar().toUpperCase();
                    else
                        text += keyEvent.getCode().getChar().toLowerCase();
                }
            }
        }

        node.setText(text);

        for (EdgeUI edge : node.edges) {
            edge.update();
        }
    }

    private String deleteChar(String text) {
        String newText = text;

        if (text.length() > 0) {
            newText = newText.substring(0, text.length()-1);
        }

        return newText;
    }

    //Remove methods

    private void removeNode(NodeUI nodeToRemove) {
        pane.getChildren().remove(nodeToRemove);

        for (EdgeUI edge : nodeToRemove.edges) {
            pane.getChildren().remove(edge);
            graph.removeEdge(edge.EDGE);
        }

        graph.removeNode(nodeToRemove.NODE);
        nodeToRemove.edges.clear();
        nodes.remove(nodeToRemove);

        NodeUI.count--;
    }

    public static void removeEdge(EdgeUI edge) {
        paneReference.getChildren().remove(edge);
        edge.getNode1().edges.remove(edge);
        edge.getNode2().edges.remove(edge);
        graph.removeEdge(edge.EDGE);
    }

    //Create methods

    private NodeUI createNode(double x, double y) {
        NodeUI node = new NodeUI(x, y);
        node.NODE = graph.addNode(node.getText());

        node.setOnMousePressed(mouseEvent -> onNodePressed(mouseEvent, node));
        node.setOnDragDetected(mouseEvent -> onNodeDragDetected(mouseEvent, node));
        node.setOnMouseDragged(mouseEvent -> onNodeDragged(mouseEvent, node));
        node.setOnMouseReleased(mouseEvent -> onNodeReleased(mouseEvent, node));
        node.setOnMouseDragEntered(mouseDragEvent -> onNodeDragEntered(mouseDragEvent, node));
        node.setOnMouseDragExited(mouseDragEvent -> onNodeDragExited(mouseDragEvent, node));

        nodes.add(node);

        pane.getChildren().add(node);

        return node;
    }

    private EdgeUI createEdge(NodeUI node1, NodeUI node2) {
        EdgeUI edge = new EdgeUI(node1, node2);
        edge.EDGE = graph.addEdge(node1.NODE, node2.NODE);

        node1.edges.add(edge);
        node2.edges.add(edge);

        pane.getChildren().add(edge);

        edge.toFront();

        return edge;
    }
}
