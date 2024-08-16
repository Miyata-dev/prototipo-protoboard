package com.example.prototipo;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Bateria {
    private Image image;
    private int voltios;

    public Bateria(Image image, int voltios) {
        this.image = image;
        this.voltios = voltios;
    }

    public ImageView getImage() {
        ImageView imageView = new ImageView(image);

        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setY(250);
        imageView.setX(700);

        return imageView;
    }
}
