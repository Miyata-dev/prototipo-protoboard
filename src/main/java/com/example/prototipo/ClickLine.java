package com.example.prototipo;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;



public class ClickLine {
    private Line line;
    CustomCircle StartHandler, EndHandler;

    public static void CreateLine(AnchorPane root, GridPane gridPane){
        root.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            boolean observador=false;
            double x=event.getX();
            double y=event.getY();
            for (Node node : gridPane.getChildren()) {
                Circle circulo = (Circle) node;
                circulo.setOnMouseClicked(mouseEvent ->{
                    CustomCircle startHandler = (CustomCircle) mouseEvent.getTarget();

                    System.out.println("El Custom circle esta siendo presionado con la Id:" + startHandler.getId());
                });
            }
                System.out.println("no le dio a un custom circle");
        });
    }


}
