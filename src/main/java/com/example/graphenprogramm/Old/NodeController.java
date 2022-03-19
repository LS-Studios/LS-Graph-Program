package com.example.graphenprogramm.Old;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NodeController implements Initializable {

    @FXML
    public Button button;

    //Variables
    private double x,y;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    public void clickNode(MouseEvent event) throws IOException {
        if (event.isStillSincePress()) {
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
    }

    @FXML
    void dragNode(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();

        button.setTranslateX(x - (button.getBoundsInParent().getWidth()/2));
        button.setTranslateY(y - (button.getBoundsInParent().getHeight() * 1.5));
    }
}
