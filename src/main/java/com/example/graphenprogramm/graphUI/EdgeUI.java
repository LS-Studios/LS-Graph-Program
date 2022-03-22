package com.example.graphenprogramm.graphUI;

import com.example.graphenprogramm.Controller;
import com.example.graphenprogramm.graphLogic.Edge;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.graphenprogramm.Controller.*;

public class EdgeUI extends Group {
    public Edge EDGE;

    public Polyline edge = new Polyline();
    public Polyline arrowA = new Polyline();
    public Polyline arrowB = new Polyline();

    private SimpleDoubleProperty x1 = new SimpleDoubleProperty();
    private SimpleDoubleProperty y1 = new SimpleDoubleProperty();
    private SimpleDoubleProperty x2 = new SimpleDoubleProperty();
    private SimpleDoubleProperty y2 = new SimpleDoubleProperty();

    private SimpleBooleanProperty arrowAVisible = new SimpleBooleanProperty(true);
    private SimpleBooleanProperty arrowBVisible = new SimpleBooleanProperty(true);

    public static SimpleBooleanProperty contentVisible = new SimpleBooleanProperty(true);

    private double EDGE_SCALER = 20;
    private final double ARROW_ANGLE = Math.toRadians(20);
    private final double ARROW_LENGTH = 10;

    private NodeUI node1, node2;

    public Button contentBtn;

    private final Pane contentPane = new Pane();
    public static ArrayList<EdgeUI> selectedEdges = new ArrayList<>();
    private boolean removeText = true;

    public EdgeUI(NodeUI node1, NodeUI node2) {
        //Set the properties to the given values
        x1.set(node1.getLayoutX());
        y1.set(node1.getLayoutY());
        x2.set(node2.getLayoutX());
        y2.set(node2.getLayoutY());

        //Bind the properties to the nodes
        x1.bind(node1.layoutXProperty());
        y1.bind(node1.layoutYProperty());
        x2.bind(node2.layoutXProperty());
        y2.bind(node2.layoutYProperty());

        this.node1 = node1;
        this.node2 = node2;

        //region Set edge up

        getChildren().addAll(edge, arrowA, arrowB);

        //Add lister to the property to update the position of the edges
        for (SimpleDoubleProperty s : new SimpleDoubleProperty[] {this.x1, this.y1, this.x2, this.y2}) {
            s.addListener( (l, o, n) -> update());
        }

        //Bind the visible property of the arrows
        arrowA.visibleProperty().bind(arrowAVisible);
        arrowB.visibleProperty().bind(arrowBVisible);

        //endregion

        update();

        //region Set content up

        //Create the content
        contentBtn = new Button("1");
        contentBtn.getStyleClass().add("weightStyle");

        //Bind the content visible property
        contentBtn.visibleProperty().bind(contentVisible);

        //Set the select events for the content
        contentBtn.setOnMousePressed(mouseEvent -> onContentPressed(mouseEvent));

        contentBtn.setOnMouseReleased(mouseEvent -> onContentReleased(mouseEvent));

        contentPane.getChildren().add(contentBtn);

        //Add the content pane to the edge
        getChildren().addAll(contentPane);

        //Bind and set the position of the content
        contentPane.layoutXProperty().bind(x2Property().add(x1Property()).divide(2).subtract(contentPane.widthProperty().divide(2)));
        contentPane.layoutYProperty().bind(y2Property().add(y1Property()).divide(2).subtract(contentPane.heightProperty().divide(2)));

        //endregion
    }

    //region Content events

    private void onContentPressed(MouseEvent mouseEvent) {
        //On left mouse click
        if (mouseEvent.isPrimaryButtonDown()) {
            //Change the arrow direction
            if (selectedEdges.contains(this) && !controlPressed) {
                if (isArrowAVisible() && isArrowBVisible()) {
                    setArrowAVisible(false);
                    setArrowBVisible(true);
                    EDGE.changeConnectionToNode(node1.NODE, false);
                    EDGE.changeConnectionToNode(node2.NODE, true);
                } else if (!isArrowAVisible() && isArrowBVisible()) {
                    setArrowAVisible(true);
                    setArrowBVisible(false);
                    EDGE.changeConnectionToNode(node1.NODE, true);
                    EDGE.changeConnectionToNode(node2.NODE, false);
                } else if (isArrowAVisible() && !isArrowBVisible()) {
                    setArrowAVisible(true);
                    setArrowBVisible(true);
                    EDGE.changeConnectionToNode(node1.NODE, true);
                    EDGE.changeConnectionToNode(node2.NODE, true);
                }
            }

            //Select the edge
            selectEdge(this);
        }
    }

