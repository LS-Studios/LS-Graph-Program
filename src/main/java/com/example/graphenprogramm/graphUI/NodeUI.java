package com.example.graphenprogramm.graphUI;

import com.example.graphenprogramm.Controller;
import com.example.graphenprogramm.Main;
import com.example.graphenprogramm.Old.NodeSettingsPopUpController;
import com.example.graphenprogramm.graphLogic.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import static com.example.graphenprogramm.Controller.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class NodeUI extends Button {
    public Node NODE;

    public static int count = 0;
    private int ID;

    public ObservableList<EdgeUI> edges = FXCollections.observableArrayList();

    private NodeUI nodeToDelete;

    private boolean removeText = true;

    public static ArrayList<NodeUI> selectedNodes = new ArrayList<>();
    public double startDragX = 0;
    public double startDragY = 0;

    public NodeUI(double x, double y) {
        setLayoutX(x);
        setLayoutY(y);

        translateXProperty().bind(widthProperty().divide(-2));
        translateYProperty().bind(heightProperty().divide(-2));

        //region Set ID
        ID = count++;

        boolean needNewID = false;

        for (NodeUI node : Controller.nodes) {
            if (!isNumeric(node.getText()))
                needNewID = true;
            else if (ID == Integer.parseInt(node.getText())) {
                needNewID = true;
            }
        }

        if (needNewID) {
            int id = 0;

            while (true) {
                boolean isContained = true;
                for (NodeUI node : Controller.nodes) {
                    if (isNumeric(node.getText()) && id == Integer.parseInt(node.getText())) {
                        isContained = true;
                        break;
                    }
                    else
                        isContained = false;
                }

                if (isContained)
                    id++;
                else
                    break;
            }

            ID = id;
        }

        setText("" + ID);
        getStyleClass().add("nodeStyle");
        //endregion

        //Set events
        setOnMousePressed(mouseEvent -> onNodePressed(mouseEvent));
        setOnDragDetected(mouseEvent -> onNodeDragDetected(mouseEvent));
        setOnMouseDragged(mouseEvent -> onNodeDragged(mouseEvent));
        setOnMouseReleased(mouseEvent -> onNodeReleased(mouseEvent));
        setOnMouseDragEntered(mouseDragEvent -> onNodeDragEntered(mouseDragEvent));
        setOnMouseDragExited(mouseDragEvent -> onNodeDragExited(mouseDragEvent));
        setOnMouseMoved(mouseEvent -> {
            if (contextMenu != null)
                contextMenu.hide();
        });

        startDragX = getLayoutX();
        startDragY = getLayoutY();
    }

    /**
     * Checks if the given String is a number
     */
    private boolean isNumeric(String text) {
        try {
            double d = Double.parseDouble(text);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    //region Node events
    private void onNodePressed(MouseEvent mouseEvent) {
        if (mouseEvent.isPrimaryButtonDown()) {
            if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.isStillSincePress() && selectedNodes.contains(this)) {
                //Create context menu
                MenuItem item1 = new MenuItem("Set node as start point");
                MenuItem item2 = new MenuItem("Set node as end point");

                item1.setOnAction(actionEvent -> System.out.println());

                createContextMenu(Arrays.asList(item1, item2), mouseEvent.getScreenX() - 10, mouseEvent.getScreenY() - 10);
            }

            selectNode(this);
        }
        if (mouseEvent.isSecondaryButtonDown())
            nodeToDelete = this;

        //Set drag positions
        startDragX = getLayoutX();
        startDragY = getLayoutY();

        if (selectedNodes.contains(this)) {
            selectedNodes.forEach(node -> {
                if (node != this) {
                    node.startDragX = node.getLayoutX();
                    node.startDragY = node.getLayoutY();
                }
            });
        }
    }

    private void onNodeReleased(MouseEvent mouseEvent) {
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

            createEdge(dragOverNode, this);
            dragOverNode = null;
        }

        node2 = null;
        nodeToDelete = null;
    }

    private void onNodeDragDetected(MouseEvent e) {
        if (e.isPrimaryButtonDown()) {
            node1 = this;
            node2 = createNode(getLayoutX() + e.getX() + getTranslateX(),
                               getLayoutY() + e.getY() + getTranslateY());
            node2.toBack();
            edgeUI = createEdge(this, node2);
        } else if (e.isSecondaryButtonDown()) {
            nodeToDelete = null;
        }

        startFullDrag();
    }

    private void onNodeDragEntered(MouseDragEvent dragEvent) {
        if (node2 != null && node2 != this) {
            node2.setVisible(false);

            boolean canConnect = true;

            for (EdgeUI edge : edges) {
                if (edge.getNode1().equals(node1)
                        || edge.getNode2().equals(node1)) {
                    canConnect = false;
                }
            }

            if (canConnect) {
                getStyleClass().add("draggedOver");
                dragOverNode = this;
            } else {
                dragOverNode = null;
                getStyleClass().add("draggedOverError");
            }

        }
    }

    private void onNodeDragExited(MouseDragEvent dragEvent) {
        if (node2 != null)
            node2.setVisible(true);

        dragOverNode = null;

        getStyleClass().remove("draggedOver");
        getStyleClass().remove("draggedOverError");
    }

    private void onNodeDragged(MouseEvent e) {
        nodeToDelete = null;

        if (node2 != null) {
            //tempNode2.toFront();
            node2.setLayoutX(getLayoutX() + e.getX() + getTranslateX());
            node2.setLayoutY(getLayoutY() + e.getY() + getTranslateY());
        }
        if (e.isSecondaryButtonDown()) {
            setLayoutX(startDragX + (e.getSceneX() - startDragX));
            setLayoutY(startDragY + (e.getSceneY() - startDragY));

            if (selectedNodes.contains(this)) {
                selectedNodes.forEach(node -> {
                    if (node != this) {
                        node.setLayoutX(node.startDragX + (e.getSceneX() - startDragX));
                        node.setLayoutY(node.startDragY + (e.getSceneY() - startDragY));
                    }
                });
            }
        }
    }
    //endregion

    //region Node actions

    /**
     * Do select the node
     */
    public void selectNode(NodeUI nodeToSelect) {
        removeText = true;

        System.out.println(shiftPressed);
        if (!selectedNodes.isEmpty() && !shiftPressed) {
            deselectSelectedNodes();
        }

        nodeToSelect.setOnKeyPressed(keyEvent -> setNodeText(keyEvent));
        nodeToSelect.getStyleClass().add("selected");
        selectedNodes.add(nodeToSelect);
    }

    /**
     * Deselecting the current selected node
     */
    public static void deselectSelectedNodes() {
        if (!selectedNodes.isEmpty()) {
            selectedNodes.forEach(node -> {
                node.setOnKeyPressed(null);
                node.getStyleClass().remove("selected");
            });
            selectedNodes.clear();
        }
    }

    /**
     * Sets the text of the node
     */
    private void setNodeText(KeyEvent keyEvent) {
        String text = getText();

        if (removeText && keyEvent.getCode() != KeyCode.SHIFT) {
            setText("");
            removeText = false;
            setNodeText(keyEvent);
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

        NODE.setName(text);
        setText(text);

        for (EdgeUI edge : edges) {
            edge.update();
        }
    }

    /**
     * Delete the last char of the node name
     */
    private String deleteChar(String text) {
        String newText = text;

        if (text.length() > 0) {
            newText = newText.substring(0, text.length()-1);
        }

        return newText;
    }

    /**
     * Removes this node from the graph and screen
     */
    public void removeNode() {
        paneReference.getChildren().remove(this);

        for (EdgeUI edge : edges) {
            paneReference.getChildren().remove(edge);
            graph.removeEdge(edge.EDGE);
        }

        graph.removeNode(NODE);
        edges.clear();
        nodes.remove(this);

        NodeUI.count--;
    }

    /**
     * Removes the given node from the graph and screen
     */
    public void removeNode(NodeUI nodeToRemove) {
        paneReference.getChildren().remove(nodeToRemove);

        for (EdgeUI edge : nodeToRemove.edges) {
            paneReference.getChildren().remove(edge);
            graph.removeEdge(edge.EDGE);
        }

        graph.removeNode(nodeToRemove.NODE);
        nodeToRemove.edges.clear();
        nodes.remove(nodeToRemove);

        NodeUI.count--;
    }
    //endregion
}
