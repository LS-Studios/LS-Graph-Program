package com.example.graphenprogramm;

import com.example.graphenprogramm.graphLogic.Graph;
import com.example.graphenprogramm.graphLogic.GraphFile;
import com.example.graphenprogramm.graphLogic.Node;
import com.example.graphenprogramm.graphUI.EdgeUI;
import com.example.graphenprogramm.graphUI.NodeUI;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.util.*;

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

    public static FileChooser fileChooser;
    public static ContextMenu contextMenu;
    private GraphFile copyFile;

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
                case DELETE -> deleteAll();
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

        pane.setOnDragOver(dragEvent -> {
            if (dragEvent.getDragboard().hasFiles()) {
                /* allow for both copying and moving, whatever user chooses */
                dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            dragEvent.consume();
        });

        pane.setOnDragDropped(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                for (File file : db.getFiles()) {
                    GraphFile graphFile = loadGraphFile(file);
                    if (graphFile != null)
                        createNodesWithEdges(graphFile.createUIList());
                }
                success = true;
            }
            /* let the source know whether the string was successfully
             * transferred and used */
            dragEvent.setDropCompleted(success);

            dragEvent.consume();
        });

        //Add graph
        graph = new Graph();

        //region Add grid

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

        //endregion
    }

    //region Load and save
    private void saveFile() {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Save graph");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GRAPH", "*.graph")
        );
        File saveGraphFile = fileChooser.showSaveDialog(Main.mainStage);

        saveGraphFile(saveGraphFile, new GraphFile(nodes));
    }

    private void loadFile() {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open saved graph");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GRAPH", "*.graph")
        );
        File graphFileToOpen = fileChooser.showOpenDialog(Main.mainStage);

        GraphFile graphFile = loadGraphFile(graphFileToOpen);
        if (graphFile != null)
            createNodesWithEdges(graphFile.createUIList());
    }

    /**
     * Do to the given graph file to the given file directory
     */
    private void saveGraphFile(File fileDir, GraphFile graphFile) {
        if (fileDir != null) {
            try{
                FileOutputStream fos = new FileOutputStream(fileDir.getAbsolutePath());
                ObjectOutputStream oos = new ObjectOutputStream(fos);

                oos.writeObject(graphFile);

                oos.close();
                fos.close();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Do load the graph file from the given file
     */
    private GraphFile loadGraphFile(File file) {
        GraphFile graphFile = null;
        if (file != null) {
            try{
                FileInputStream fileIn = new FileInputStream(file.getAbsolutePath());
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);

                Object obj = objectIn.readObject();

                graphFile = (GraphFile) obj;
                System.out.println("The Object has been read from the file");
                objectIn.close();
                fileIn.close();
            }
            catch(IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return graphFile;
    }
    //endregion

    //region Graph events

    public void onGraphPressed(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            node1 = createNode(event.getX(), event.getY());

            NodeUI.deselectSelectedNodes();
            EdgeUI.deselectSelectedEdges();
        }
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
            edgeUI.removeEdge();
            edgeUI = createEdge(node1, dragOverNode);
            dragOverNode = null;
        }

        //Create options context menu
        if (event.getButton() == MouseButton.SECONDARY && event.isStillSincePress()) {
            //region Algorithm menu

            Menu algorithmMenu = new Menu("Algorithm");
            MenuItem subAlgorithm1 = new MenuItem("Dijkstra path");
            MenuItem subAlgorithm2 = new MenuItem("Dijkstra progress");

            //Set dijkstra calculation up and calculate the path
            subAlgorithm1.setOnAction(actionEvent -> {
                NodeUI.deselectSelectedNodes();
                EdgeUI.deselectSelectedEdges();

                if (NodeUI.startNode != null && NodeUI.endNode != null)
                    graph.setDijkstraAlgorithmUp(NodeUI.startNode.NODE, NodeUI.endNode.NODE).showPath(200, false);
            });
            subAlgorithm2.setOnAction(actionEvent -> {
                NodeUI.deselectSelectedNodes();
                EdgeUI.deselectSelectedEdges();

                if (NodeUI.startNode != null && NodeUI.endNode != null)
                    graph.setDijkstraAlgorithmUp(NodeUI.startNode.NODE, NodeUI.endNode.NODE).showProgress(350);
            });

            algorithmMenu.getItems().addAll(subAlgorithm1, subAlgorithm2);

            //endregion

            //region Edit options

            Menu editMenu = new Menu("Edit");
            MenuItem item1 = new MenuItem("Toggle weight");
            MenuItem item2 = new MenuItem("Duplicate selected nodes");
            MenuItem item3 = new MenuItem("Select all");
            MenuItem item4 = new MenuItem("Delete all");

            //Toggle the visibility if the weights
            item1.setOnAction(actionEvent -> {
                boolean visible = !nodes.get(0).edges.get(0).isContentVisible();
                nodes.forEach(node -> node.edges.forEach(edge -> edge.setContentVisible(visible)));
            });

            item2.setOnAction(actionEvent -> {
                copyFile = new GraphFile(NodeUI.selectedNodes);
                createNodesWithEdges(copyFile.createUIList());
            });

            //Select all nodes in the graph
            item3.setOnAction(actionEvent -> selectAll());

            //Delete all nodes in the graph
            item4.setOnAction(actionEvent -> deleteAll());

            if (NodeUI.selectedNodes.size() > 0)
                editMenu.getItems().addAll(item1, item2, item3, item4);
            else
                editMenu.getItems().addAll(item1, item3, item4);

            //endregion

            //region file menu
            Menu fileMenu = new Menu("File");
            MenuItem subFile1 = new MenuItem("Save");
            MenuItem subFile2 = new MenuItem("Load");

            //Set dijkstra calculation up and calculate the path
            subFile1.setOnAction(actionEvent -> {
                saveFile();
            });

            subFile2.setOnAction(actionEvent -> {
                loadFile();
            });

            fileMenu.getItems().addAll(subFile1, subFile2);
            //endregion

            //Create the menu
            createContextMenu(Arrays.asList(algorithmMenu, editMenu, fileMenu), event.getScreenX() - 10, event.getScreenY() - 10);
        }

        node1 = null;
        node2 = null;
    }

    //endregion

    //region Create methods
    public static void createNodesWithEdges(List<NodeUI> nodesToCreate) {
        NodeUI.deselectSelectedNodes();
        EdgeUI.deselectSelectedEdges();

        for (NodeUI node : nodesToCreate) {

            //Add and select node
            addNode(node);

            if (!node.getStyleClass().contains("selected"))
                node.getStyleClass().add("selected");

            if (!NodeUI.selectedNodes.contains(node))
                NodeUI.selectedNodes.add(node);

            node.layoutXProperty().addListener((prop, oldValue, newValue) -> {
                node.NODE.getPosition().setX(newValue.doubleValue());
            });

            node.layoutYProperty().addListener((prop, oldValue, newValue) -> {
                node.NODE.getPosition().setY(newValue.doubleValue());
            });

            //Add and select edges
            for (EdgeUI edge : node.edges) {
                if (!paneReference.getChildren().contains(edge)) {
                    paneReference.getChildren().add(edge);
                }

                if (!edge.contentBtn.getStyleClass().contains("selected"))
                    edge.contentBtn.getStyleClass().add("selected");

                if (!EdgeUI.selectedEdges.contains(edge))
                    EdgeUI.selectedEdges.add(edge);
            }
        }
    }

    public static NodeUI createNode(double x, double y) {
        NodeUI node = new NodeUI(x, y);

        //Add node to graph data
        node.NODE = graph.addNode(node.getText());

        node.layoutXProperty().addListener((prop, oldValue, newValue) -> {
            node.NODE.getPosition().setX(newValue.doubleValue());
        });

        node.layoutYProperty().addListener((prop, oldValue, newValue) -> {
            node.NODE.getPosition().setY(newValue.doubleValue());
        });

        //Add node to the global list
        nodes.add(node);

        //Add node to screen
        paneReference.getChildren().add(node);

        return node;
    }

    public static NodeUI addNode(NodeUI nodeToAdd) {
        //Add node to graph data
        graph.addNode(nodeToAdd.NODE);

        //Add node to the global list
        nodes.add(nodeToAdd);

        //Add node to screen
        paneReference.getChildren().add(nodeToAdd);

        return nodeToAdd;
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

    /**
     * Select all the nodes and edges
     */
    public static void selectAll() {
        //loop through the nodes list and remove the nodes plus edges from the screen
        nodes.forEach(node -> {
            //Select the node
            if (!node.getStyleClass().contains("selected"))
                node.getStyleClass().add("selected");

            if (!NodeUI.selectedNodes.contains(node))
                NodeUI.selectedNodes.add(node);

            //Select the edge
            node.edges.filtered(edge -> {
                if (!edge.contentBtn.getStyleClass().contains("selected"))
                    edge.contentBtn.getStyleClass().add("selected");

                if (!EdgeUI.selectedEdges.contains(edge))
                    EdgeUI.selectedEdges.add(edge);

                return true;
            });
        });
    }

    /**
     * Delete all the nodes and edges
     */
    public static void deleteAll() {
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

        NodeUI.selectedNodes.clear();
        EdgeUI.selectedEdges.clear();
    }


    /**
     * Return the node ui that contains the given node
     */
    public static NodeUI getNodeUIByNode(Node node) {
        NodeUI foundNodeUI = null;

        for (NodeUI nodeUI : nodes) {
            if (nodeUI.NODE.equals(node)) {
                foundNodeUI = nodeUI;
                break;
            }
        }

        return foundNodeUI;
    }
}
