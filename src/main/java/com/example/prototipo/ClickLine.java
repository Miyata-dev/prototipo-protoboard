package com.example.prototipo;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import java.util.ArrayList;

public class ClickLine {
    private Cable CurrentLine;
    private CustomCircle StartHandler;
    private CustomCircle EndHandler;
    private ID[] ids;

    private AnchorPane root;
    private GridPaneTrailController firstGridPane;
    private GridPaneTrailController secondGridPane;
    private ArrayList<Cable> cables = new ArrayList<>();

    public ClickLine(AnchorPane root, GridPaneTrailController firstGridPane, GridPaneTrailController secondGridPane) {
        this.root = root;
        this.firstGridPane = firstGridPane;
        this.secondGridPane = secondGridPane;
        ids = new ID[2];
        CurrentLine = new Cable(new Line());
    }

    public void CircleAsignator(){
        root.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            Node nodoclickeado = event.getPickResult().getIntersectedNode();
            if(nodoclickeado instanceof CustomCircle){
                CustomCircle Circulo = (CustomCircle) nodoclickeado;
                SetStartHandler(Circulo);
                CurrentLine.setTipodecarga(Circulo.getState()); //se pasa por el cable el tipo de carga que tiene el circulo

                ids[0] = new ID(Circulo.getId());
            }
        });
        //elimina la linea que se presiona, TODO implementar quite de energia una vez el cable se va.
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
        root.setOnMouseDragged(e -> {
            //se asegura que acabe en un circulo.
            if (e.getTarget() instanceof Circle) {
                DragLine(e.getX(), e.getY(),e );
            }
        });
        root.setOnMouseReleased(e -> {
            //verificar que se suelta en un circulo.
            if (e.getTarget() instanceof CustomCircle) {
                CurrentLine.setIds(ids);
                cables.add(CurrentLine);

                //Utils.paintCircles(firstGridPane.getGridPane(), ids[1].getIndexColumn(), CurrentLine.getTipodecarga());

                //recuperamos el nombre del gridName para ver en cuan gridpane pintar
                String endCircleGridName = new ID(EndHandler.getId()).getGridName();

                if (endCircleGridName.equals(firstGridPane.getName())) {
                    Utils.paintCircles(firstGridPane.getGridPane(), ids[1].getIndexColumn(), CurrentLine.getTipodecarga());
                } else {
                    Utils.paintCircles(secondGridPane.getGridPane(), ids[1].getIndexColumn(), CurrentLine.getTipodecarga());
                }
            }
        });
    }

    private void LinePressed(AnchorPane root,MouseEvent Event){
        if(Event.getPickResult().getIntersectedNode() instanceof CustomCircle) {  //Verifica que empiece solo de los circulos
            CurrentLine.setLine(new Line(Event.getSceneX(), Event.getSceneY(), Event.getX(), Event.getY()));
            CurrentLine.getLine().setStrokeWidth(5);
            root.getChildren().add(CurrentLine.getLine());
        }
    }

    private void DragLine(double x, double y, MouseEvent event){
        if(CurrentLine != null && (event.getPickResult().getIntersectedNode() instanceof CustomCircle)){
            CustomCircle UltimateCircle = (CustomCircle) event.getPickResult().getIntersectedNode();
            ids[1] = new ID(UltimateCircle.getId());
            SetEndHandler(UltimateCircle);

            if(!(ID.isSameID(ids[0], ids[1]))){
                CurrentLine.getLine().setEndX(x);
                CurrentLine.getLine().setEndY(y);
            }
        }

    }
    public void SetStartHandler(CustomCircle startHandler){
        StartHandler = startHandler;
    }
    public void SetEndHandler(CustomCircle endHandler){
        EndHandler = endHandler;
    }

    //   private static void MouseReleased(AnchorPane root, MouseEvent Event){  //Verificar si la conexion es correcta
//
//        if(Event.getPickResult().getIntersectedNode() instanceof CustomCircle) { //Igual entra, ya que termina con la conexion correcta, pero esta mal
//            CurrentLine.setEndX(Event.getX());
//            CurrentLine.setEndY(Event.getY());
//            line = CurrentLine;
//        }
//    }
}