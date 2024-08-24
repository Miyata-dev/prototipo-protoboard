package com.example.prototipo;


import javafx.scene.Node;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import javafx.scene.shape.Line;



public class ClickLine {
    private Line line;
    static CustomCircle StartHandler;
    static CustomCircle EndHandler;

    public static void CreateLine(AnchorPane root, GridPane gridPane){


        root.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> { //Problema con el evento Arreglar...
            Node nodoclickeado = event.getPickResult().getIntersectedNode();
            if(nodoclickeado instanceof CustomCircle){
                CustomCircle Circulo = (CustomCircle) nodoclickeado;
                SetStartHandler(Circulo);
                System.out.println("El presionado es: "+ Circulo.getId());
            } else{
                System.out.println("no se presiono");
            }
        });
        root.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
            Node nodoliberado = mouseEvent.getPickResult().getIntersectedNode();
            if(nodoliberado instanceof CustomCircle){
                CustomCircle Circlefree= (CustomCircle) nodoliberado;
                SetEndHandler(Circlefree);
                System.out.println("El nodo liberado es: "+Circlefree.getId());
            } else{
                System.out.println("no se presiono");
            }
        });
    }


    public static void SetStartHandler(CustomCircle startHandler){
        StartHandler = startHandler;
    }
    public static void SetEndHandler(CustomCircle endHandler){
        EndHandler=endHandler;
    }


}
