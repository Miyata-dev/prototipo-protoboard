package com.example.prototipo;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import java.util.ArrayList;
import java.util.Arrays;

public class ClickLine {
    private Cable CurrentLine;
    private CustomCircle StartHandler;
    private CustomCircle EndHandler;
    private ID[] ids;
    private AnchorPane root;
    private GridPaneTrailController firstGridPane;
    private GridPaneTrailController secondGridPane;
    private GridPaneController firsGridPaneVolt;
    private GridPaneController secondGridPaneVolt;
    private Bateria bateria;
    private ArrayList<Cable> cables = new ArrayList<>();
    private Basurero basurero;
    private CustomShape rec;

    public ClickLine(AnchorPane root, GridPaneTrailController firstGridPane, GridPaneTrailController secondGridPane,GridPaneController firsGridPaneVolt1,GridPaneController firsGridPaneVolt2, Basurero basurero, Bateria bateria) {
        this.root = root;
        this.firstGridPane = firstGridPane;
        this.secondGridPane = secondGridPane;
        this.firsGridPaneVolt = firsGridPaneVolt1;
        this.secondGridPaneVolt = firsGridPaneVolt2;
        this.basurero = basurero;
        this.bateria = bateria;
        ids = new ID[2];
        CurrentLine = new Cable();
    }

    public void CircleAsignator(){
        root.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            Node nodoclickeado = event.getPickResult().getIntersectedNode();
            if(nodoclickeado instanceof CustomCircle && !((CustomCircle) nodoclickeado).getIsTaken()){
                ids = new ID[2]; //se asegura de que el cable tome la referencia del objeto actual y no el anterior.

                CustomCircle Circulo = (CustomCircle) nodoclickeado;

                SetStartHandler(Circulo);
                StartHandler.setisTaken(true);

//                CurrentLine.setRandomID();

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
            if (e.getTarget() instanceof CustomShape shape) {
                rec = shape;
            }

            if(!(e.getTarget() instanceof CustomCircle)) return;
            RealizeLine(e);
        });

