package com.example.graphenprogramm;

import com.example.graphenprogramm.graphLogic.Graph;
import com.example.graphenprogramm.graphLogic.Node;
import com.example.graphenprogramm.graphUI.EdgeUI;
import com.example.graphenprogramm.graphUI.NodeUI;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public static NodeUI node1;
    public static NodeUI node2;
    public static EdgeUI edgeUI;

    public static NodeUI dragOverNode;

    public static boolean shiftPressed = false;

    public static ContextMenu contextMenu;
    private VBox shortCutPopUp;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        paneReference = pane;

        pane.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case BACK_SPACE -> {
                    if (NodeUI.selectedNodes.size() > 1) {
                        NodeUI.selectedNodes.forEach(node -> {
                            node.removeNode();
                        });
                    }
                }
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
                case SHIFT -> shiftPressed = true;
            }
        });

        pane.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.SHIFT)
                shiftPressed = false;
        });

        pane.setOnMouseMoved(mouseEvent -> {
            if (contextMenu != null)
                contextMenu.hide();

            if (shortCutPopUp != null)
                pane.getChildren().remove(shortCutPopUp);
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
        NodeUI.deselectSelectedNodes();
        EdgeUI.deselectSelectedContent();
        System.out.println(graph);
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
            node2.removeNode();
            node2 = dragOverNode;
            node2.getStyleClass().remove("draggedOver");
            edgeUI = createEdge(node1, node2);
            dragOverNode = null;
        }

        if (event.getButton() == MouseButton.SECONDARY && event.isStillSincePress()) {
            MenuItem item1 = new MenuItem("Show short cuts");
            MenuItem item2 = new MenuItem("Start algorithm");

            item1.setOnAction(actionEvent -> {
                try {
                    shortCutPopUp = FXMLLoader.load(getClass().getResource("ShortCutInfo.fxml"));
                    shortCutPopUp.getStyleClass().add("popUpPane");
                    shortCutPopUp.toFront();

                    shortCutPopUp.setLayoutX(event.getSceneX() - shortCutPopUp.getPrefWidth()/2);
                    shortCutPopUp.setLayoutY(event.getSceneY()- shortCutPopUp.getPrefHeight()/2);

                    pane.getChildren().add(shortCutPopUp);

                    shortCutPopUp.setOnMousePressed(mouseEvent -> {
                        pane.getChildren().remove(shortCutPopUp);
                        mouseEvent.consume();
                    });
                    shortCutPopUp.setOnMouseReleased(mouseEvent -> mouseEvent.consume());
                    shortCutPopUp.setOnMouseDragged(mouseEvent -> mouseEvent.consume());
                    shortCutPopUp.setOnMouseMoved(mouseEvent -> mouseEvent.consume());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            createContextMenu(Arrays.asList(item1, item2), event.getScreenX() - 10, event.getScreenY() - 10);
        }

        node1 = null;
        node2 = null;
    }

    //Create methods

    public static NodeUI createNode(double x, double y) {
        NodeUI node = new NodeUI(x, y);
        node.NODE = graph.addNode(node.getText());

        nodes.add(node);

        paneReference.getChildren().add(node);

        return node;
    }

    public static EdgeUI createEdge(NodeUI node1, NodeUI node2) {
        EdgeUI edge = new EdgeUI(node1, node2);
        edge.EDGE = graph.addEdge(node1.NODE, node2.NODE);

        node1.edges.add(edge);
        node2.edges.add(edge);

        paneReference.getChildren().add(edge);

        edge.toFront();

        return edge;
    }

    public static void createContextMenu(List<MenuItem> menuItems, double x, double y) {
        if (contextMenu != null) {
            contextMenu.hide();
        }

        contextMenu = new ContextMenu();

        contextMenu.getItems().addAll(menuItems);

        contextMenu.show(paneReference, x, y);
    }
}
