package com.example.prototipo;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import javafx.scene.Node;
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

    public static void makeDraggableNode(Node node) {
        //Para hacer que funcione necesitamos utilizar AtomicReference
        AtomicReference<Double> startX = new AtomicReference<>((double) 0);
        AtomicReference<Double> startY = new AtomicReference<>((double) 0);

        node.setOnMousePressed(e -> {
            //calculate offset points
            startX.set(e.getSceneX() - node.getTranslateX()); //get x value  or teh cursor
            startY.set(e.getSceneY() - node.getTranslateY()); //get x value or teh cursor
        });

        node.setOnMouseDragged(e -> {
            //set values every time the mouse drags.
            node.setTranslateX(e.getSceneX() - startX.get());
            node.setTranslateY(e.getSceneY() - startY.get());
        });

        node.setOnMouseReleased(e -> {
            Node target = (Node) e.getTarget();
        });
    }
}