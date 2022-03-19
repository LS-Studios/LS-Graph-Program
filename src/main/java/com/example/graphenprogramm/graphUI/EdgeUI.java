package com.example.graphenprogramm.graphUI;

import com.example.graphenprogramm.Controller;
import com.example.graphenprogramm.graphLogic.Edge;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;

public class EdgeUI extends Group {
    public Edge EDGE;

    private Polyline edge = new Polyline();
    private Polyline arrowA = new Polyline();
    private Polyline arrowB = new Polyline();

    private SimpleDoubleProperty x1 = new SimpleDoubleProperty();
    private SimpleDoubleProperty y1 = new SimpleDoubleProperty();
    private SimpleDoubleProperty x2 = new SimpleDoubleProperty();
    private SimpleDoubleProperty y2 = new SimpleDoubleProperty();

    private SimpleBooleanProperty arrowAVisible = new SimpleBooleanProperty(true);
    private SimpleBooleanProperty arrowBVisible = new SimpleBooleanProperty(true);

    private double EDGE_SCALER = 20;
    private final double ARROW_ANGLE = Math.toRadians(20);
    private final double ARROW_LENGTH = 10;

    private NodeUI node1, node2;

    private final Pane content = new Pane();
    public static Button selectedContentBtn;
    private boolean removeText = true;

    public EdgeUI(NodeUI node1, NodeUI node2) {
        x1.set(node1.getLayoutX());
        y1.set(node1.getLayoutY());
        x2.set(node2.getLayoutX());
        y2.set(node2.getLayoutY());

        x1.bind(node1.layoutXProperty());
        y1.bind(node1.layoutYProperty());
        x2.bind(node2.layoutXProperty());
        y2.bind(node2.layoutYProperty());

        this.node1 = node1;
        this.node2 = node2;

        getChildren().addAll(edge, arrowA, arrowB);

        for (SimpleDoubleProperty s : new SimpleDoubleProperty[] {this.x1, this.y1, this.x2, this.y2}) {
            s.addListener( (l, o, n) -> update());
        }

        arrowA.visibleProperty().bind(arrowAVisible);
        arrowB.visibleProperty().bind(arrowBVisible);

        update();

        Button contentBtn = new Button("1");
        contentBtn.getStyleClass().add("weightStyle");

        contentBtn.setOnMousePressed(mouseEvent ->
        {
            if (mouseEvent.isPrimaryButtonDown()) {
                if (selectedContentBtn != null) {
                    if (contentBtn == selectedContentBtn) {
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
                    } else {
                        selectNode(contentBtn);
                    }
                } else {
                    selectNode(contentBtn);
                }
            } else if (mouseEvent.isSecondaryButtonDown()) {
                Controller.removeEdge(this);
            }
        });

        content.getChildren().add(contentBtn);

        getChildren().addAll(content);

        //node coordinates = the arrow's mid-point minus 1/2 the width/height, so the content is bang in the centre
        content.layoutXProperty().bind(x2Property().add(x1Property()).divide(2).subtract(content.widthProperty().divide(2)));
        content.layoutYProperty().bind(y2Property().add(y1Property()).divide(2).subtract(content.heightProperty().divide(2)));
    }

    public void update() {
        //If 1 that take height, if 2 that take with
        var value = Math.abs(Math.tanh((y2.get() - y1.get())/(x2.get()-x1.get())));

        double[] start = scale(x1.get(), y1.get(), x2.get(), y2.get(), lerp((node1.getWidth()/2) + EDGE_SCALER, node1.getHeight(), value));
        double[] end = scale(x2.get(), y2.get(), x1.get(), y1.get(), lerp((node2.getWidth()/2) + EDGE_SCALER, node2.getHeight(), value));

        double x1 = start[0];
        double y1 = start[1];
        double x2 = end[0];
        double y2 = end[1];

        edge.getPoints().setAll(x1, y1, x2, y2);

        createArrows(x1, y1, x2, y2);

        setStyleUp();
    }

    private void selectNode(Button contentToSelect) {
        removeText = true;

        if (selectedContentBtn != null) {
            selectedContentBtn.setOnKeyPressed(null);
            selectedContentBtn.getStyleClass().remove("selected");
        }

        selectedContentBtn = contentToSelect;
        selectedContentBtn.getStyleClass().add("selected");

        selectedContentBtn.setOnKeyPressed(keyEvent -> setEdgeWeight(keyEvent, selectedContentBtn));
    }

    public static void deselectSelectedEdge() {
        if (selectedContentBtn != null) {
            selectedContentBtn.setOnKeyPressed(null);
            selectedContentBtn.getStyleClass().remove("selected");
            selectedContentBtn = null;
        }
    }

    private void setEdgeWeight(KeyEvent keyEvent, Button node) {
        String text = node.getText();

        if (removeText) {
            node.setText("");
            removeText = false;
            setEdgeWeight(keyEvent, node);
            return;
        }

        switch (keyEvent.getCode()) {
            case BACK_SPACE -> text = deleteChar(text);
            case SPACE -> text += " ";
            default -> {
                if (keyEvent.getCode().getChar().equals("7") && keyEvent.isShiftDown()) {
                    text += "/";
                } else if (keyEvent.getCode().isDigitKey() || keyEvent.getCode().getChar().equals(".") || keyEvent.getCode().getChar().equals(",")) {
                    text += keyEvent.getCode().getChar();
                }
            }
        }

        node.setText(text);
    }

    private String deleteChar(String text) {
        String newText = text;

        if (text.length() > 0) {
            newText = newText.substring(0, text.length()-1);
        }

        return newText;
    }

    double lerp(double a, double b, double f) {
        return a + f * (b - a);
    }

    private void setStyleUp() {
        edge.getStyleClass().setAll("edge");
        arrowA.getStyleClass().setAll("edge");
        arrowB.getStyleClass().setAll("edge");

        arrowA.getStyleClass().add("arrow");
        arrowB.getStyleClass().add("arrow");
    }

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

    private double[] scale(double x1, double y1, double x2, double y2, double SCALER) {
        double theta = Math.atan2(y2 - y1, x2 - x1);
        return new double[] {
                x1 + Math.cos(theta) * SCALER,
                y1 + Math.sin(theta) * SCALER,
        };
    }

    //Get and set
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
}
