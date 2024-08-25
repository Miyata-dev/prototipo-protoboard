package com.example.prototipo;

import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;

import javax.management.monitor.MonitorSettingException;

public class ClickLine {
    private static Line line;
    private static Line CurrentLine;

    static CustomCircle StartHandler;
    static CustomCircle EndHandler;
    static ID[] ids = new ID[2];

    public static void CircleAsignator(AnchorPane root, GridPane gridPane){
        root.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> { //Problema con el evento Arreglar...
            Node nodoclickeado = event.getPickResult().getIntersectedNode();
            if(nodoclickeado instanceof CustomCircle){
                CustomCircle Circulo = (CustomCircle) nodoclickeado;
                SetStartHandler(Circulo);
                ids[0] = new ID(Circulo.getId());
                System.out.println("El presionado es: "+ Circulo.getId());

            } else{
                System.out.println("no se presiono");
            }
        });

//        root.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseEvent -> {        //NO ENTRA NUNCA
//            Node nodoliberado = mouseEvent.getPickResult().getIntersectedNode();
//            if(nodoliberado instanceof CustomCircle){
//                CustomCircle Circlefree = (CustomCircle) nodoliberado;
//                SetEndHandler(Circlefree);
//                System.out.println("El nodo liberado es: "+Circlefree.getId());
//                LinePressed(root,mouseEvent);
//            } else{
//                System.out.println("no se presiono");
//            }
//
//        });
        //elimin la linea que se presiona.
        root.setOnMouseClicked(e -> {
            Node pressedNode = (Node) e.getTarget();

            if (e.getTarget() instanceof Line) {
                ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
            }
        });


        root.setOnMousePressed(e -> {
           // Agregar cable
               LinePressed(root,e);

        });
        root.setOnMouseDragged(e -> DragLine(e.getX(), e.getY(),e ));
        root.setOnMouseReleased(e -> MouseReleased(/*e.getX(), e.getY(),*/root,e));

    }

    private static void LinePressed(AnchorPane root,MouseEvent Event){
        if(Event.getPickResult().getIntersectedNode() instanceof CustomCircle) {  //Verifica que empiece solo de los circulos

            CurrentLine = new Line(Event.getSceneX(), Event.getSceneY(), Event.getX(), Event.getY());
            CurrentLine.setStrokeWidth(5);
            root.getChildren().add(CurrentLine);
        }
    }

    private static void DragLine(double x, double y, MouseEvent event){


        if(CurrentLine != null && (event.getPickResult().getIntersectedNode() instanceof CustomCircle)){
            CustomCircle UltimateCircle = (CustomCircle) event.getPickResult().getIntersectedNode();
            ids[1] = new ID(UltimateCircle.getId());
            System.out.println(ids[0]+" -- "+ids[1]);
            CurrentLine.setEndX(x);
            CurrentLine.setEndY(y);
        }

    }

   private static void MouseReleased(AnchorPane root, MouseEvent Event){  //Verificar si la conexion es correcta

        if(Event.getPickResult().getIntersectedNode() instanceof CustomCircle) { //Igual entra, ya que termina con la conexion correcta, pero esta mal
            CurrentLine.setEndX(Event.getX());
            CurrentLine.setEndY(Event.getY());
            line = CurrentLine;
        }
    }
    public static void SetStartHandler(CustomCircle startHandler){
        StartHandler = startHandler;
    }
    public static void SetEndHandler(CustomCircle endHandler){
        EndHandler = endHandler;
    }
}



