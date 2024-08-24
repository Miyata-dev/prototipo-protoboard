package com.example.prototipo;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;



public class ClickLine {
    private Line line;
    static CustomCircle StartHandler;
    static CustomCircle EndHandler;

    public static void CreateLine(AnchorPane root, GridPane gridPane){


        root.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> { //Problema con el evento Arreglar...
            int index=0;
            while(index < gridPane.getChildren().size()){
                Node node= gridPane.getChildren().get(index);
                Node node2= gridPane.getChildren().get(index);
                Circle circulo = (Circle) node;
                Circle circulo2 = (Circle) node2;
                circulo.setOnMousePressed(e ->{
                    CustomCircle circuloclick= (CustomCircle) e.getTarget();
                    SetStartHandler(circuloclick);
                    System.out.println("El Custom circle esta siendo presionado con la Id:" + circuloclick.getId());

                });
                index++;
            }
                System.out.println("no le dio a un custom circle");
        });
        root.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
            int index=0;
            while(index < gridPane.getChildren().size()){

                Node node2= gridPane.getChildren().get(index);
                Circle circulo2 = (Circle) node2;
                circulo2.setOnMouseReleased(event1->{
                    CustomCircle circleFree =( CustomCircle) event1.getTarget();
                    SetEndHandler(circleFree);
                    System.out.println("El Custom circle esta siendo liberado con la Id:" + circleFree.getId());
                });
                index++;
            }
            System.out.println("no le dio a un custom circle");
        });
    }


    public static void SetStartHandler(CustomCircle startHandler){
        StartHandler = startHandler;
    }
    public static void SetEndHandler(CustomCircle endHandler){
        EndHandler=endHandler;
    }


}
