/*
 * This product was created by Lennard Stubbe
 * and is licensed under the CC BY-NC-SA license.
 * Thus, the product may be changed and shared as desired,
 * but not for commercial use.
 */

package com.example.graphenprogramm;

import com.example.graphenprogramm.graphLogic.Algorithm.Algorithm;
import com.example.graphenprogramm.graphLogic.Graph;
import com.example.graphenprogramm.graphLogic.GraphFile;
import com.example.graphenprogramm.graphLogic.Node;
import com.example.graphenprogramm.graphUI.EdgeUI;
import com.example.graphenprogramm.graphUI.NodeUI;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    //region Variables
    @FXML
    public Pane pane;
    public static Pane paneReference;

    @FXML
    public GridPane bgGrid;

    //region Menu pane references

    @FXML
    public VBox menuPane;
    @FXML
    public AnchorPane editPane;
    @FXML
    public AnchorPane algorithmPane;
    @FXML
    public AnchorPane graphPane;
    @FXML
    public AnchorPane filePane;

    //endregion

    //region To menu pane button references

    @FXML
    public Button editMenuBtn;
    @FXML
    public Button algorithmMenuBtn;
    @FXML
    public Button graphMenuBtn;
    @FXML
    public Button fileMenuBtn;
    @FXML
    public Button infoMenuBtn;
    //endregion

    //region Menu button references

    @FXML
    public Button renameSelectedBtn;
    public static Button renameBtnReference;
    @FXML
    public Button deleteSelectedBtn;
    @FXML
    public Button selectAllBtn;
    @FXML
    public Button deleteAllBtn;
    @FXML
    public Button setStartNodeBtn;
    @FXML
    public Button setEndNodeBtn;
    @FXML
    public Button startDijkstraPathBtn;
    @FXML
    public Button startDijkstraProgressBtn;
    @FXML
    public Button undoBtn;
    @FXML
    public Button redoBtn;
    @FXML
    public Button copyBtn;
    @FXML
    public Button pasteBtn;
    @FXML
    public Button duplicateSelectedBtn;
    @FXML
    public Button toggleWeightBtn;
    @FXML
    public Button saveGraphBtn;
    @FXML
    public Button loadGraphBtn;

    //endregion

    //region Graph references

    public static Graph graph;

    public static ArrayList<NodeUI> nodes = new ArrayList<>();

    public static NodeUI node1;
    public static NodeUI node2;
    public static EdgeUI edgeUI;

    public static NodeUI dragOverNode;

    private GraphFile duplicationFile;
    private GraphFile copyFile;
    public static List<GraphFile> undoFiles = new ArrayList<>();
    public static List<GraphFile> redoFiles = new ArrayList<>();

    //endregion
    //endregion

    //region Logic
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Set the static references
        paneReference = pane;
        renameBtnReference = renameSelectedBtn;

        //region Global input

        //region Graph pane input
        pane.setOnKeyPressed(this::globalOnKeyPressed);
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
                        createNodesWithEdges(graphFile.createUIList(), true);
                }
                success = true;
            }
            /* let the source know whether the string was successfully
             * transferred and used */
            dragEvent.setDropCompleted(success);

            dragEvent.consume();
        });
        //endregion

        //region Menu pane input
        menuPane.setOnMousePressed(mouseEvent -> nodes.forEach(node -> {
            node.removeState("rename");
            node.edges.forEach(edge -> edge.removeState(edge.contentBtn, "rename"));
        }));
        menuPane.setOnKeyPressed(this::globalOnKeyPressed);
        //endregion

        //endregion

        //region Toggle menu panes

        //region Set all panes to not visible except for the edit pane
        editPane.setVisible(true);
        algorithmPane.setVisible(false);
        graphPane.setVisible(false);
        filePane.setVisible(false);
        //endregion

        //region Toggle the respective menu pane
        editMenuBtn.setOnAction(actionEvent -> {
            //Stop renaming
            nodes.forEach(node -> {
                node.removeState("rename");
                node.edges.forEach(edge -> edge.removeState(edge.contentBtn, "rename"));
            });

            editPane.setVisible(true);
            algorithmPane.setVisible(false);
            graphPane.setVisible(false);
            filePane.setVisible(false);
        });
        algorithmMenuBtn.setOnAction(actionEvent -> {
            //Stop renaming
            nodes.forEach(node -> {
                node.removeState("rename");
                node.edges.forEach(edge -> edge.removeState(edge.contentBtn, "rename"));
            });

            editPane.setVisible(false);
            algorithmPane.setVisible(true);
            graphPane.setVisible(false);
            filePane.setVisible(false);
        });
        graphMenuBtn.setOnAction(actionEvent -> {
            //Stop renaming
            nodes.forEach(node -> {
                node.removeState("rename");
                node.edges.forEach(edge -> edge.removeState(edge.contentBtn, "rename"));
            });

            editPane.setVisible(false);
            algorithmPane.setVisible(false);
            graphPane.setVisible(true);
            filePane.setVisible(false);
        });
        fileMenuBtn.setOnAction(actionEvent -> {
            //Stop renaming
            nodes.forEach(node -> {
                node.removeState("rename");
                node.edges.forEach(edge -> edge.removeState(edge.contentBtn, "rename"));
            });

            editPane.setVisible(false);
            algorithmPane.setVisible(false);
            graphPane.setVisible(false);
            filePane.setVisible(true);
        });
        infoMenuBtn.setOnAction(actionEvent -> {
            Dialog<ButtonType> dialog = new Alert(Alert.AlertType.INFORMATION,
                    """
                            Shortcuts:

                            Create node: Press/Drag left mouse button
                            Join/Move nodes: Drag right mouse button
                            Select: Press left mouse button
                            Select multiple: SHIFT + left mouse button
                            Deselect single: STRG + left mouse button

                            Deselect all: Press right mouse button

                            Rename selected: SHIFT + R

                            Delete selected: Back space
                            Delete all: ENTF

                            Select all: STRG + A

                            Set selected as start node: SHIFT + S
                            Set selected as end node: SHIFT + E

                            Start dijkstra path animation: SHIFT + P
                            Start dijkstra progress animation: ALT + P

                            Undo action: STRG + Z
                            Redo action: STRG + Y

                            Duplicate selected: SHIFT + D

                            Copy selected: STRG + C
                            Past selected: STRG + V

                            Toggle weights: SHIFT + T

                            Save file: STRG + S
                            Load file: STRG + O

                            Snap to local grid: Drag + STRG
                            Snap to global grid: Drag + ALT""",
                    ButtonType.CLOSE);
            dialog.setTitle("Information");
            dialog.setHeaderText("Graph-Program created by Lennard Stubbe");
            dialog.setGraphic(null);
            dialog.showAndWait();
        });
        //endregion

        //endregion

        //region Menu button actions

        //region Edit menu
        renameSelectedBtn.setOnAction(actionEvent -> {
            nodes.forEach(node -> {
                node.removeState("rename");
                node.edges.forEach(edge -> edge.removeState(edge.contentBtn, "rename"));
            });
            renameSelected(renameSelectedBtn);
        });
        renameSelectedBtn.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                nodes.forEach(node -> {
                    node.removeState("rename");

                    node.edges.forEach(edge -> edge.removeState(edge.contentBtn, "rename"));
                });
            }
        });

        deleteSelectedBtn.setOnAction(actionEvent -> {
            //Add to undo backup files
            undoFiles.add(new GraphFile(nodes, true));

            while (NodeUI.selectedNodes.size() > 0) {
                NodeUI.selectedNodes.get(NodeUI.selectedNodes.size()-1).removeNode();
            }
            while (EdgeUI.selectedEdges.size() > 0) {
                EdgeUI.selectedEdges.get(EdgeUI.selectedEdges.size()-1).removeEdge();
            }
        });
        selectAllBtn.setOnAction(actionEvent -> {
            //Stop renaming
            nodes.forEach(node -> {
                node.removeState("rename");
                node.edges.forEach(edge -> edge.removeState(edge.contentBtn, "rename"));
            });

            selectAll();
        });
        deleteAllBtn.setOnAction(actionEvent -> {
            //Stop renaming
            nodes.forEach(node -> {
                node.removeState("rename");
                node.edges.forEach(edge -> edge.removeState(edge.contentBtn, "rename"));
            });

            deleteAll(true);
        });
        //endregion

        //region Algorithm menu
        setStartNodeBtn.setOnAction(actionEvent -> {
            //Stop renaming
            nodes.forEach(node -> {
                node.removeState("rename");
                node.edges.forEach(edge -> edge.removeState(edge.contentBtn, "rename"));
            });

            setStartNode();
        });
        setEndNodeBtn.setOnAction(actionEvent -> {
            //Stop renaming
            nodes.forEach(node -> {
                node.removeState("rename");
                node.edges.forEach(edge -> edge.removeState(edge.contentBtn, "rename"));
            });

            setEndNode();
        });
        startDijkstraPathBtn.setOnAction(actionEvent -> {
            //Stop renaming
            nodes.forEach(node -> {
                node.removeState("rename");
                node.edges.forEach(edge -> edge.removeState(edge.contentBtn, "rename"));
            });

            startDijkstraPath();
        });
        startDijkstraProgressBtn.setOnAction(actionEvent -> {
            //Stop renaming
            nodes.forEach(node -> {
                node.removeState("rename");
                node.edges.forEach(edge -> edge.removeState(edge.contentBtn, "rename"));
            });

            startDijkstraProgress();
        });
        //endregion

        //region Graph menu
        undoBtn.setOnAction(actionEvent -> undo());
        redoBtn.setOnAction(actionEvent -> redo());
        copyBtn.setOnAction(actionEvent -> copyFile = new GraphFile(nodes, false));
        pasteBtn.setOnAction(actionEvent -> {
            if (copyFile != null) {
                //Add to undo backup files
                undoFiles.add(new GraphFile(nodes, true));

                createNodesWithEdges(copyFile.createUIList(), false);
            }
        });
        duplicateSelectedBtn.setOnAction(actionEvent -> {
            //Add to undo backup files
            undoFiles.add(new GraphFile(nodes, true));

            //Stop renaming
            nodes.forEach(node -> {
                node.removeState("rename");
                node.edges.forEach(edge -> edge.removeState(edge.contentBtn, "rename"));
            });

            duplicationFile = new GraphFile(NodeUI.selectedNodes, false);
            createNodesWithEdges(duplicationFile.createUIList(), false);
        });
        toggleWeightBtn.setOnAction(actionEvent -> {
            //Stop renaming
            nodes.forEach(node -> {
                node.removeState("rename");
                node.edges.forEach(edge -> edge.removeState(edge.contentBtn, "rename"));
            });

            boolean visible = !nodes.get(0).edges.get(0).isContentVisible();
            nodes.forEach(node -> node.edges.forEach(edge -> edge.setContentVisible(visible)));
        });
        //endregion

        //region File menu
        saveGraphBtn.setOnAction(actionEvent -> {
            //Stop renaming
            nodes.forEach(node -> {
                node.removeState("rename");
                node.edges.forEach(edge -> edge.removeState(edge.contentBtn, "rename"));
            });

            saveFile();
        });
        loadGraphBtn.setOnAction(actionEvent -> {
            //Stop renaming
            nodes.forEach(node -> {
                node.removeState("rename");
                node.edges.forEach(edge -> edge.removeState(edge.contentBtn, "rename"));
            });

            loadFile();
        });
        //endregion

        //endregion

        //region Add grid

        //Define the grid size
        final int numCols = 100;
        final int numRows = 100;

        //Add the column cells to the grid
        for (int i = 0; i < numCols; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(numCols);
            bgGrid.getColumnConstraints().add(colConst);
        }

        //Add the row cells to the grid
        for (int i = 0; i < numRows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(numRows);
            bgGrid.getRowConstraints().add(rowConst);
        }

        //endregion

        //Add graph
        graph = new Graph();
    }

    //region Global key input
    private void globalOnKeyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            //Delete selected shortcut
            case BACK_SPACE -> {
                //Add to undo backup files
                undoFiles.add(new GraphFile(nodes, true));

                if (NodeUI.selectedNodes.size() > 0) {
                    List<NodeUI> nodesToRemove = new ArrayList<>(NodeUI.selectedNodes);
                    nodesToRemove.forEach(NodeUI::removeNode);
                }
                if (EdgeUI.selectedEdges.size() > 0) {
                    List<EdgeUI> edgesToRemove = new ArrayList<>(EdgeUI.selectedEdges);
                    edgesToRemove.forEach(EdgeUI::removeEdge);
                }
            }
            //Delete all shortcut
            case DELETE -> deleteAll(true);
            //Select all shortcut
            case A -> {
                if (keyEvent.isControlDown()) {
                    selectAll();
                }
            }
            //Duplicate shortcut
            case D -> {
                if (keyEvent.isShiftDown()) {
                    //Add to undo backup files
                    undoFiles.add(new GraphFile(nodes, true));

                    duplicationFile = new GraphFile(NodeUI.selectedNodes, false);
                    createNodesWithEdges(duplicationFile.createUIList(), false);
                }
            }
            //Set end node shortcut
            case E -> {
                if (keyEvent.isShiftDown()) {
                    undoFiles.add(new GraphFile(nodes, true));
                    setEndNode();
                }
            }
            //Save + start node shortcut
            case S -> {
                if (keyEvent.isControlDown()) {
                    saveFile();
                } else if (keyEvent.isShiftDown()) {
                    setStartNode();
                }
            }
            //Open shortcut
            case O -> {
                if (keyEvent.isControlDown()) {
                    loadFile();
                }
            }
            //Rename shortcut
            case R -> {
                if (keyEvent.isShiftDown()) {
                    if (NodeUI.selectedNodes.size() > 0 || EdgeUI.selectedEdges.size() > 0) {
                        renameSelected(null);
                    }
                }
            }
            //Toggle weights shortcut
            case T -> {
                if (keyEvent.isShiftDown()) {
                    boolean visible = !nodes.get(0).edges.get(0).isContentVisible();
                    nodes.forEach(node -> node.edges.forEach(edge -> edge.setContentVisible(visible)));
                }
            }
            //Start algorithm shortcut
            case P -> {
                if (keyEvent.isShiftDown()) {
                    startDijkstraPath();
                } else if (keyEvent.isAltDown()) {
                    startDijkstraProgress();
                }
            }
            //Copy shortcut
            case C -> {
                if (keyEvent.isControlDown()) {
                    copyFile = new GraphFile(NodeUI.selectedNodes, false);
                }
            }
            //Past shortcut
            case V -> {
                if (keyEvent.isControlDown()) {
                    if (copyFile != null) {
                        //Add to undo backup files
                        undoFiles.add(new GraphFile(nodes, true));

                        createNodesWithEdges(copyFile.createUIList(), false);
                    }
                }
            }
            //Cut shortcut
            case X -> {
                if (keyEvent.isControlDown()) {
                    //Add to undo backup files
                    undoFiles.add(new GraphFile(nodes, true));

                    copyFile = new GraphFile(NodeUI.selectedNodes, true);

                    if (NodeUI.selectedNodes.size() > 0) {
                        List<NodeUI> nodesToRemove = new ArrayList<>(NodeUI.selectedNodes);
                        nodesToRemove.forEach(NodeUI::removeNode);
                    }
                    if (EdgeUI.selectedEdges.size() > 0) {
                        List<EdgeUI> edgesToRemove = new ArrayList<>(EdgeUI.selectedEdges);
                        edgesToRemove.forEach(EdgeUI::removeEdge);
                    }
                }
            }
            //Load undo backup shortcut
            case Z -> {
                if (keyEvent.isControlDown()) {
                    undo();
                }
            }
            //Load redo backup shortcut
            case Y -> {
                if (keyEvent.isControlDown()) {
                    redo();
                }
            }
        }
        keyEvent.consume();
    }
    //endregion

    //region Menu methods
    private void renameSelected(javafx.scene.Node nodeToRename) {
        //Add to undo backup files
        undoFiles.add(new GraphFile(nodes, true));

        NodeUI.selectedNodes.forEach(node -> {
            node.getStyleClass().add("rename");
            node.removeText = true;
            if (nodeToRename != null)
                nodeToRename.setOnKeyPressed(keyEvent -> node.setNodeText(keyEvent, true));
            else
                node.setOnKeyPressed(keyEvent -> node.setNodeText(keyEvent, true));
        });
        EdgeUI.selectedEdges.forEach(edge -> {
            edge.contentBtn.getStyleClass().add("rename");
            edge.removeText = true;
            if (nodeToRename != null)
                nodeToRename.setOnKeyPressed(keyEvent -> edge.setEdgeWeight(keyEvent, true));
            else
                edge.contentBtn.setOnKeyPressed(keyEvent -> edge.setEdgeWeight(keyEvent, true));
        });
    }

    private void setStartNode() {
        //Add to undo backup files
        undoFiles.add(new GraphFile(nodes, true));

        if (NodeUI.selectedNodes.size() > 0) {
            if (NodeUI.startNode != null) {
                NodeUI.startNode.getStyleClass().remove("startNode");

                if (NodeUI.selectedNodes.get(0).getStyleClass().contains("endNode"))
                    NodeUI.startNode.getStyleClass().remove("endNode");
            }

            if (!NodeUI.selectedNodes.get(0).getStyleClass().contains("startNode"))
                NodeUI.selectedNodes.get(0).getStyleClass().add("startNode");
            NodeUI.startNode = NodeUI.selectedNodes.get(0);
            NodeUI.selectedNodes.get(0).NODE.setStartNode(true);
        }
    }

    private void setEndNode() {
        //Add to undo backup files
        undoFiles.add(new GraphFile(nodes, true));

        if (NodeUI.selectedNodes.size() > 0) {
            if (NodeUI.endNode != null) {
                NodeUI.endNode.getStyleClass().remove("endNode");

                if (NodeUI.selectedNodes.get(0).getStyleClass().contains("startNode"))
                    NodeUI.startNode.getStyleClass().remove("startNode");
            }

            if (!NodeUI.selectedNodes.get(0).getStyleClass().contains("endNode"))
                NodeUI.selectedNodes.get(0).getStyleClass().add("endNode");
            NodeUI.endNode = NodeUI.selectedNodes.get(0);
            NodeUI.selectedNodes.get(0).NODE.setEndNode(true);
        }
    }

    private void undo() {
        if (undoFiles.size() > 0) {
            //Create redo backup file
            redoFiles.add(new GraphFile(nodes, true));

            deleteAll(false);
            createNodesWithEdges(undoFiles.get(undoFiles.size()-1).createUIList(), true);
            undoFiles.remove(undoFiles.size()-1);
        }
    }

    private void redo() {
        if (redoFiles.size() > 0) {
            //Create backup file
            undoFiles.add(new GraphFile(nodes, true));

            deleteAll(false);
            createNodesWithEdges(redoFiles.get(redoFiles.size()-1).createUIList(), true);
            redoFiles.remove(redoFiles.size()-1);
        }
    }

    public void startDijkstraPath() {
        //Start animation only if none exists yet
        if (!Algorithm.animationIsPlaying) {
            //Remove the previous algorithm states
            nodes.forEach(node -> {
                node.removeState("checked");
                node.removeState("inProgress");
                node.removeState("path");

                node.edges.forEach(edge -> {
                    edge.removeState(edge.edge, "path");
                    edge.removeState(edge.contentBtn, "path");
                    edge.removeState(edge.arrowA, "path");
                    edge.removeState(edge.arrowB, "path");
                });
            });

            //Start showing dijkstra path animation
            if (NodeUI.startNode != null && NodeUI.endNode != null)
                graph.setDijkstraAlgorithmUp(NodeUI.startNode.NODE, NodeUI.endNode.NODE).showPath(200, false);
        }
    }

    public void startDijkstraProgress () {
        //Start animation only if none exists yet
        if (!Algorithm.animationIsPlaying) {
            //Remove the previous algorithm states
            nodes.forEach(node -> {
                node.removeState("checked");
                node.removeState("inProgress");
                node.removeState("path");

                node.edges.forEach(edge -> {
                    edge.removeState(edge.edge, "path");
                    edge.removeState(edge.contentBtn, "path");
                    edge.removeState(edge.arrowA, "path");
                    edge.removeState(edge.arrowB, "path");
                });
            });

            //Start showing dijkstra progress animation
            if (NodeUI.startNode != null && NodeUI.endNode != null)
                graph.setDijkstraAlgorithmUp(NodeUI.startNode.NODE, NodeUI.endNode.NODE).showProgress(350);
        }
    }
    //endregion

    //region Load and save

    /**
     * Do save a file with a FileChooser
     */
    private void saveFile() {
        //Create the file chooser and set the title as well as the extension filter
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save graph");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GRAPH", "*.graph"));

        //Show tge file chooser and save the given path in a variable
        File saveGraphFile = fileChooser.showSaveDialog(Main.mainStage);

        //Create and save the graph file to the given path
        saveGraphFile(saveGraphFile, new GraphFile(nodes, false));
    }

    /**
     * Do load a file with a FileChooser
     */
    private void loadFile() {
        //Add to undo backup files
        undoFiles.add(new GraphFile(nodes, true));

        //Create the file chooser and set the title as well as the extension filter
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open saved graph");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GRAPH", "*.graph")
        );

        //Show tge file chooser and save the given path in a variable
        File graphFileToOpen = fileChooser.showOpenDialog(Main.mainStage);

        //Load the graph file from the given path and create it
        GraphFile graphFile = loadGraphFile(graphFileToOpen);
        if (graphFile != null)
            createNodesWithEdges(graphFile.createUIList(), false);
    }

    /**
     * Do save the given graph file to the given file directory
     */
    private void saveGraphFile(File fileDir, GraphFile graphFile) {
        //Serialise the graph file if the given path is not null
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
        //Create graph file variable to return
        GraphFile graphFile = null;

        //Deserialize the graph file if the given path is not null
        if (file != null) {
            try{
                FileInputStream fileIn = new FileInputStream(file.getAbsolutePath());
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);

                Object obj = objectIn.readObject();

                //Save the file to the created variable
                graphFile = (GraphFile) obj;

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
            //Add to undo backup files
            undoFiles.add(new GraphFile(nodes, true));

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
            node2.moveNode(event);
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

        node1 = null;
        node2 = null;
    }

    //endregion

    //region Create methods

    public static void createNodesWithEdges(List<NodeUI> nodesToCreate, boolean useExtraInformation) {
        //Deselect all nodes and edges
        NodeUI.deselectSelectedNodes();
        EdgeUI.deselectSelectedEdges();

        //Create nodes
        for (NodeUI node : nodesToCreate) {

            //Add the node
            createNode(node);

            //Select the created node
            if (useExtraInformation) {
                if (node.NODE.isSelected()) {
                    node.selectNode();
                }
                if (node.NODE.isStartNode() && NodeUI.startNode == null) {
                    if (!node.getStyleClass().contains("startNode"))
                        node.getStyleClass().add("startNode");
                    NodeUI.startNode = node;
                }
                if (node.NODE.isEndNode() && NodeUI.endNode == null) {
                    if (!node.getStyleClass().contains("endNode"))
                        node.getStyleClass().add("endNode");
                    NodeUI.endNode = node;
                }
            }
            else {
                node.selectNode();
            }

            //Create edges
            for (EdgeUI edge : node.edges) {
                //Add edge to the pane
                if (!paneReference.getChildren().contains(edge)) {
                    paneReference.getChildren().add(edge);
                }

                if (useExtraInformation) {
                    if (edge.EDGE.isSelected())
                        edge.selectEdge();
                }
                else
                    edge.selectEdge();
            }
        }
    }

    public static NodeUI createNode(double x, double y) {
        //Create the node object
        NodeUI node = new NodeUI(x, y);

        //Add node to graph data
        node.NODE = graph.addNode(node.getText());

        //Add node to the global list
        nodes.add(node);

        //Add node to screen
        paneReference.getChildren().add(node);

        return node;
    }

    public static void createNode(NodeUI nodeToAdd) {
        //Add node to graph data
        graph.addNode(nodeToAdd.NODE);

        //Add node to the global list
        nodes.add(nodeToAdd);

        //Add node to screen
        paneReference.getChildren().add(nodeToAdd);
    }

    public static EdgeUI createEdge(NodeUI node1, NodeUI node2) {
        //Create the edge object
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

    //endregion

    //region Global methods

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
    public static void deleteAll(boolean createBackup) {
        Algorithm.animationIsPlaying = false;

        //Add to undo backup files
        if (createBackup)
            undoFiles.add(new GraphFile(nodes, true));

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

        NodeUI.startNode = null;
        NodeUI.endNode = null;

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

    //endregion
    //endregion
}
