package com.example.prototipo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Utils {
    public Utils() {
    }
    //se obtiene una coleccion de CustomCircles a partir de la columna en comun.
    public static ArrayList<CustomCircle> getColumnOfCustomCircles(GridPane grid, int columnToGet) {
        Iterator<Node> circleToPaint = grid.getChildren().iterator();
        ArrayList<CustomCircle> circles = new ArrayList<>();

        while(circleToPaint.hasNext()) {
            Node circle = circleToPaint.next();
            String targetID = circle.getId();
            ID temporaryID = new ID(targetID);
            CustomCircle targetedCircle = (CustomCircle) circle;

            if (ID.isThisColumn(temporaryID, columnToGet)) {
                circles.add(targetedCircle);
            }
        }

        return circles;
    }

    //TODO resolver bug cuando se contecta 2 cables en los grid de voltios. propuesta, debe tomar un gridpanetrail.
    //pinta una columna de un color dependiendo del estado que tenga (1 ¿ positivo, -1 negativo)
    public static void paintCircles(GridPane grid, int columnToPaint, int state) {
        ArrayList<CustomCircle> circles = getColumnOfCustomCircles(grid, columnToPaint);

        circles.forEach(circle -> {
            circle.setState(state);
        });
    }

    public static void unPaintCircles(GridPane grid, int columnToUnPaint) {
        ArrayList<CustomCircle> circles = getColumnOfCustomCircles(grid, columnToUnPaint);

        circles.forEach(circle -> {
            circle.setState(0);
            System.out.println("state in paintCircles: " + circle.getState());
        });
    }

    //retorna null si no encuentra el nodo.
    public static CustomCircle getCustomCircleByID(GridPaneTrailController grid, ID id) {
        CustomCircle foundCircle = null;
        Iterator<Node> circleToPaint = grid.getGridPane().getChildren().iterator();

        while(circleToPaint.hasNext()) {
            Node circle = (Node) circleToPaint.next();
            String targetID = circle.getId();
            ID temporaryID = new ID(targetID);
            CustomCircle targetedCircle = (CustomCircle) circle;

            if (ID.isSameID(temporaryID, id)) {
                foundCircle = targetedCircle;
            }
        }

        return foundCircle;
    }
    //devuelve el numero de cables que tiene una columna.
    public static int numberOfCables(GridPane grid, int columnToInspect) {
        int count = 0;
        ArrayList<CustomCircle> circles = getColumnOfCustomCircles(grid, columnToInspect);

        for (CustomCircle circle : circles) {
            if (circle.getIsTaken()) {
                count++;
            }
        }

        return count;
    }

    public static void deleteCable(MouseEvent e, GridPaneTrailController gridOne, GridPaneTrailController gridTwo) {
        //estos son los nombres que usa internamente las ID de los circulos pertenecientes al centro.
        String[] validGridNames = {
                "gridTrail1",
                "gridTrail2"
        };

        Node pressedNode = (Node) e.getTarget();
        if (!(e.getTarget() instanceof Cable pressedCable)) return;

        ID firstID = pressedCable.getIds()[0];
        ID secondID = pressedCable.getIds()[1];

        //se revisa que la id esté en uno de los gridpanes del medio, ya que ahi es donde se desconectara, si pertenece a los volts, no se realizarán las operaciones.
        boolean isFirstIdValid = Arrays.asList(validGridNames).contains(firstID.getGridName());
        boolean isSecondIdValid = Arrays.asList(validGridNames).contains(secondID.getGridName());

        //Obtener el gridpane en donde esta el circulo con la id[1] (secondID), solo se buscan si son validas
        GridPane firstCircleGridPane = null;
        GridPane secondCircleGridPane = null;

        if (isSecondIdValid && secondID.getGridName().equals(gridTwo.getName())) {
            secondCircleGridPane = gridTwo.getGridPane();
        } else if (isSecondIdValid && secondID.getGridName().equals(gridOne.getName())) {
            secondCircleGridPane = gridOne.getGridPane();
        }

        //solo si tiene un cable, se quita la energia, TODO mejorar la condicion, es defectuoza en edge cases.
        if (secondCircleGridPane != null && numberOfCables(secondCircleGridPane, secondID.getIndexColumn()) <= 1) {
            unPaintCircles(secondCircleGridPane, secondID.getIndexColumn());
            System.out.println("number of cables: " + numberOfCables(secondCircleGridPane, secondID.getIndexColumn()));
        }

        if (isFirstIdValid && firstID.getGridName().equals(gridTwo.getName())) {
            firstCircleGridPane = gridTwo.getGridPane();
        } else if (isFirstIdValid && firstID.getGridName().equals(gridOne.getName())) {
            firstCircleGridPane = gridOne.getGridPane();
        }

        if (firstCircleGridPane != null && numberOfCables(firstCircleGridPane, firstID.getIndexColumn()) <= 1) {
            unPaintCircles(firstCircleGridPane, firstID.getIndexColumn());
            System.out.println("number of cables: " + numberOfCables(firstCircleGridPane, firstID.getIndexColumn()));
        }

        ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
    }

    //necesita el evento y el nodo para poder aplicar los calculos.
    private static void calculateOffSet(MouseEvent e, Node node, AtomicReference<Double> startX, AtomicReference<Double> startY) {
        startX.set(e.getSceneX() - node.getTranslateX()); //get x value  or the cursor
        startY.set(e.getSceneY() - node.getTranslateY()); //get y value of the cursor.
    }

    private static void setOnDragFigure(MouseEvent e, Node node, AtomicReference<Double> startX, AtomicReference<Double> startY) {
        node.setTranslateX(e.getSceneX() - startX.get());
        node.setTranslateY(e.getSceneY() - startY.get());
    }

    public static void makeDraggableNode(Node node, AtomicReference<Double> startX, AtomicReference<Double> startY) {
        //Para hacer que funcione necesitamos utilizar AtomicReference
        node.setOnMousePressed(e -> {
            calculateOffSet(
                    e,
                    node,
                    startX,
                    startY
            );
        });

        node.setOnMouseDragged(e -> {
            setOnDragFigure(
                    e,
                    node,
                    startX,
                    startY
            );
        });

        node.setOnMouseReleased(e -> {
            System.out.println(e.getTarget());
        });
    }

    public static void makeUndraggableNode(Node node) {
        //
        node.setOnMousePressed(null);
        node.setOnMouseDragged(null);
    }
}