        root.setOnMouseClicked(e -> {
            boolean canDelete = basurero.getIsActive();
            if (!canDelete) return;
            //if (!(e.getTarget() instanceof Line || e.getTarget() instanceof CustomShape)) return;

            Utils.deleteCable(e, firstGridPane, secondGridPane, firsGridPaneVolt, secondGridPaneVolt, bateria, cables);
        });
    }
    //TODO: aca puede que ocurran los ids duplicados.
    private void LinePressed(AnchorPane root,MouseEvent Event){
        if(Event.getPickResult().getIntersectedNode() instanceof CustomCircle) {  //Verifica que empiece solo de los circulos
            //CurrentLine.setLine(Event.getSceneX(), Event.getSceneY(), Event.getX(), Event.getY());
            int carga = CurrentLine.getTipodecarga();
            //String previousID = CurrentLine.getRandomID();
            CurrentLine = new Cable(Event.getSceneX(), Event.getSceneY(), Event.getX(), Event.getY());
            CurrentLine.setTipodecarga(carga);
            CurrentLine.setStrokeWidth(5);
            CurrentLine.setRandomID();

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
            //se da valor del cable de nuevo, ya que el EndHandler se actualiza, y por ende pierdse el valor del cable.

            if(!(ID.isSameID(ids[0], ids[1]))){
                CurrentLine.setEndX(x);
                CurrentLine.setEndY(y);
            }

        }
    }
    //TODO colocar setIsTaken en el deleteCable.
    private void RealizeLine(MouseEvent e) {
        //
        String[] gridNames = {
                "switchvolt1",
                "LedVolt1",
                "BateryVolt"
        };

        String[] edgeCases = {
                "gridVolt1",
                "gridVolt2"
        };

        if (EndHandler == null || StartHandler == null) {  // REVISAR METODO, PORQUE AL PRESIONAR EL CIRCULO FINAL DE LA LINEA SE ELIMINA EL CABLE

            CustomCircle circle = (CustomCircle) e.getTarget();
            //si no esta tomado, se sale de la funcion, y por lo tanto no se elimina.
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
        current.setRandomID(CurrentLine.getRandomID());

        current.SetCircles(new CustomCircle[] {StartHandler, EndHandler });

        StartHandler.setCable(current);
        EndHandler.setCable(current);



        cables.add(current);

        if (rec != null && !StartHandler.getID().getIsForGridpane()) {
            System.out.println("pata 1 del led");

            if (StartHandler.getID().getIndexRow() == 1) {
                System.out.println("ID 1");
                rec.setLeg1(StartHandler);
                System.out.println(StartHandler.getCable());
            } else if (StartHandler.getID().getIndexRow() == 2) {
                System.out.println("ID 2");
                rec.setLeg2(StartHandler);
                System.out.println(StartHandler.getCable());
            }

        } else if (rec != null && !EndHandler.getID().getIsForGridpane()) {
            System.out.println("pata 2 del led");
            if (EndHandler.getID().getIndexRow() == 1) {
                System.out.println("ID 1");
                rec.setLeg1(EndHandler);
                System.out.println(EndHandler.getCable());
            } else if (EndHandler.getID().getIndexRow() == 2) {
                System.out.println("ID 2");
                rec.setLeg2(EndHandler);
                System.out.println(EndHandler.getCable());
            }
        }
        //CASO BATERIA-GRIDPANEVOLT OR GRIDPANEVOLT-BATERIA
        if (Arrays.asList(edgeCases).contains(StartHandler.getID().getGridName()) && Arrays.asList(gridNames).contains(EndHandler.getID().getGridName())){ //PREGUNTA SI DE DONDE EMPIEZA ES UN GRIDPANEVOLT Y SI TERMINA ES LA BATERIA (GRIDPANEVOLT --> BATERIA)
            String startCircleGridnamevolt = StartHandler.getID().getGridName();
            if(startCircleGridnamevolt.equals(firsGridPaneVolt.getName())){ // PREGUNTA SI DE DONDE EMPIEZA ES EL PRIMER GRIDPANEVOLT (GRIDPANEVOLT-->BATERIA)
                Utils.paintCirclesVolt(firsGridPaneVolt.getGridPane(),ids[0],EndHandler.getState());
            }  else{ // PARA EL CASO DEL SEGUNDO GRIDPANEVOLT (GRIDPANEVOLT --> BATERIA)
                Utils.paintCirclesVolt(secondGridPaneVolt.getGridPane(),ids[0],EndHandler.getState());
            }
        }else if(Arrays.asList(edgeCases).contains(EndHandler.getID().getGridName()) && Arrays.asList(gridNames).contains(StartHandler.getID().getGridName())){ // PREGUNTA SI DE DONDE EMPIEZA ES UNA BATERIA Y SI TERMINA EN UN GRIDPANEVOLT (BATERIA --> GRIDPANEVOLT)
            String FinalCircleGridnamevolt = EndHandler.getID().getGridName();
            if(FinalCircleGridnamevolt.equals(firsGridPaneVolt.getName())){ // BATERIA --> PRIMER GRIDPANEVOLT
                 Utils.paintCirclesVolt(firsGridPaneVolt.getGridPane(),ids[1],StartHandler.getState());
            }else{ // BATERIA --> SEGUNDO GRIDPANEVOLT
                Utils.paintCirclesVolt(secondGridPaneVolt.getGridPane(),ids[1],StartHandler.getState());
            }
        }

        //si el circulo inicial no tiene energia, se toma la energia del circulo final.
        if ( ( !StartHandler.hasEnergy() && StartHandler.getID().getIsForGridpane() && EndHandler.getID().getIsForGridpane() ) || (EndHandler.getID().getGridName()).equals(gridNames[2]) ) { // PREGUNTA SI DONDE EMPIEZA Y DONDE TERMINA ES PARA UN GRIDPANE, ADEMAS DE PREGUNTAR SI DONDE TERMINA ES UNA BATERIA
            String startCircleGridName = StartHandler.getID().getGridName();
            if (startCircleGridName.equals(firstGridPane.getName())) {
                Utils.paintCircles(firstGridPane.getGridPane(), ids[0], EndHandler.getState());
            } else if(startCircleGridName.equals(secondGridPane.getName())) {
                Utils.paintCircles(secondGridPane.getGridPane(), ids[0], EndHandler.getState());
            } else if (startCircleGridName.equals(firsGridPaneVolt.getName())) {
                Utils.paintCirclesVolt(firsGridPaneVolt.getGridPane(),ids[0],EndHandler.getState());
            } else if (startCircleGridName.equals(secondGridPaneVolt.getName())) {
                Utils.paintCirclesVolt(secondGridPaneVolt.getGridPane(),ids[0],EndHandler.getState());
            }
            EndHandler.setisTaken(true);
            //StartHandler.setisTaken(true);
        } else {
        //recuperamos el nombre del gridName para ver en cual gridpane pintar
            if ( (StartHandler.getID().getIsForGridpane() && EndHandler.getID().getIsForGridpane()) || (StartHandler.getID().getGridName()).equals(gridNames[2]) ) { // Pregunta si el gridname es igual al gridname de la bateria
                String endCircleGridName = EndHandler.getID().getGridName();
                if (endCircleGridName.equals(firstGridPane.getName())) {
                    Utils.paintCircles(firstGridPane.getGridPane(), ids[1], CurrentLine.getTipodecarga());
                }else if(endCircleGridName.equals(secondGridPane.getName())){
                    Utils.paintCircles(secondGridPane.getGridPane(), ids[1], CurrentLine.getTipodecarga());
                } else if (endCircleGridName.equals(firsGridPaneVolt.getName())) {
                    Utils.paintCirclesVolt(firsGridPaneVolt.getGridPane(),ids[1],CurrentLine.getTipodecarga());
                } else if(endCircleGridName.equals(secondGridPaneVolt.getName())){
                    Utils.paintCirclesVolt(secondGridPaneVolt.getGridPane(),ids[1],CurrentLine.getTipodecarga());
                }
                EndHandler.setisTaken(true);
                //StartHandler.setisTaken(true);
            }
        }

        UpdateState(StartHandler, EndHandler, current);
        Utils.IdentifiedFunction(StartHandler, EndHandler, rec);
        System.out.println(StartHandler.getState());
        System.out.println(EndHandler.getState());

        //TODO revisar que hace.
        StartHandler = null;
        EndHandler = null;
    }

    //Este metodo lo que hace es actualizar el estado a los CustomCircle que no son para un GridPane
    public void UpdateState(CustomCircle StartHandler, CustomCircle EndHandler, Cable current){
        //Preguntamos si el StartHandler es para gridPane y si el EndHandler no es para GridPane
        if(StartHandler.getID().getIsForGridpane() && !(EndHandler.getID().getIsForGridpane())){
            //Actualizamos el estado del que no es para GridPane
            EndHandler.setState(StartHandler.getState());
            //Actualizamos que el StartHandler y el EndHandler estan tomados para que no se conecten mas de un cable en el mismo CustomCircle
            EndHandler.setisTaken(true);
            StartHandler.setisTaken(true);

            //Preguntamos si el EndHandler es para GridPane y el StartHandler no es para GridPane
        } else if(EndHandler.getID().getIsForGridpane() && !(StartHandler.getID().getIsForGridpane())){
            //Actualizamos el estado del que no es para GridPane
            StartHandler.setState(EndHandler.getState());
            //Actualizamos que el StartHandler y el EndHandler estan tomados para que no se conecten mas de un cable en el mismo CustomCircle
            EndHandler.setisTaken(true);
            StartHandler.setisTaken(true);
        }
    }

    public void SetStartHandler(CustomCircle startHandler){this.StartHandler = startHandler;}
    public void SetEndHandler(CustomCircle endHandler){this.EndHandler = endHandler;}
}