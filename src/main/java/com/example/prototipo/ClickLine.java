package com.example.prototipo;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import jdk.jshell.execution.Util;

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
            if(nodoclickeado instanceof CustomCircle && !((CustomCircle) nodoclickeado).getIsTaken()){

                CustomCircle Circulo = (CustomCircle) nodoclickeado;

                System.out.println("Estado: "+Circulo.getIsTaken());
                SetStartHandler(Circulo);
                StartHandler.setisTaken(true);

                CurrentLine.setTipodecarga(Circulo.getState()); //se pasa por el cable el tipo de carga que tiene el circulo

                ids[0] = new ID(Circulo.getId());
                LinePressed(root,event);
            }
        });
        //elimina la linea que se presiona, TODO implementar quite de energia una vez el cable se va.
        //root.setOnMouseClicked(Utils::deleteNode);

        root.setOnMouseDragged(e -> {
            //se asegura que acabe en un circulo.
            if (e.getTarget() instanceof Circle) {
                DragLine(e.getX(), e.getY(),e );
            }
        });
        root.setOnMouseReleased(e -> {
            if (EndHandler == null) return;

            //verificar que se suelta en un circulo.
            if (e.getTarget() instanceof CustomCircle) {
                CurrentLine.setIds(ids);
                //si no se hace esto, los cables de la coleccion terminan siendo iguales al ultimo cable.
                Cable current = new Cable(CurrentLine.getLine());
                current.setIds(CurrentLine.getIds());
                current.setTipodecarga(CurrentLine.getTipodecarga());

                cables.add(current);

                System.out.println();
                //recuperamos el nombre del gridName para ver en cuan gridpane pintar
                String endCircleGridName = new ID(EndHandler.getId()).getGridName();

                if (endCircleGridName.equals(firstGridPane.getName())) {
                    Utils.paintCircles(firstGridPane.getGridPane(), ids[1].getIndexColumn(), CurrentLine.getTipodecarga());
                } else {
                    Utils.paintCircles(secondGridPane.getGridPane(), ids[1].getIndexColumn(), CurrentLine.getTipodecarga());
                }

                EndHandler.setisTaken(true);
            }
        });
    }

    private void LinePressed(AnchorPane root,MouseEvent Event){
        if(Event.getPickResult().getIntersectedNode() instanceof CustomCircle) {  //Verifica que empiece solo de los circulos
            CurrentLine.setLine(new Line(Event.getSceneX(), Event.getSceneY(), Event.getX(), Event.getY()));
            CurrentLine.getLine().setStrokeWidth(5);

//            if(ID.isSameID(StartHandler.getID(),EndHandler.getID()) && EndHandler != null){
//
//            }     TODO SE HACE UN CLICK Y SE CREA UN PUNTO EN LOS CIRCULOS, DEJA TERMINAR LA LINEA DONDE HAY CABLES YA Y CAMBIAR EL ESTADO DE LOS CIRCULOS CUANDO SE ELIMINAN LOS CABLES
            root.getChildren().add(CurrentLine.getLine());
        }
    }

    private void DragLine(double x, double y, MouseEvent event){
        if(CurrentLine != null && (event.getPickResult().getIntersectedNode() instanceof CustomCircle) ) {
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
        this.StartHandler = startHandler;

    }
    public void SetEndHandler(CustomCircle endHandler){
        this.EndHandler = endHandler;
    }
}