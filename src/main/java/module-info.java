module com.example.prototipo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.management;


    opens com.example.prototipo to javafx.fxml;
    exports com.example.prototipo;
}