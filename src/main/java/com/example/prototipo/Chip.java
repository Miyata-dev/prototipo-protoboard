package com.example.prototipo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Chip extends Group {
    private ArrayList<Rectangle> patitas = new ArrayList<>();
    private ArrayList<Double[]> coords = new ArrayList<>(); // coords[0]= x, coords[1] = y
    private Basurero basurero;
    private GridPaneObserver gridPaneObserver;
    private boolean isPlacedCorrectly = true;

    public Chip(CustomShape customShape, Basurero basurero, GridPaneObserver gridPaneObserver) {
        super(customShape);
        this.basurero = basurero;
        this.gridPaneObserver = gridPaneObserver;
        addPatitas(customShape);
        this.setTranslateY(-100);

        Utils.makeDraggableNode(this, customShape.getStartX(), customShape.getStartY());
        manageEvents();
    }

    private void manageEvents() {
        ArrayList<CustomCircle> circlesFromObserver = gridPaneObserver.getCirclesCollection();

        //se extraen todos las coordenadas X de los circulos de circlesFromObserver.
        List<Double> coordsXFromObserver = gridPaneObserver.getCirclesCollection()
                .stream()
                .map(CustomCircle::getX)
                .toList();
        //se extraen todos las coordenadas Y de los circulos de circlesFromObserver.
        List<Double> coordsYFromObserver = gridPaneObserver.getCirclesCollection()
                .stream()
                .map(CustomCircle::getY)
                .toList();

        this.setOnMouseClicked(e -> {
            if (basurero.getIsActive()) {
                gridPaneObserver.getRoot().getChildren().remove(this);
            }
        });

        this.setOnMouseReleased(e -> {
            coords.clear();  // Limpia coords antes de llenarlo de nuevo
            isPlacedCorrectly = true; //para reiniciar la variable.

            //por cada pata, se obtienen las coordenadas y se guardan en una colección.
            patitas.forEach(pata -> {
                //se obtienen las coordenadas de la pata que se mira del chip para agregarlas a la colección de coordenadas.
                double coordX = pata.localToScene(pata.getX(), pata.getY()).getX();
                double coordY = pata.localToScene(pata.getX(), pata.getY()).getY();

                Double[] currentCoords = new Double[]{ coordX, coordY };

                coords.add(currentCoords);
            });

            double maxRange = (circlesFromObserver.get(0).getRadius() * 2) - patitas.get(0).getHeight() + 2; //ese es el rango maximo que puede tener.
            //TODO mejorar el algoritmo.
            for (Double[] coord : coords) {
                //obtiene los numeros mas cercanos
                double closestX = Utils.findNearestNumber(coordsXFromObserver, coord[0]);
                double closestY = Utils.findNearestNumber(coordsYFromObserver, coord[1]);

                //calcula la distancia entre la coordenada que está  siendo iterada y la coordenada más cercana.
                double distanceX = Math.abs(closestX - coord[0]);
                double distanceY = Math.abs(closestY - coord[1]);

                //se mira que la distancia entre el las coordenadas x e y
                if ((distanceX > maxRange) || (distanceY > maxRange)) {
                    isPlacedCorrectly = false;
                }
            }

            if (isPlacedCorrectly) {
                Utils.makeUndraggableNode(this);
            }
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
    }
    //se crea una pata y la agrega al grupo de la clase Chip.
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