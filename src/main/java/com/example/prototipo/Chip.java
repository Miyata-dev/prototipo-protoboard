package com.example.prototipo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Chip extends Group {
    public Chip(CustomShape customShape) {
        super(customShape);
        addPatitas(customShape);
        this.setTranslateY(-100);
    }

    public void addPatitas(CustomShape customShape) {
        System.out.println("width: " + customShape.getWidth() + ", height: " + customShape.getHeight());
    }



}