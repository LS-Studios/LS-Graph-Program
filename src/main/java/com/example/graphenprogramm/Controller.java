package com.example.graphenprogramm;

import com.example.graphenprogramm.graphLogic.Algorithm.Algorithm;
import com.example.graphenprogramm.graphLogic.Graph;
import com.example.graphenprogramm.graphLogic.GraphFile;
import com.example.graphenprogramm.graphLogic.Node;
import com.example.graphenprogramm.graphUI.EdgeUI;
import com.example.graphenprogramm.graphUI.NodeUI;
import com.example.graphenprogramm.graphUI.Position;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.util.*;

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

    //endregion

    //region Menu button references

    @FXML
    public Button renameSelectedBtn;
    public static Button renameBtnReference;
    @FXML
    public Button deleteSelectedBtn;
    @FXML
    public Button duplicateSelectedBtn;
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

    public static boolean shiftPressed = false;
    public static boolean controlPressed = false;
    public static boolean altPressed = false;

    public static ContextMenu contextMenu;

    public static javafx.scene.Node renameKeyListenerNode;

    private GraphFile duplicationFile;
    private GraphFile copyFile;
    public static List<GraphFile> backupFiles = new ArrayList<>();

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
        pane.setOnKeyPressed(keyEvent -> {
            globalOnKeyPressed(keyEvent);
        });
        pane.setOnKeyReleased(keyEvent -> globalOnKeyReleased(keyEvent));
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
        //endregion

        //region Menu pane input
        menuPane.setOnMousePressed(mouseEvent -> nodes.forEach(node -> {
            node.removeState("rename");
            node.edges.forEach(edge -> edge.removeState(edge.contentBtn, "rename"));
        }));
        menuPane.setOnKeyPressed(this::globalOnKeyPressed);
        menuPane.setOnKeyReleased(this::globalOnKeyReleased);
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
        //endregion

        //endregion

        //region Menu button actions

        //region Edit menu
        renameSelectedBtn.setOnAction(actionEvent -> {
            //Add to back up
            backupFiles.add(new GraphFile(nodes));

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
            //Add to back up
            backupFiles.add(new GraphFile(nodes));

            while (NodeUI.selectedNodes.size() > 0) {
                NodeUI.selectedNodes.get(NodeUI.selectedNodes.size()-1).removeNode();
            }
            while (EdgeUI.selectedEdges.size() > 0) {
                EdgeUI.selectedEdges.get(EdgeUI.selectedEdges.size()-1).removeEdge();
            }
        });
        duplicateSelectedBtn.setOnAction(actionEvent -> {
            //Add to back up
            backupFiles.add(new GraphFile(nodes));

            //Stop renaming
            nodes.forEach(node -> {
                node.removeState("rename");
                node.edges.forEach(edge -> edge.removeState(edge.contentBtn, "rename"));
            });

            duplicationFile = new GraphFile(NodeUI.selectedNodes);
            createNodesWithEdges(duplicationFile.createUIList());
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
                //Add to back up
                backupFiles.add(new GraphFile(nodes));

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
                if (controlPressed) {
                    selectAll();
                }
            }
            //Duplicate shortcut
            case D -> {
                if (shiftPressed) {
                    backupFiles.add(new GraphFile(nodes));

                    duplicationFile = new GraphFile(NodeUI.selectedNodes);
                    createNodesWithEdges(duplicationFile.createUIList());
                }
            }
            //Set end node shortcut
            case E -> {
                if (shiftPressed) {
                    backupFiles.add(new GraphFile(nodes));
                    setEndNode();
                }
            }
            //Save + start node shortcut
            case S -> {
                if (controlPressed) {
                    saveFile();
                } else if (shiftPressed) {
                    setStartNode();
                }
            }
            //Open shortcut
            case O -> {
                if (controlPressed) {
                    loadFile();
                }
            }
            //Rename shortcut
            case R -> {
                if (shiftPressed) {
                    if (NodeUI.selectedNodes.size() > 0) {
                        renameKeyListenerNode = NodeUI.selectedNodes.get(0);
                        renameSelected(null);
                    }
                }
            }
            //Toggle weights shortcut
            case T -> {
                if (shiftPressed) {
                    boolean visible = !nodes.get(0).edges.get(0).isContentVisible();
                    nodes.forEach(node -> node.edges.forEach(edge -> edge.setContentVisible(visible)));
                }
            }
            //Start algorithm shortcut
            case P -> {
                if (shiftPressed) {
                    startDijkstraPath();
                } else if (altPressed) {
                    startDijkstraProgress();
                }
            }
            //Copy shortcut
            case C -> {
                if (controlPressed) {
                    copyFile = new GraphFile(NodeUI.selectedNodes);
                }
            }
            //Past shortcut
            case V -> {
                if (controlPressed) {
                    if (copyFile != null) {
                        //Add to back up
                        backupFiles.add(new GraphFile(nodes));

                        createNodesWithEdges(copyFile.createUIList());
                    }
                }
            }
            case Z -> {
                if (controlPressed) {
                    if (backupFiles.size() > 0) {
                        deleteAll(false);
                        createNodesWithEdges(backupFiles.get(backupFiles.size()-1).createUIList());
                        backupFiles.remove(backupFiles.size()-1);
                    }
                }
            }
            case SHIFT -> shiftPressed = true;
            case CONTROL -> controlPressed = true;
            case ALT -> altPressed = true;
        }
    }

    private void globalOnKeyReleased(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case SHIFT -> shiftPressed = false;
            case CONTROL -> controlPressed = false;
            case ALT -> altPressed = false;
        }
    }
    //endregion

    //region Menu methods
    private void renameSelected(javafx.scene.Node nodeToRename) {
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
        if (NodeUI.selectedNodes.size() > 0) {
            if (NodeUI.startNode != null) {
                NodeUI.startNode.getStyleClass().remove("startNode");

                if (NodeUI.selectedNodes.get(0).getStyleClass().contains("endNode"))
                    NodeUI.startNode.getStyleClass().remove("endNode");
            }

            if (!NodeUI.selectedNodes.get(0).getStyleClass().contains("startNode"))
                NodeUI.selectedNodes.get(0).getStyleClass().add("startNode");
            NodeUI.startNode = NodeUI.selectedNodes.get(0);
        }
    }

    private void setEndNode() {
        if (NodeUI.selectedNodes.size() > 0) {
            if (NodeUI.endNode != null) {
                NodeUI.endNode.getStyleClass().remove("endNode");

                if (NodeUI.selectedNodes.get(0).getStyleClass().contains("startNode"))
                    NodeUI.startNode.getStyleClass().remove("startNode");
            }

            if (!NodeUI.selectedNodes.get(0).getStyleClass().contains("endNode"))
                NodeUI.selectedNodes.get(0).getStyleClass().add("endNode");
            NodeUI.endNode = NodeUI.selectedNodes.get(0);
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
        saveGraphFile(saveGraphFile, new GraphFile(nodes));
    }

    /**
     * Do load a file with a FileChooser
     */
    private void loadFile() {
        //Add to back up
        backupFiles.add(new GraphFile(nodes));

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
            createNodesWithEdges(graphFile.createUIList());
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

    //region Algorithm actions

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

    //region Graph events

    public void onGraphPressed(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            //Add to back up
            backupFiles.add(new GraphFile(nodes));

            node1 = createNode(event.getX(), event.getY());

            nodes.forEach(node -> {
                node.removeAllStates("button", "nodeStyle", "startNode", "endNode");

                node.edges.forEach(edge -> {
                    edge.removeAllStates(edge.edge,"null");
                    edge.removeAllStates(edge.contentBtn,"weightStyle");
                });
            });
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

        //Create options context menu
        if (event.getButton() == MouseButton.SECONDARY && event.isStillSincePress()) {
            //region Algorithm menu

            Menu algorithmMenu = new Menu("Algorithm");
            MenuItem subAlgorithm1 = new MenuItem("Dijkstra path");
            MenuItem subAlgorithm2 = new MenuItem("Dijkstra progress");

            //Set dijkstra calculation up and calculate the path
            subAlgorithm1.setOnAction(actionEvent -> startDijkstraPath());
            subAlgorithm2.setOnAction(actionEvent -> startDijkstraProgress());

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
                duplicationFile = new GraphFile(NodeUI.selectedNodes);
                createNodesWithEdges(duplicationFile.createUIList());
            });

            //Select all nodes in the graph
            item3.setOnAction(actionEvent -> selectAll());

            //Delete all nodes in the graph
            item4.setOnAction(actionEvent -> deleteAll(true));

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
            subFile1.setOnAction(actionEvent -> saveFile());

            subFile2.setOnAction(actionEvent -> loadFile());

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
        //Deselect all nodes and edges
        NodeUI.deselectSelectedNodes();
        EdgeUI.deselectSelectedEdges();

        //Create nodes
        for (NodeUI node : nodesToCreate) {

            //Add the node
            createNode(node);

            //Select the created node
            if (!node.getStyleClass().contains("selected"))
                node.getStyleClass().add("selected");

            //Add the new node to the selected nodes
            if (!NodeUI.selectedNodes.contains(node))
                NodeUI.selectedNodes.add(node);

            //Create edges
            for (EdgeUI edge : node.edges) {
                //Add edge to the pane
                if (!paneReference.getChildren().contains(edge)) {
                    paneReference.getChildren().add(edge);
                }

                //Select the edge
                if (!edge.contentBtn.getStyleClass().contains("selected"))
                    edge.contentBtn.getStyleClass().add("selected");

                //Add the new edge to the selected edges
                if (!EdgeUI.selectedEdges.contains(edge))
                    EdgeUI.selectedEdges.add(edge);
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
        //Add to back up
        if (createBackup)
            backupFiles.add(new GraphFile(nodes));

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

    //endregion
    //endregion
}
