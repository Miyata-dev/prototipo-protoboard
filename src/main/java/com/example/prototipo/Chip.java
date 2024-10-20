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
        manageEvents(customShape);
    }


    private void manageEvents(CustomShape customShape) {
        //Conseguimos la coleccion de circulos
        ArrayList<CustomCircle> circlesFromObserver = gridPaneObserver.getCirclesCollection();

        this.setOnMouseClicked(e -> {
            if (basurero.getIsActive() && customShape.getHasMoved()) {
                gridPaneObserver.getRoot().getChildren().remove(this);
                closeCircles.forEach(circle -> {
                    circle.setisTaken(false);
                });
            }

            if (basurero.getIsActive()) customShape.setHasMoved(true);
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

                //Si al menos uno de los circulos encontrados tiene un cable se sale del metodo
                if (closestCircle.hasCable()) return;

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

                if (closeCircles.size() == 8) {
                    //por cada circulo se le setea el estado de ocupado.
                    closeCircles.forEach(circle -> {
                        circle.setisTaken(true);
                    });

                    //Unicamente el Chip deberia ser Undraggable cuando la cantidad de circulos encontrados son 8
                    Utils.makeUndraggableNode(this);
                }

            }
        });
    }

    //Este metodo lo que hace es crear patas y despues añadirla al grupo
    public void addPatitas(CustomShape customShape) {
        int initailX = 4;
        int initialY = 7;
        int incrementX = 18; //12 -> 18 6 * 4
        double heightDifference = customShape.getHeight() + 7; //para poder crear las patas de artriba y abajo se tiene la diferencia de altura.

        //este primer ciclo crea las patas de la parte superior del Chip
        for (int i = 0; i < 4; i++) {
            crearPatita(customShape,initailX + incrementX * i, initialY);
        }
        //crea las patas de la parte inferior del Chip.
        for (int i = 0; i < 4; i++) {
            crearPatita(customShape,initailX + incrementX * i, initialY - heightDifference);
        }
    }
    //Este metodo crea una pata y la agrega al grupo de la clase Chip.
    private void crearPatita(CustomShape customShape, double xOffSet, double yOffSet) {

        //obtenemos la ubicacion del custom shape
        double widthX = customShape.getX();
        double heightY = customShape.getY();

        //Para asi poder darles una ubicacion a la figura que seria un rectangulo
        Rectangle rectangle = new Rectangle(7, 7);

        rectangle.setLayoutY(heightY - yOffSet);
        rectangle.setLayoutX(widthX + xOffSet);
        rectangle.setFill(Color.BLACK);

        patitas.add(rectangle);

        this.getChildren().add(rectangle);
    }
}