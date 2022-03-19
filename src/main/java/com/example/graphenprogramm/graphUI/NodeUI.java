package com.example.graphenprogramm.graphUI;

import com.example.graphenprogramm.Controller;
import com.example.graphenprogramm.graphLogic.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;

import java.util.ArrayList;

public class NodeUI extends Button {
    public Node NODE;

    public static int count = 0;
    private int ID;

    public ObservableList<EdgeUI> edges = FXCollections.observableArrayList();

    public NodeUI(double x, double y) {
        setLayoutX(x);
        setLayoutY(y);

        translateXProperty().bind(widthProperty().divide(-2));
        translateYProperty().bind(heightProperty().divide(-2));

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
    }
    private boolean isNumeric(String text) {
        try {
            double d = Double.parseDouble(text);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
