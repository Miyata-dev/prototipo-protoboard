package com.example.prototipo;

import javafx.scene.control.Alert;

public class Popup {
    Alert alert = new Alert(Alert.AlertType.WARNING);

    public Popup(String title, String header, String content) {
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
    }

    public void setHeader(String text) {
        alert.setHeaderText(text);
    }

    public void setContent(String content) {
        alert.setContentText(content);
    }

    public void show() {
        alert.showAndWait();
    }
}