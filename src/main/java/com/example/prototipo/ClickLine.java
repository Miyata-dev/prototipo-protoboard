package com.example.prototipo;


import javafx.scene.Node;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;


public class ClickLine {
    private static Line line;
    private static Line CurrentLine;

    static CustomCircle StartHandler;
    static CustomCircle EndHandler;

    public static void CircleAsignator(AnchorPane root, GridPane gridPane){
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
                CustomCircle Circlefree = (CustomCircle) nodoliberado;
                SetEndHandler(Circlefree);
                System.out.println("El nodo liberado es: "+Circlefree.getId());
                LinePressed(root,mouseEvent);
            } else{
                System.out.println("no se presiono");
            }

        });


        root.setOnMousePressed(e -> {
           if(e.getButton() == MouseButton.PRIMARY){ // Agregar cable
               LinePressed(root,e);
           } else if(e.getButton() == MouseButton.SECONDARY){ // Eliminar el cable
               RightClickRemove(e.getX(), e.getY(),root);

           }

        });
        root.setOnMouseDragged(e -> DragLine(e.getX(), e.getY()));
        root.setOnMouseReleased(e -> MouseReleased(/*e.getX(), e.getY(),*/root,e));

    }

    private static void LinePressed(AnchorPane root,MouseEvent Event){
        if(Event.getPickResult().getIntersectedNode() instanceof CustomCircle) {  //Verifica que empiece solo de los circulos
            CurrentLine = new Line(Event.getSceneX(), Event.getSceneY(), Event.getX(), Event.getY());
            CurrentLine.setStrokeWidth(5);
            root.getChildren().add(CurrentLine);
        }else{
            root.getChildren().remove(CurrentLine);
        }
    }

    private static void RightClickRemove(double x, double y,AnchorPane root){ //Buscar la flecha
        root.getChildren().remove(CurrentLine);
    }

    private static void DragLine(double x, double y){

        if(CurrentLine != null){
            CurrentLine.setEndX(x);
            CurrentLine.setEndY(y);
        }

    }

   private static void MouseReleased(/* double x, double y,*/AnchorPane root, MouseEvent Event){  //Verificar si la conexion es correcta

        if(Event.getPickResult().getIntersectedNode() instanceof CustomCircle) { //Igual entra, ya que termina con la conexion correcta, pero esta mal
            CurrentLine.setEndX(Event.getX());
            CurrentLine.setEndY(Event.getY());
            line = CurrentLine;
        } else{
            //root.getChildren().remove(CurrentLine);
        }
    }






    public static void SetStartHandler(CustomCircle startHandler){
        StartHandler = startHandler;
    }
    public static void SetEndHandler(CustomCircle endHandler){
        EndHandler = endHandler;
    }
}



