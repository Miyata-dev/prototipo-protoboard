package com.example.prototipo;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class Utils {
    public Utils() {
    }
    //pinta una columna de un color dependiendo del estado que tenga (1 ¿ positivo, -1 negativo)
    public static void paintCircles(GridPane grid, int columnToPaint, int state) {
        Iterator circleToPaint = grid.getChildren().iterator();

        while(circleToPaint.hasNext()) {
            Node circle = (Node) circleToPaint.next();
            String targetID = circle.getId();
            ID temporaryID = new ID(targetID);
            CustomCircle targetedCircle = (CustomCircle) circle;

            if (ID.isThisColumn(temporaryID, columnToPaint)) {
                System.out.println("state in paintCircles: " + state);
                targetedCircle.setState(state);
            }
        }
    }

    public static void unPaintCircles(GridPane grid, int columnToUnPaint) {
        Iterator circleToPaint = grid.getChildren().iterator();

        while(circleToPaint.hasNext()) {
            Node circle = (Node) circleToPaint.next();
            String targetID = circle.getId();
            ID temporaryID = new ID(targetID);
            CustomCircle targetedCircle = (CustomCircle) circle;

            if (ID.isThisColumn(temporaryID, columnToUnPaint)) {
                System.out.println("state in paintCircles: " + 0);
                targetedCircle.setState(0);
            }
        }
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