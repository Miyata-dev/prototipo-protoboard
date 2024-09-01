package com.example.prototipo;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import java.util.ArrayList;

public class ClickLine {
    private Cable CurrentLine;
    private CustomCircle StartHandler;//El circulo inicial al que estara vinculado el cable
    private CustomCircle EndHandler;//El circulo final al que estara vinculado el cable
    private ID[] ids= new ID[2];
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
        CurrentLine = new Cable(new Line());
    }

    public void CircleAsignator(){
        root.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            Node nodoclickeado = event.getPickResult().getIntersectedNode();
            if(nodoclickeado instanceof CustomCircle && !((CustomCircle) nodoclickeado).getIsTaken()){

                CustomCircle Circulo = (CustomCircle) nodoclickeado;
                SetStartHandler(Circulo);
                StartHandler.setisTaken(true);
                CurrentLine.setTipodecarga(Circulo.getState()); //se pasa por el cable el tipo de carga que tiene el circulo
                ids[0] = new ID(Circulo.getID().getGeneratedID());//Esto hay que cambiar para que unicamente podamos mover una linea del Switch
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
            boolean canDelete = basurero.getIsActive();
            if (!canDelete) return;
            if (!(e.getTarget() instanceof Line)) return;

            System.out.println("im ALSO here");
            Utils.deleteCable(e);
        });
    }

    private void LinePressed(AnchorPane root,MouseEvent Event){
        if(Event.getPickResult().getIntersectedNode() instanceof CustomCircle) {  //Verifica que empiece solo de los circulos
            CurrentLine.setLine(new Line(Event.getSceneX(), Event.getSceneY(), Event.getX(), Event.getY()));
            CurrentLine.getLine().setStrokeWidth(5);
            //TODO SE HACE UN CLICK Y SE CREA UN PUNTO EN LOS CIRCULOS
            root.getChildren().add(CurrentLine.getLine());
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
            //Error
            if(!(ID.isSameID(ids[0], ids[1]))){
                CurrentLine.getLine().setEndX(x);
                CurrentLine.getLine().setEndY(y);
            }
        }
    }

    private void RealizeLine(MouseEvent e) {
        System.out.println("END" + EndHandler);
        System.out.println("START" + StartHandler);
        if (EndHandler == null || StartHandler == null) {  // REVISAR METODO, PORQUE AL PRESIONAR EL CIRCULO FINAL DE LA LINEA SE ELIMINA EL CABLE

            CustomCircle circle = (CustomCircle) e.getTarget();
            //si no esta tomado, se sale de la funcion, y por lo tanto no se elimina.
            if (!circle.getIsTaken()) return;
            if (StartHandler == null && circle.hasEnergy()) return;

            root.getChildren().remove(CurrentLine.getLine());
            CurrentLine = new Cable(new Line());

            if (StartHandler != null){
                StartHandler.setisTaken(false);
            }
            return;
        }

        CurrentLine.setIds(ids);
        //si no se hace esto, los cables de la coleccion terminan siendo iguales al ultimo cable.
        Cable current = new Cable(CurrentLine.getLine());
        current.setIds(CurrentLine.getIds());
        current.setTipodecarga(CurrentLine.getTipodecarga());
        cables.add(current);
        //si el circulo inicial no tiene energia, se toma la energia del circulo final.
        if (!StartHandler.hasEnergy()) {
            String startCircleGridName = new ID(StartHandler.getID().getGeneratedID()).getGridName();
            if (startCircleGridName.equals(firstGridPane.getName())) {
                Utils.paintCircles(firstGridPane.getGridPane(), ids[0].getIndexColumn(), EndHandler.getState());
            } else {
                Utils.paintCircles(secondGridPane.getGridPane(), ids[0].getIndexColumn(), EndHandler.getState());
            }
            EndHandler.setisTaken(true);
            //StartHandler.setisTaken(true);
        } else {
        //recuperamos el nombre del gridName para ver en cuan gridpane pintar
            String endCircleGridName = new ID(EndHandler.getId()).getGridName();
            if (endCircleGridName.equals(firstGridPane.getName())) {
            Utils.paintCircles(firstGridPane.getGridPane(), ids[1].getIndexColumn(), CurrentLine.getTipodecarga());
            }else {
                Utils.paintCircles(secondGridPane.getGridPane(), ids[1].getIndexColumn(), CurrentLine.getTipodecarga());
            }
            EndHandler.setisTaken(true);
            //StartHandler.setisTaken(true);
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