    private void onContentReleased(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.SECONDARY && mouseEvent.isStillSincePress()) {
            //Reset the state of all nodes and edges
            nodes.forEach(node -> {
                node.setOnKeyPressed(null);
                node.getStyleClass().remove("rename");

                node.edges.forEach(edge -> {
                    edge.contentBtn.setOnKeyPressed(null);
                    edge.contentBtn.getStyleClass().remove("rename");
                });
            });

            //Create context menu

            //region Rename menu

            Menu renameMenu = new Menu("Rename");
            MenuItem subRename1 = new MenuItem("Rename current");
            MenuItem subRename2 = new MenuItem("Rename all selected nodes");

            //Rename current node
            subRename1.setOnAction(actionEvent -> {
                contentBtn.getStyleClass().add("rename");
                removeText = true;
                contentBtn.setOnKeyPressed(keyEvent -> setEdgeWeight(keyEvent, false));
            });

            //Rename all selected nodes
            subRename2.setOnAction(actionEvent -> {
                selectedEdges.forEach(edge -> {
                    edge.contentBtn.getStyleClass().add("rename");
                    removeText = true;
                    edge.contentBtn.setOnKeyPressed(keyEvent -> setEdgeWeight(keyEvent, true));
                });
            });

            renameMenu.getItems().addAll(subRename1, subRename2);

            //endregion

            //region Delete menu
            Menu deleteMenu = new Menu("Delete");
            MenuItem subDelete1 = new MenuItem("Delete current");
            MenuItem subDelete2 = new MenuItem("Delete all selected");

            //Delete current node
            subDelete1.setOnAction(actionEvent -> removeEdge());

            //Delete all selected nodes and edges
            subDelete2.setOnAction(actionEvent -> {
                selectedEdges.forEach(edge -> {
                    edge.removeEdge();
                });
                selectedEdges.clear();
                NodeUI.selectedNodes.forEach(node -> {
                    node.removeNode();
                });
                NodeUI.selectedNodes.clear();
            });

            deleteMenu.getItems().addAll(subDelete1, subDelete2);
            //endregion

            //Multiple items are selected
            if (selectedEdges.size() > 1 || NodeUI.selectedNodes.size() > 0 && selectedEdges.contains(this)) {
                //Create menu for multiple items to rename and delete
                if (selectedEdges.size() > 1)
                    createContextMenu(Arrays.asList(renameMenu, deleteMenu), mouseEvent.getScreenX() - 10, mouseEvent.getScreenY() - 10);

                    //Create menu for multiple items to delete but just rename the current one
                else
                    createContextMenu(Arrays.asList(subRename1, deleteMenu), mouseEvent.getScreenX() - 10, mouseEvent.getScreenY() - 10);
            }

            //Just one or not enough items are selected
            else {
                //Create item to rename and delete just the current item
                createContextMenu(Arrays.asList(subRename1, subDelete1), mouseEvent.getScreenX() - 10, mouseEvent.getScreenY() - 10);
            }
        }


