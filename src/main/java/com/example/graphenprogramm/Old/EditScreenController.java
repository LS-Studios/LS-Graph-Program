package com.example.graphenprogramm.Old;

import com.example.graphenprogramm.Main;
import com.example.graphenprogramm.graphLogic.Graph;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditScreenController implements Initializable {

    @FXML
    public Pane pane;

    @FXML
    public Pane addPane;
    public static Pane addPaneReference;

    @FXML
    public GridPane bgGrid;

    public static Graph graph;
    private static ContextMenu contextMenu;

    //Variables
    private double x,y;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addPaneReference = addPane;

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

    @FXML
    public void quit() {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }

    @FXML
    void dragged(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() -y);

    }

    @FXML
    void pressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    @FXML
    public void createNode() throws IOException {
        Node createdNode = FXMLLoader.load(getClass().getResource("PlaceHolderNode.fxml"));

        addPane.getChildren().add(createdNode);

        Button nodeButton = (Button) createdNode;
        Main.globalNodes.add(new Pair<>(createdNode, graph.addNode(nodeButton.getText())));

        NodeSettingsPopUpController.selectedNode = nodeButton;

        Parent root = FXMLLoader.load(getClass().getResource("NodeSettingsPopUp.fxml"));

        Scene popUpScene = new Scene(root);
        popUpScene.setFill(Color.TRANSPARENT);

        Stage popUpStage = new Stage();
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.initStyle(StageStyle.TRANSPARENT);
        popUpStage.setTitle("Node edit");
        popUpStage.setScene(popUpScene);

        popUpStage.showAndWait();
    }

    @FXML
    public void removeContextMenu() {
        if (contextMenu != null) {
            contextMenu.hide();
        }
    }

    @FXML
    public void createRightClickMenu(MouseEvent event) throws IOException {
        if (event.getButton() == MouseButton.SECONDARY) {
            contextMenu = new ContextMenu();

            x = event.getSceneX();
            y = event.getSceneY();

            MenuItem item1 = new MenuItem("Create node");
            MenuItem item2 = new MenuItem("Create edge");

            item1.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    Node createdNode = null;
                    try {
                        createdNode = FXMLLoader.load(getClass().getResource("PlaceHolderNode.fxml"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    addPane.getChildren().add(createdNode);

                    Button nodeButton = (Button) createdNode;
                    Main.globalNodes.add(new Pair<>(createdNode, graph.addNode(nodeButton.getText())));

                    NodeSettingsPopUpController.selectedNode = nodeButton;

                    Parent root = null;
                    try {
                        root = FXMLLoader.load(getClass().getResource("NodeSettingsPopUp.fxml"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    nodeButton.setTranslateX(x);
                    nodeButton.setTranslateY(y - 60);

                    Scene popUpScene = new Scene(root);
                    popUpScene.setFill(Color.TRANSPARENT);

                    Stage popUpStage = new Stage();
                    popUpStage.initModality(Modality.APPLICATION_MODAL);
                    popUpStage.initStyle(StageStyle.TRANSPARENT);
                    popUpStage.setTitle("Node edit");
                    popUpStage.setScene(popUpScene);

                    popUpStage.showAndWait();
                }
            });

            item2.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    System.out.println("item2");
                }
            });

            contextMenu.getItems().addAll(item1, item2);

            contextMenu.show(addPane, event.getScreenX(), event.getScreenY());
        }
    }
}
