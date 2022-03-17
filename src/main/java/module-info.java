module com.example.graphenprogramm {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.graphenprogramm to javafx.fxml;
    exports com.example.graphenprogramm;
}