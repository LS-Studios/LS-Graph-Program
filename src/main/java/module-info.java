module com.example.graphenprogramm {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.graphenprogramm to javafx.fxml;
    exports com.example.graphenprogramm;

    exports com.example.graphenprogramm.graphLogic;
    opens com.example.graphenprogramm.graphLogic to javafx.fxml;

    exports com.example.graphenprogramm.graphLogic.Algorithm;
    opens com.example.graphenprogramm.graphLogic.Algorithm to javafx.fxml;

    opens com.example.graphenprogramm.graphUI to javafx.fxml;
    exports com.example.graphenprogramm.graphUI;
}