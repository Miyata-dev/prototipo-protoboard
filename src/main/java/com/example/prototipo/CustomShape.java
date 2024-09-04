package com.example.prototipo;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CustomShape extends Rectangle {
    public CustomShape(int x, int y, int width, int height, Color color) {
        super(x, y, width, height);
        //la linea es arbitraria independiente del tamaño que tenga.
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(3);
        this.setFill(color);
    }
}