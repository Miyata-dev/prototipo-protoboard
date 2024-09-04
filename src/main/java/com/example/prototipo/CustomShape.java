package com.example.prototipo;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CustomShape extends Rectangle {

    public CustomShape(int x, int y, int width, int height, Color color) {
        super(x, y, width, height);
        //El grosor de la linea de la figura independientemente del tamano que tenga
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(3);
        this.setFill(color);
    }
}