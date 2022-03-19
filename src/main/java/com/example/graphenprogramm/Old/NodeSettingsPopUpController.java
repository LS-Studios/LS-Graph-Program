package com.example.graphenprogramm.Old;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class NodeSettingsPopUpController implements Initializable {

    @FXML
    public Pane pane;

    @FXML
    public Button applyBtn;

    @FXML
    public Button cancelBtn;

    @FXML
    public Button deleteBtn;

    @FXML
    public TextField nameField;

    public static Node selectedNode;

    //Variables
    private double x,y;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Button selectedNodeBtn = (Button) selectedNode;
        nameField.setText(selectedNodeBtn.getText());
    }

    @FXML
    public void apply() {
        Button nodeBtn = (Button) selectedNode;
        nodeBtn.setText(nameField.getText());

        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cancel() {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }

    @FXML
    void deleteNode() {
        EditScreenController.addPaneReference.getChildren().remove(selectedNode);

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
}
