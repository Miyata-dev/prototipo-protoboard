package com.example.prototipo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;

public class Chip extends Group {
    private ArrayList<Rectangle> patitas = new ArrayList<>();

    public Chip(CustomShape customShape) {
        super(customShape);
        addPatitas(customShape);
        this.setTranslateY(-100);

        Utils.makeDraggableNode(this, customShape.getStartX(), customShape.getStartY());

        this.setOnMouseClicked(e -> {
            Utils.makeUndraggableNode(this);
        });

        patitas.forEach(pata -> {

            System.out.println(pata.getBoundsInParent());
        });
    }

    public void addPatitas(CustomShape customShape) {
        int initailX = 4;
        int initialY = 7;
        int incrementX = 18; //12 -> 18 6 * 4
        int heightDifference = 29; //para poder crear las patas de artriba y abajo se tiene la diferencia de altura.

        //primer for para crear las patas de arriba
        for (int i = 0; i < 4; i++) {
            crearPatita(customShape,initailX + incrementX * i, initialY);
        }
        //crea las patas de abajo.
        for (int i = 0; i < 4; i++) {
            crearPatita(customShape,initailX + incrementX * i, initialY - heightDifference);
        }

        System.out.println("width: " + customShape.getWidth() + ", height: " + customShape.getHeight());
    }

    public void crearPatita(CustomShape customShape, double xOffSet, double yOffSet) {
        double widthX = customShape.getX();
        double heightY = customShape.getY();

        Rectangle rectangle = new Rectangle(7, 7);

        rectangle.setLayoutY(heightY - yOffSet);
        rectangle.setLayoutX(widthX + xOffSet);
        rectangle.setFill(Color.BLACK);

        patitas.add(rectangle);

        this.getChildren().add(rectangle);
    }
}