package com.example.prototipo;

import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;

public class Chip extends Group {
    private ArrayList<Rectangle> patitas = new ArrayList<>();
    private ArrayList<Double[]> coords = new ArrayList<>(); // coords.get(0) = x, coords.get(1) = y

    public Chip(CustomShape customShape, Basurero basurero, AnchorPane root) {
        super(customShape);
        addPatitas(customShape);
        this.setTranslateY(-100);

        Utils.makeDraggableNode(this, customShape.getStartX(), customShape.getStartY());

        this.setOnMouseClicked(e -> {
            Utils.makeUndraggableNode(this);

            if (basurero.getIsActive()) {
                root.getChildren().remove(this);
            }

        });

        this.setOnMouseReleased(e -> {
            patitas.forEach(pata -> {
                //se obtienen las coordenadas de la pata que se mira del chip para agregarlas a la colección de coordenadas.
                double coordX = pata.localToScene(pata.getX(), pata.getY()).getX();
                double coordY = pata.localToScene(pata.getX(), pata.getY()).getY();

                Double[] currentCoords = new Double[]{ coordX, coordY };

                coords.add(currentCoords);
            });
        });
    }

    public void addPatitas(CustomShape customShape) {
        int initailX = 4;
        int initialY = 7;
        int incrementX = 18; //12 -> 18 6 * 4
        double heightDifference = customShape.getHeight() + 7; //para poder crear las patas de artriba y abajo se tiene la diferencia de altura.

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

    private void crearPatita(CustomShape customShape, double xOffSet, double yOffSet) {
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