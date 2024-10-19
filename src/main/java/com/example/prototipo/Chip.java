package com.example.prototipo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;

public class Chip extends Group {
    private ArrayList<Rectangle> patitas = new ArrayList<>();
    private ArrayList<Double[]> coords = new ArrayList<>(); // coords[0]= x, coords[1] = y
    private ArrayList<CustomCircle> closeCircles = new ArrayList<>();
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

        this.setOnMouseClicked(e -> {
            //TODO desoc
            if (basurero.getIsActive()) {
                gridPaneObserver.getRoot().getChildren().remove(this);
                closeCircles.forEach(circle -> {
                    circle.setisTaken(false);
                });
            }
        });

        this.setOnMouseReleased(e -> {
            closeCircles.clear();
            coords.clear();  // Limpia coords antes de llenarlo de nuevo
            isPlacedCorrectly = true; //para reiniciar la variable.

            //por cada pata, se obtienen las coordenadas y se guardan en una colección.
            patitas.forEach(pata -> {
                //se obtienen las coordenadas de la pata que se mira del chip para agregarlas a la colección de coordenadas.
                double coordX = pata.localToScreen(pata.getX(), pata.getY()).getX();
                double coordY = pata.localToScreen(pata.getX(), pata.getY()).getY();

                Double[] currentCoords = new Double[]{ coordX, coordY };

                coords.add(currentCoords);
            });

            double maxRange = (circlesFromObserver.get(0).getRadius() * 2) - patitas.get(0).getHeight() + 2; //ese es el rango maximo que puede tener.
            //TODO mejorar el algoritmo.
            for (Double[] coord : coords) {
                //obtiene los numeros mas cercanos
                CustomCircle closestCircle = Utils.getClosestCircle(gridPaneObserver.getCirclesCollection(), coord[0], coord[1]);

                double distanceY = Math.abs(coord[1] - closestCircle.getY());
                //se mira que la distancia entre el las coordenadas x e y sean válidas
                if (distanceY >= maxRange) {
                    isPlacedCorrectly = false;
                    return;
                } else {
                    if (!closeCircles.contains(closestCircle)) { //solo se agregan los circulos si no están repetidos en la colección.
                        closeCircles.add(closestCircle);
                    }
                };
            }

            if (isPlacedCorrectly) {
                //se toman los gridNames del primer círculo de arriba y el último círculo de abajo.,
                String firstCircleGridName = closeCircles.get(0).getID().getGridName();
                String secondCircleGridName = closeCircles.get(closeCircles.size() - 1).getID().getGridName();

                //si pertenecen al mismo gridPane no se suelta el Chip.
                if (firstCircleGridName.equals(secondCircleGridName)) return;
                //mira si alguno de los elementos de closeCircles tiene un cable.
                boolean someHaveCables = closeCircles.stream().anyMatch(CustomCircle::hasCable);

                //solo suelta el chip si hay 8 circulos en la colección y no tienen cables.
                if (closeCircles.size() == 8 && !someHaveCables) {
                    //por cada circulo se le setea el estado de ocupado.
                    closeCircles.forEach(circle -> {
                        circle.setisTaken(true);
                    });

                    Utils.makeUndraggableNode(this);
                }
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