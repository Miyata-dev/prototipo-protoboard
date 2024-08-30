package com.example.prototipo;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Basurero extends ImageView {
    private Image image;
    private Label label;
    private boolean isActive;

    public Basurero(Image image, Label label) {
        this.image = image;
        this.label = label;
        this.isActive = false;
        init();
    }

    public void init() {
        this.setImage(image);
        this.setFitHeight(50);
        this.setFitWidth(50);

        label.setId("basureroLabel");
        label.setTranslateY(60);

        this.setOnMouseClicked(e -> {
            if (!isActive) {
                label.setText("desactivar");
            } else {
                label.setText("activar");
            }

            this.isActive = !this.isActive;
        });
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
}
