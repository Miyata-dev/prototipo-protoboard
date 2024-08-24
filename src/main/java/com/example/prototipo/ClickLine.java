package com.example.prototipo;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;



public class ClickLine {
    private Line line;
    CustomCircle StartHandler, EndHandler;

    public void CreateLine(Pane root, GridPane gridPane){
        root.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            boolean observador=false;
            double x=event.getX();
            double y=event.getY();
            int i=0;
            for (Node node : gridPane.getChildren()) {
                if (node.getLayoutX() == x && node.getLayoutY() == y) {
                    observador = true;
                    break; // salir del bucle si se encuentra el nodo
                }
            }
            if (observador){
                System.out.println("El Custom circle "+ i+ "hola");
            } else {
                System.out.println("no le dio a un custom circle");
            }
        });
    }


}
