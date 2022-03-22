package com.example.graphenprogramm;

import com.example.graphenprogramm.graphLogic.Graph;
import com.example.graphenprogramm.graphUI.EdgeUI;
import com.example.graphenprogramm.graphUI.NodeUI;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.layout.*;

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
    public GridPane bgGrid;

    //Variables
    public static Graph graph;

    public static ArrayList<NodeUI> nodes = new ArrayList<>();

    public static NodeUI node1;
    public static NodeUI node2;
    public static EdgeUI edgeUI;

    public static NodeUI dragOverNode;

    public static boolean shiftPressed = false;
    public static boolean controlPressed = false;

    public static ContextMenu contextMenu;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        paneReference = pane;

        //Global graph input
        pane.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case BACK_SPACE -> {
                    if (NodeUI.selectedNodes.size() > 1) {
                        NodeUI.selectedNodes.forEach(NodeUI::removeNode);
                    }
                }
                case DELETE -> deleteAllNodes();
                case SHIFT -> shiftPressed = true;
                case CONTROL -> controlPressed = true;
            }
        });

        pane.setOnKeyReleased(keyEvent -> {
            switch (keyEvent.getCode()) {
                case SHIFT -> shiftPressed = false;
                case CONTROL -> controlPressed = false;
            }
        });

        pane.setOnMouseMoved(mouseEvent -> {
            if (contextMenu != null)
                contextMenu.hide();
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

    //region Graph events

    public void onGraphPressed(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            node1 = createNode(event.getX(), event.getY());
        }
        NodeUI.deselectSelectedNodes();
        EdgeUI.deselectSelectedEdges();
    }

    public void onGraphDragDetected(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            node2 = createNode(event.getX(), event.getY());
            node2.toBack();
            edgeUI = createEdge(node1, node2);
            node2.startFullDrag();
        }
    }

    public void onGraphDragged(MouseEvent event) {
        if (node2 != null) {
            if (event.getSceneX() > 0 && event.getSceneX() < Main.mainStage.getWidth()-16)
                node2.setLayoutX(event.getX());

            if (event.getSceneY() > 0 && event.getSceneY() < Main.mainStage.getHeight()-39)
                node2.setLayoutY(event.getY());
        }
    }

    public void onGraphReleased(MouseEvent event) {
        if (node2 != null) {
            node2.toFront();
            node2.setVisible(true);
        }

        //Add edge between drag from and dragged over node
        if (node2 != null && event.getButton() == MouseButton.PRIMARY && dragOverNode != null) {
            node2.removeNode();
            dragOverNode.getStyleClass().remove("draggedOver");
            edgeUI = createEdge(node1, dragOverNode);
            dragOverNode = null;
        }

        //Create options context menu
        if (event.getButton() == MouseButton.SECONDARY && event.isStillSincePress()) {
            //region Algorithm menu

            Menu algorithmMenu = new Menu("Algorithm");
            MenuItem subAlgorithm1 = new MenuItem("Dijkstra");

            //Set dijkstra calculation up and calculate the path
            subAlgorithm1.setOnAction(actionEvent -> {
                if (NodeUI.startNode != null && NodeUI.endNode != null) {
                    graph.setDijkstraAlgorithmUp(NodeUI.startNode.NODE, NodeUI.endNode.NODE).calculate(false);
                }
            });

            algorithmMenu.getItems().addAll(subAlgorithm1);

            //endregion

            //region Edit options

            MenuItem item1 = new MenuItem("Toggle weight");
            MenuItem item2 = new MenuItem("Delete all");

            //Toggle the visibility if the weights
            item1.setOnAction(actionEvent -> {
                boolean visible = !nodes.get(0).edges.get(0).isContentVisible();
                nodes.forEach(node -> node.edges.forEach(edge -> edge.setContentVisible(visible)));
            });

            //Delete all nodes in graph
            item2.setOnAction(actionEvent -> deleteAllNodes());

            //endregion

            //Create the menu
            createContextMenu(Arrays.asList(algorithmMenu, item1, item2), event.getScreenX() - 10, event.getScreenY() - 10);
        }

        node1 = null;
        node2 = null;
    }

    //endregion

    //region Create methods

    public static NodeUI createNode(double x, double y) {
        NodeUI node = new NodeUI(x, y);

        //Add node to graph data
        node.NODE = graph.addNode(node.getText());

        //Add node to the global list
        nodes.add(node);

        //Add node to screen
        paneReference.getChildren().add(node);

        return node;
    }

    public static EdgeUI createEdge(NodeUI node1, NodeUI node2) {
        EdgeUI edge = new EdgeUI(node1, node2);

        //Add edge to graph data
        edge.EDGE = graph.addEdge(node1.NODE, node2.NODE);

        //Add edges to the nodes
        node1.edges.add(edge);
        node2.edges.add(edge);

        //Add edge to screen
        paneReference.getChildren().add(edge);

        return edge;
    }

    public static void createContextMenu(List<MenuItem> menuItems, double x, double y) {
        //Hide existing context menu
        if (contextMenu != null) {
            contextMenu.hide();
        }

        contextMenu = new ContextMenu();

        //Add all given items to created context menu
        contextMenu.getItems().addAll(menuItems);

        //Show the context menu
        contextMenu.show(paneReference, x, y);
    }

    //endregion

    public static void deleteAllNodes() {
        //loop through the nodes list and remove the nodes plus edges from the screen
        nodes.forEach(node -> {
            paneReference.getChildren().remove(node);
            for (EdgeUI edge : node.edges) {
                paneReference.getChildren().remove(edge);
            }
            node.edges.clear();
        });

        //Remove nodes from graph data
        graph.removeAllNodes();

        //Reset the node count
        NodeUI.count = 0;
        nodes.clear();
    }
}