        setStyleUp();
    }

    //endregion

    //region Set edge position and style

    /**
     * Updates the current position of the EdgeUI
     */
    public void update() {
        //Value between 0 and 1 depending on whether the mouse is placed
        var value = Math.abs(Math.tanh((y2.get() - y1.get())/(x2.get()-x1.get())));

        //Lerp the position of the Edge by the calculated value
        double[] start = scale(x1.get(), y1.get(), x2.get(), y2.get(), lerp((node1.getWidth()/2) + EDGE_SCALER, node1.getHeight(), value));
        double[] end = scale(x2.get(), y2.get(), x1.get(), y1.get(), lerp((node2.getWidth()/2) + EDGE_SCALER, node2.getHeight(), value));

        double x1 = start[0];
        double y1 = start[1];
        double x2 = end[0];
        double y2 = end[1];


        //Set the position of the edge points
        edge.getPoints().setAll(x1, y1, x2, y2);

        //Create the arrows
        createArrows(x1, y1, x2, y2);
    }

    /**
     * Sets the style of the edge and arrows up
     */
    private void setStyleUp() {
        edge.getStyleClass().remove("path");
        edge.getStyleClass().setAll("edge");
        arrowA.getStyleClass().setAll("edge");
        arrowB.getStyleClass().setAll("edge");

        arrowA.getStyleClass().remove("path");
        arrowA.getStyleClass().add("arrow");
        arrowB.getStyleClass().remove("path");
        arrowB.getStyleClass().add("arrow");
    }

    /**
     * Do create the arrows at the end of the edge
     */
    private void createArrows(double x1, double y1, double x2, double y2) {
        double theta = Math.atan2(y2 - y1, x2 - x1);

        //Edge side 1
        //Calculate arrow side 1
        double x = x1 + Math.cos(theta + ARROW_ANGLE) * ARROW_LENGTH;
        double y = y1 + Math.sin(theta + ARROW_ANGLE) * ARROW_LENGTH;

        //Set arrow side1 and edge corner
        arrowA.getPoints().setAll(x, y, x1, y1);

        //Calculate arrow side 2
        x = x1 + Math.cos(theta - ARROW_ANGLE) * ARROW_LENGTH;
        y = y1 + Math.sin(theta - ARROW_ANGLE) * ARROW_LENGTH;

        //Add arrow side 2
        arrowA.getPoints().addAll(x, y);

        //Edge side 2
        //Calculate arrow side 1
        x = x2 - Math.cos(theta + ARROW_ANGLE) * ARROW_LENGTH;
        y = y2 - Math.sin(theta + ARROW_ANGLE) * ARROW_LENGTH;

        //Set arrow side1 and edge corner
        arrowB.getPoints().setAll(x, y, x2, y2);

        //Calculate arrow side 2
        x = x2 - Math.cos(theta - ARROW_ANGLE) * ARROW_LENGTH;
        y = y2 - Math.sin(theta - ARROW_ANGLE) * ARROW_LENGTH;

        //Add arrow side 2
        arrowB.getPoints().addAll(x, y);
    }

    /**
     * Scales the edge depending on the scaler value, so it doesn't overlap with the node
     */
    private double[] scale(double x1, double y1, double x2, double y2, double SCALER) {
        double theta = Math.atan2(y2 - y1, x2 - x1);
        return new double[] {
                x1 + Math.cos(theta) * SCALER,
                y1 + Math.sin(theta) * SCALER,
        };
    }

    //endregion

    //region Selecting

    /**
     * Do select the clicked content
     */
    private void selectEdge(EdgeUI edgeToSelect) {
        if (!controlPressed) {
            //Deselect all the other edges and nodes if shift is not pressed
            if (!Controller.shiftPressed) {
                deselectSelectedEdges();
                NodeUI.deselectSelectedNodes();
            }

            //Select the given edge
            if (!edgeToSelect.contentBtn.getStyleClass().contains("selected"))
                edgeToSelect.contentBtn.getStyleClass().add("selected");

            selectedEdges.add(edgeToSelect);
        }

        //Deselect if control is pressed
        else {
            deselectEdge(edgeToSelect);
        }
    }

    /**
     * Deselect all selected edges
     */
    public static void deselectSelectedEdges() {
        //Reset all nodes to their base state
        nodes.forEach(node -> {
            node.edges.forEach(edge -> {
                edge.contentBtn.setOnKeyPressed(null);
                edge.contentBtn.getStyleClass().remove("rename");
                edge.contentBtn.getStyleClass().remove("path");
                edge.setStyleUp();
            });
        });

        //Remove the select style
        selectedEdges.forEach(content -> {
            content.contentBtn.getStyleClass().removeAll("selected");
        });

        //Clear the list of selected edges
        selectedEdges.clear();
    }

    /**
     * Deselect the given selected Edge
     */
    public static void deselectEdge(EdgeUI edgeToDeselect) {
        //Reset all edges to their base state
        nodes.forEach(node -> {
            node.edges.forEach(edge -> {
                edge.contentBtn.setOnKeyPressed(null);
                edge.contentBtn.getStyleClass().remove("rename");
                edge.contentBtn.getStyleClass().remove("path");
                edge.setStyleUp();
            });
        });

        //Remove the select sty
        edgeToDeselect.contentBtn.getStyleClass().remove("select");

        //Remove the given edge from the list
        selectedEdges.remove(edgeToDeselect);
    }

    //endregion

    /**
     * Updates the weight/length of the edge
     */
    public void setEdgeWeight(KeyEvent keyEvent, boolean setAll) {
        String text = contentBtn.getText();

        //Remove the text if the user start renaming
        if (removeText) {
            contentBtn.setText("");
            removeText = false;
            setEdgeWeight(keyEvent, setAll);
            return;
        }

        //Do different action depending on the key that's pressed
        switch (keyEvent.getCode()) {
            case BACK_SPACE -> text = deleteChar(text);
            case ENTER -> {
                //Complete renaming
                selectedEdges.forEach(edge -> {
                    edge.contentBtn.setOnKeyPressed(null);
                    edge.contentBtn.getStyleClass().remove("rename");
                });
            }
            default -> {
                //Only add char if it's a number or an / for fractures
                if (keyEvent.getCode().getChar().equals("7") && keyEvent.isShiftDown()) {
                    text += "/";
                } else if (keyEvent.getCode().isDigitKey() || keyEvent.getCode().getChar().equals(".") || keyEvent.getCode().getChar().equals(",")) {
                    text += keyEvent.getCode().getChar();
                }
            }
        }

        //Rename all selected edges the same, if true
        final String contentText = text;
        if (setAll) {
            selectedEdges.forEach(edge -> {
                if (contentText.length() > 0) {
                    if (contentText.contains("/")) {
                        edge.EDGE.setLength(calculateFracture(contentText));
                    } else
                        edge.EDGE.setLength(Double.parseDouble(contentText));
                }

                edge.contentBtn.setText(contentText);
            });
        }
        else {
            //Set the edge length to the calculated number
            if (text.length() > 0) {
                if (text.contains("/")) {
                    EDGE.setLength(calculateFracture(text));
                } else
                    EDGE.setLength(Double.parseDouble(text));
            }

            contentBtn.setText(text);
        }
    }


    //region Helper function

    /**
     * Calculates the fracture of the weight/length if needed
     */
    private double calculateFracture(String operation) {
        double calculated = 0;

        ArrayList<String> numbers = new ArrayList<>();
        numbers.add("");

        //Gets all sub numbers behind and before an / and add it ti numbers
        for (int i = 0; i < operation.length(); i++) {
            if (operation.substring(i, i+1).equals("/")) {
                numbers.add("");
            } else {
                numbers.set(numbers.size()-1, numbers.get(numbers.size()-1) + operation.substring(i, i+1));
            }
        }

        //Calculate the fracture if numbers is not empty
        if (!numbers.isEmpty()) {
            calculated = Double.parseDouble(numbers.get(0));

            for (int i = 1; i < numbers.size(); i++) {
                if (!numbers.get(i).isEmpty())
                   calculated /= Double.parseDouble(numbers.get(i));
            }
        }

        return calculated;
    }

    /**
     * Delete the last char of the edge name
     */
    private String deleteChar(String text) {
        String newText = text;

        if (text.length() > 0) {
            newText = newText.substring(0, text.length()-1);
        }

        return newText;
    }

    /**
     * Returns the value between a and b depending on f
     */
    double lerp(double a, double b, double f) {
        return a + f * (b - a);
    }

    //endregion

    /**
     * Removes the edge from the graph and screen
     */
    public void removeEdge() {
        Controller.paneReference.getChildren().remove(this);
        getNode1().edges.remove(this);
        getNode2().edges.remove(this);
        Controller.graph.removeEdge(EDGE);
    }

    //region Getters and setters
    public double getX1() {
        return x1.get();
    }
    public SimpleDoubleProperty x1Property() {
        return x1;
    }
    public void setX1(double x1) {
        this.x1.set(x1);
    }
    public double getY1() {
        return y1.get();
    }
    public SimpleDoubleProperty y1Property() {
        return y1;
    }
    public void setY1(double y1) {
        this.y1.set(y1);
    }
    public double getX2() {
        return x2.get();
    }
    public SimpleDoubleProperty x2Property() {
        return x2;
    }
    public void setX2(double x2) {
        this.x2.set(x2);
    }
    public double getY2() {
        return y2.get();
    }
    public SimpleDoubleProperty y2Property() {
        return y2;
    }
    public void setY2(double y2) {
        this.y2.set(y2);
    }
    public boolean isArrowAVisible() {
        return arrowAVisible.get();
    }
    public SimpleBooleanProperty arrowAVisibleProperty() {
        return arrowAVisible;
    }
    public void setArrowAVisible(boolean arrowAVisible) {
        this.arrowAVisible.set(arrowAVisible);
    }
    public boolean isArrowBVisible() {
        return arrowBVisible.get();
    }
    public SimpleBooleanProperty arrowBVisibleProperty() {
        return arrowBVisible;
    }
    public void setArrowBVisible(boolean arrowBVisible) {
        this.arrowBVisible.set(arrowBVisible);
    }
    public NodeUI getNode1() {
        return node1;
    }
    public void setNode1(NodeUI node1) {
        this.node1 = node1;
    }
    public NodeUI getNode2() {
        return node2;
    }
    public void setNode2(NodeUI node2) {
        this.node2 = node2;
    }
    public boolean isContentVisible() {
        return contentVisible.get();
    }

    public SimpleBooleanProperty contentVisibleProperty() {
        return contentVisible;
    }

    public void setContentVisible(boolean contentVisible) {
        this.contentVisible.set(contentVisible);
    }
    //endregion
}
