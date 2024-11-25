
package com.example.prototipo;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;

public class ClickLine {
    private Cable CurrentLine;
    private Resistencia resistencia;
    private CustomCircle StartHandler;
    private CustomCircle EndHandler;
    private GridPaneObserver gridPaneObserver;
    private ID[] ids;
    private AnchorPane root;
    private Bateria bateria;
    private Basurero basurero;
    private CustomShape rec;
    private ArrayList<CustomShape> shapes = new ArrayList<>();
    private boolean isCableFixed = false;
    //private ArrayList<Cable> cables;
    private boolean isResistenciaModeActive = false;

    public ClickLine(AnchorPane root, GridPaneObserver gridPaneObserver, Basurero basurero, Bateria bateria, ArrayList<Cable> cables, ArrayList<LED> leds, ArrayList<Switch> switches) {
        this.root = root;
        this.gridPaneObserver = gridPaneObserver;
        this.basurero = basurero;
        this.bateria = bateria;
        gridPaneObserver.setCables(cables);
        gridPaneObserver.setLeds(leds);
        gridPaneObserver.setSwitches(switches);
        ids = new ID[2];
        CurrentLine = new Cable();
        resistencia = new Resistencia(10);
    }

    public void CircleAsignator(){
        root.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            Node nodoclickeado = event.getPickResult().getIntersectedNode();
            if(nodoclickeado instanceof CustomCircle && !((CustomCircle) nodoclickeado).getIsTaken()){
                ids = new ID[2]; //se asegura de que el cable tome la referencia del objeto actual y no el anterior.
                CustomCircle Circulo = (CustomCircle) nodoclickeado;
                SetStartHandler(Circulo);
                StartHandler.setisTaken(true);
                CurrentLine.setTipodecarga(Circulo.getState()); //se pasa por el cable el tipo de carga que tiene el circulo
                ids[0] = new ID(Circulo.getID().getGeneratedID());

                LinePressed(root,event);
                isCableFixed = false;
            }
        });
        root.setOnMouseDragged(e -> {
            //se asegura que acabe en un circulo.
            if (!isCableFixed && e.getTarget() instanceof Circle) {
                DragLine(e.getX(), e.getY(),e );
            }
        });
        root.setOnMouseReleased(e -> {
            if (e.getTarget() instanceof CustomShape shape) {
                rec = shape;
                shapes.add(rec);
            }

            if(!(e.getTarget() instanceof CustomCircle)) return;
            RealizeLine(e);
            isCableFixed = true;
            GridPaneObserver.refreshCables(gridPaneObserver);
            if(!gridPaneObserver.getBurnedCables().isEmpty()){
                gridPaneObserver.getCables().removeAll(gridPaneObserver.getBurnedCables()); // TODO: REVISAR EL ARREGLO DE RESISTENCIAS PORQUE NO SE ESTAN ACTUALIZANDO
            }

            //al eliminar un cable, el paso de energía es defectuoso, por ello se llama esta función que se asegura de que esté bn.
            if (gridPaneObserver.getIsEnergyActivated()) {
                //se crea una copia de los cables antes de iterar.
                new ArrayList<>(gridPaneObserver.getCables()).forEach(n ->
                    GridPaneObserver.refreshProtoboard(gridPaneObserver)
                );
            }

            System.out.println("-----------------");
            gridPaneObserver.getCables().forEach(cable -> {
                System.out.println(cable);
            });
            System.out.println("------------------------------");

        });

        root.setOnMouseClicked(e -> {
            if (e.getTarget() instanceof CustomShape shape){
                shapes.add(shape);
            }
            boolean canDelete = basurero.getIsActive();
            if (!canDelete) return;


            if(e.getTarget() instanceof Cable cable) {
                //Buscamos el cable presionado para asi ver despues si el cable pertenece a un elemento del protoboard
                Cable cablefound = Utils.getCableByID(gridPaneObserver.getCables(), cable);

                if(cablefound == null) {  // Eliminar los cables que estan dentro de la columna quemada
                    Cable burnedCable = Utils.getCableByID(gridPaneObserver.getBurnedCables(), cable);
                    Utils.ResetStateCustomCircles(burnedCable);
                    if(burnedCable.getTipo() != null){
                        Resistencia resis = gridPaneObserver.getResistencias().stream()
                                .filter(el -> el.getRandomID().equals(burnedCable.getRandomID()))
                                .findAny()
                                .orElse(null);
                        root.getChildren().remove(resis.getRec());
                        root.getChildren().remove(resis.getArrow());
                        root.getChildren().remove(resis);
                    }
                    root.getChildren().remove(burnedCable);
                    gridPaneObserver.getBurnedCables().remove(burnedCable);
                    return;
                }
                //asignamos los CustomCircles del cable
                CustomCircle startHandler = cablefound.Getcircles()[0];
                CustomCircle endHandler = cablefound.Getcircles()[1];
                if(!startHandler.getID().getIsForGridpane() && !startHandler.getID().getGridName().equals("BateryVolt")){
                    startHandler.removeEnergy();
                }
                if (!endHandler.getID().getIsForGridpane() && !endHandler.getID().getGridName().equals("BateryVolt")){
                    endHandler.removeEnergy();
                }
                Utils.deleteCable(e,
                        gridPaneObserver,
                        bateria
                );
                String edgeCases[] = {
                        "switchvolt1",
                        "LedVolt1"
                };
                //al eliminar un cable, el paso de energía es defectuoso, por ello se llama esta función que se asegura de que esté bn.
                if (gridPaneObserver.getIsEnergyActivated()) {
                    new ArrayList<>(gridPaneObserver.getCables()).forEach(n ->
                        GridPaneObserver.refreshProtoboard(gridPaneObserver)
                    );
                }
                //Switch
                //Lo que hacemos es preguntar si el StartHandler o el EndHandler pertenecen a un Switch
                if(startHandler.getID().getGridName().equals(edgeCases[0]) || endHandler.getID().getGridName().equals(edgeCases[0])){
                    for (Switch switchs : gridPaneObserver.getSwitches()) {
                        CustomShape shape = getCustomShapebyUniqueID(shapes, switchs.getUniqueId());
                       // switchs.Function();
                    }
                    //LED
                    //Hacemos lo mismo que con el switch
                } else if(startHandler.getID().getGridName().equals(edgeCases[1]) || endHandler.getID().getGridName().equals(edgeCases[1])){
                    for (LED led : gridPaneObserver.getLeds()) {
                        led.ONorOFF();
                    }
                }

                gridPaneObserver.getCables().forEach(c -> {
                    System.out.println(c);
                });
                System.out.println("--------");
            }

        });
    }
    private void LinePressed(AnchorPane root,MouseEvent Event){
        if(Event.getPickResult().getIntersectedNode() instanceof CustomCircle) {  //Verifica que empiece solo de los circulos
            //CurrentLine.setLine(Event.getSceneX(), Event.getSceneY(), Event.getX(), Event.getY());
            int carga;
            carga = CurrentLine.getTipodecarga();
            //String previousID = CurrentLine.getRandomID();

            if (isResistenciaModeActive) {
                resistencia = new Resistencia(Event.getSceneX(), Event.getSceneY(), Event.getX(), Event.getY(), 10);
                resistencia.setTipodecarga(carga);
                resistencia.setStrokeWidth(5);
                resistencia.setRandomID();
                root.getChildren().add(resistencia);
            } else {
                CurrentLine = new Cable(Event.getSceneX(), Event.getSceneY(), Event.getX(), Event.getY());
                CurrentLine.setTipodecarga(carga);
                CurrentLine.setStrokeWidth(5);
                CurrentLine.setRandomID();
                root.getChildren().add(CurrentLine);
            }
        }
    }

    private void DragLine(double x, double y, MouseEvent event){
        if(CurrentLine != null && (event.getPickResult().getIntersectedNode() instanceof CustomCircle) /*&& !((CustomCircle) event.getTarget()).hasCable()*/ ) {
            CustomCircle UltimateCircle = (CustomCircle) event.getPickResult().getIntersectedNode();

            if(UltimateCircle.getIsTaken()){
                return;
            }
            ids[1] = new ID(UltimateCircle.getID().getGeneratedID());
            SetEndHandler(UltimateCircle);
            //se da valor del cable de nuevo, ya que el EndHandler se actualiza, y por ende pierdse el valor del cable.

            if(!(ID.isSameID(ids[0], ids[1]))){

                if (!isResistenciaModeActive) {
                    CurrentLine.setEndX(x);
                    CurrentLine.setEndY(y);
                } else {
                    resistencia.setEndX(x);
                    resistencia.setEndY(y);
                }
            }
        }
    }
    //TODO colocar setIsTaken en el deleteCable.
    private void RealizeLine(MouseEvent e) {



        String[] edgeCases = {
                "switchvolt1",
                "LedVolt1",
                "BateryVolt"
        };

        String[] gridNames = {
                "gridVolt1",
                "gridVolt2"
        };

        String[] gridTrails = {
                "gridTrail1",
                "gridTrail2"
        };

        if (EndHandler == null || StartHandler == null) {

            CustomCircle circle = (CustomCircle) e.getTarget();
            //si no esta tomado, se sale de la funcion, y por lo tanto no se elimina.
            if (!circle.getIsTaken()) return;
            if (StartHandler == null && circle.hasEnergy()) return;

            if( ! (( (CustomCircle) e.getTarget() ).hasCable()) && !isCableFixed){
                if(isResistenciaModeActive){
                    root.getChildren().remove(resistencia);
                }else{
                    root.getChildren().remove(CurrentLine);
                }
            }

            if (StartHandler != null){
                StartHandler.setisTaken(false);
            }
            return;
        }

        CurrentLine.setIds(ids);
        //si no se hace esto, los cables de la coleccion terminan siendo iguales al ultimo cable.
        Cable current = new Cable();

        if(!getResistenciaMode()){
            CurrentLine.setIds(ids);
            current.setIds(CurrentLine.getIds());
            current.setRandomID(CurrentLine.getRandomID());
        }else{
            resistencia.setIds(ids);
            current.setIds(resistencia.getIds());
            current.setRandomID(resistencia.getRandomID());
        }
        current.SetCircles(new CustomCircle[] {
                StartHandler,
                EndHandler
        });

        //si los 2 circulos en el cable no tienen
        if (current.getFirstCircle().getState() == 0 && current.getSecondCircle().getState() == 0) {
            current.removeTipodecarga();
        } else { //si uno de los circulos tiene energía, entonces no se le quita la energía.
            if(!getResistenciaMode()){
            current.setTipodecarga(CurrentLine.getTipodecarga());
            }else{
                current.setTipodecarga(resistencia.getTipodecarga());
            }
        }

        StartHandler.setCable(current);
        EndHandler.setCable(current);
        gridPaneObserver.addCable(current);

        UpdateState(StartHandler, EndHandler, current);
        //Switch
        //Lo que hacemos es preguntar si el StartHandler o el EndHandler pertenecen a un Switch
        if(StartHandler.getID().getGridName().equals(edgeCases[0]) || EndHandler.getID().getGridName().equals(edgeCases[0])){
            for (Switch switchs : gridPaneObserver.getSwitches()) {
                CustomShape shape = getCustomShapebyUniqueID(shapes, switchs.getUniqueId());
                //switchs.Function();
            }
            //LED
        } else if(StartHandler.getID().getGridName().equals(edgeCases[1]) || EndHandler.getID().getGridName().equals(edgeCases[1])){
            for (LED led : gridPaneObserver.getLeds()) {
                led.ONorOFF();
            }
        }

        if (isResistenciaModeActive) {
            resistencia.createRectangle();
            gridPaneObserver.addResistencia(resistencia);
            resistencia.SetCircles(new CustomCircle[] {
                StartHandler,
                EndHandler
            });
            current.setTipo("resistencia");
        }

        EndHandler.setisTaken(true);
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

    public CustomShape getCustomShapebyCable( ArrayList<CustomShape> customShape, Cable cable) {
        for (CustomShape shape : customShape) {
            if(shape.getLeg2().hasCable()){
                if(shape.getLeg2().getCable().getRandomID().equals(cable.getRandomID())){
                    return shape;
                }
            } else if(shape.getLeg1().hasCable()){
                if(shape.getLeg1().getCable().getRandomID().equals(cable.getRandomID())){
                    return shape;
                }
            }
        }
        return null;
    }
    public CustomShape getCustomShapebyUniqueID(ArrayList<CustomShape> shapes, String UniqueId){
        for (CustomShape shape : shapes) {
            if(shape.getUniqueID().equals(UniqueId)){
                return shape;
            }
        }
        return null;
    }

    public void setisResistenciaModeActive(boolean resistenciaModeActive) {
        this.isResistenciaModeActive = resistenciaModeActive;
    }

    public boolean getResistenciaMode(){
        return isResistenciaModeActive;
    }
}