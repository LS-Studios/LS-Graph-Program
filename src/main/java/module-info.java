module com.example.graphenprogramm {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.graphenprogramm to javafx.fxml;
    exports com.example.graphenprogramm;
    exports com.example.graphenprogramm.graphLogic;
    opens com.example.graphenprogramm.graphLogic to javafx.fxml;
    exports com.example.graphenprogramm.Old;
    opens com.example.graphenprogramm.Old to javafx.fxml;
}