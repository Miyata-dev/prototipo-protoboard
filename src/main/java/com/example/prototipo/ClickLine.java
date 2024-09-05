package com.example.prototipo;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
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
    private Basurero basurero;

    public ClickLine(AnchorPane root, GridPaneTrailController firstGridPane, GridPaneTrailController secondGridPane, Basurero basurero) {
        this.root = root;
        this.firstGridPane = firstGridPane;
        this.secondGridPane = secondGridPane;
        this.basurero = basurero;
        ids = new ID[2];
        CurrentLine = new Cable();
    }

    public void CircleAsignator(){
        root.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            Node nodoclickeado = event.getPickResult().getIntersectedNode();
            if(nodoclickeado instanceof CustomCircle && !((CustomCircle) nodoclickeado).getIsTaken()){

                CustomCircle Circulo = (CustomCircle) nodoclickeado;

                SetStartHandler(Circulo);
                StartHandler.setisTaken(true);

                CurrentLine.setTipodecarga(Circulo.getState()); //se pasa por el cable el tipo de carga que tiene el circulo
                ids[0] = new ID(Circulo.getID().getGeneratedID());

                LinePressed(root,event);
            }
        });
        // TODO implementar quite de energia una vez el cable se va.
        root.setOnMouseDragged(e -> {
            //se asegura que acabe en un circulo.
            if (e.getTarget() instanceof Circle) {
                DragLine(e.getX(), e.getY(),e );
            }
        });
        root.setOnMouseReleased(e -> {
            if(!(e.getTarget() instanceof CustomCircle)) return;
            RealizeLine(e);
        });

        root.setOnMouseClicked(e -> {
            System.out.println(e.getTarget() instanceof Group);
            boolean canDelete = basurero.getIsActive();
            if (!canDelete) return;
            if (!(e.getTarget() instanceof Line || e.getTarget() instanceof Rectangle)) return;

            System.out.println(e.getTarget() instanceof Switch);

            if (e.getTarget() instanceof Switch pressedNode) {
                System.out.println("im method...");
                //Utils.deleteNode(pressedNode);
            } else {
                Utils.deleteCable(e, firstGridPane, secondGridPane);
            }
        });
    }

    private void LinePressed(AnchorPane root,MouseEvent Event){
        if(Event.getPickResult().getIntersectedNode() instanceof CustomCircle) {  //Verifica que empiece solo de los circulos
            //CurrentLine.setLine(Event.getSceneX(), Event.getSceneY(), Event.getX(), Event.getY());
            int carga = CurrentLine.getTipodecarga();
            CurrentLine = new Cable(Event.getSceneX(), Event.getSceneY(), Event.getX(), Event.getY());
            CurrentLine.setTipodecarga(carga);
            CurrentLine.setStrokeWidth(5);
            //TODO SE HACE UN CLICK Y SE CREA UN PUNTO EN LOS CIRCULOS
            root.getChildren().add(CurrentLine);
        }
    }

    private void DragLine(double x, double y, MouseEvent event){
        if(CurrentLine != null && (event.getPickResult().getIntersectedNode() instanceof CustomCircle) ) {
            CustomCircle UltimateCircle = (CustomCircle) event.getPickResult().getIntersectedNode();

            if(UltimateCircle.getIsTaken()){
                return;
            }
            ids[1] = new ID(UltimateCircle.getID().getGeneratedID());
            SetEndHandler(UltimateCircle);

            if(!(ID.isSameID(ids[0], ids[1]))){
                CurrentLine.setEndX(x);
                CurrentLine.setEndY(y);
            }
        }
    }
    //TODO colocar setIsTaken en el deleteCable.
    private void RealizeLine(MouseEvent e) {
        if (EndHandler == null || StartHandler == null) {  // REVISAR METODO, PORQUE AL PRESIONAR EL CIRCULO FINAL DE LA LINEA SE ELIMINA EL CABLE

            CustomCircle circle = (CustomCircle) e.getTarget();
            //si no esta tomado, se sale de la funcion, y por lo tanto no se elimina.
            System.out.println("state: " + circle.getState());
            if (!circle.getIsTaken()) return;
            if (StartHandler == null && circle.hasEnergy()) return;

            root.getChildren().remove(CurrentLine);

            if (StartHandler != null){
                StartHandler.setisTaken(false);
            }
            return;
        }

        CurrentLine.setIds(ids);
        //si no se hace esto, los cables de la coleccion terminan siendo iguales al ultimo cable.
        Cable current = new Cable();
        current.setIds(CurrentLine.getIds());
        current.setTipodecarga(CurrentLine.getTipodecarga());
        StartHandler.setCable(current);
        EndHandler.setCable(current);
        System.out.println("EndHandler" + EndHandler.getID());
        System.out.println("StartHandler" + StartHandler.getID());
        System.out.println("id 1:" + StartHandler.getCable().getIds()[1] + "id2" + StartHandler.getCable().getIds()[0]);
        System.out.println("id 1:" + EndHandler.getCable().getIds()[1] + "id2" + EndHandler.getCable().getIds()[0]);
        cables.add(current);

        System.out.println(CurrentLine.getTipodecarga());
        //si el circulo inicial no tiene energia, se toma la energia del circulo final.
        if (!StartHandler.hasEnergy() && StartHandler.getID().getIsForGridpane()) {
            String startCircleGridName = StartHandler.getID().getGridName();
            if (startCircleGridName.equals(firstGridPane.getName())) {
                Utils.paintCircles(firstGridPane.getGridPane(), ids[0], EndHandler.getState());
            } else {
                Utils.paintCircles(secondGridPane.getGridPane(), ids[0], EndHandler.getState());
            }
            EndHandler.setisTaken(true);
            //StartHandler.setisTaken(true);
        } else {
        //recuperamos el nombre del gridName para ver en cuan gridpane pintar
            if (StartHandler.getID().getIsForGridpane()) {
                String endCircleGridName = EndHandler.getID().getGridName();
                if (endCircleGridName.equals(firstGridPane.getName())) {
                    Utils.paintCircles(firstGridPane.getGridPane(), ids[1], CurrentLine.getTipodecarga());
                }else {
                    Utils.paintCircles(secondGridPane.getGridPane(), ids[1], CurrentLine.getTipodecarga());
                }
                EndHandler.setisTaken(true);
                //StartHandler.setisTaken(true);
            }
        }
        StartHandler = null;
        EndHandler = null;
    }

    public void SetStartHandler(CustomCircle startHandler){
        this.StartHandler = startHandler;

    }
    public void SetEndHandler(CustomCircle endHandler){
        this.EndHandler = endHandler;
    }
}