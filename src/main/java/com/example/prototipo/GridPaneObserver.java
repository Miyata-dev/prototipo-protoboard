package com.example.prototipo;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import jdk.jshell.execution.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class GridPaneObserver {
    private GridPaneTrailController firstGridPane;
    private GridPaneTrailController secondGridPane;
    private GridPaneController firstGridPaneVolt;
    private GridPaneController secondGridPaneVolt;
    private ArrayList<Cable> cables = new ArrayList<>();
    private ArrayList<Resistencia> resistencias = new ArrayList<>();
    private ArrayList<LED> leds = new ArrayList<>();
    private ArrayList<Switch> switches = new ArrayList<>();
    //guarda las columnas con energía y la energía que tienen.
    private ArrayList<Pair<Integer, ArrayList<CustomCircle>>> energizedColumns = new ArrayList<>();
    //aqui se guardan las columnas quemadas.
    private ArrayList<ArrayList<CustomCircle>> burnedCircles = new ArrayList<>();
    private boolean isEnergyActivated = true; //al principio, el protoboard no está apagado.
    private ArrayList<CustomCircle> CirclesCollection = new ArrayList<>();
    private AnchorPane root;
    private static Bateria bateria;

    private String[] elementIDsPrefix = {
            "LedVolt",
            "switchvolt",
            "BateryVolt"
    };
    private String[] gridNamePrefixes = {
            "gridTrail",
            "gridVolt"
    };

    public GridPaneObserver(GridPaneTrailController firstGridPane, GridPaneTrailController secondGridPane, GridPaneController firstGridPaneVolt, GridPaneController secondGridPaneVolt,Bateria bateria, AnchorPane root) {
        this.firstGridPane = firstGridPane;
        this.secondGridPane = secondGridPane;
        this.firstGridPaneVolt = firstGridPaneVolt;
        this.secondGridPaneVolt = secondGridPaneVolt;
        this.root = root;
        this.bateria = bateria;
        this.CirclesCollection = createCirclesCollection(this);
    }

    //Setters, adds y removes de elementos del Protoboard
    public void setCables(ArrayList<Cable> cables) {
        this.cables = cables;
    }

    public void addCable(Cable cable) {
        cables.add(cable);
    }

    public void addResisencia(Resistencia resistencia) {
        resistencias.add(resistencia);
    }

    public void removeResisencia(Resistencia resistencia) {
        resistencias.remove(resistencia);
    }

    public void removeCable(Cable cable) {
        cables.remove(cable);
    }
    //TODO ver donde ejecutar este método. este método agrega una columna con su respectiva energía.
    public void addColumn(ArrayList<CustomCircle> column, Integer energy) {
        //si la columna está vacía, no se agrega a la colección
        if (column.isEmpty()) return;
        System.out.println("nro de elementos: " + column.size());
        energizedColumns.add(new Pair<>(energy, column));
    }

    public static void addColumn(GridPaneObserver gridPaneObserver, ArrayList<CustomCircle> column, Integer energy) {
        if (column.isEmpty()) return;

        //solo se agrega la columna si no está quemada.
        if (!column.get(0).getIsBurned()) {
            gridPaneObserver.addColumn(column, energy);
        }

        //gridPaneObserver.addColumn(column, energy);
    }

    public void removeColumn(ArrayList<CustomCircle> column) {
        //se elimina la columna que tenga la misma id que la columna que se pasa por parametro.
//        energizedColumns.forEach(el -> {
//            System.out.println("second value: " + el.getSecondValue());
//        });

        energizedColumns.removeIf(el -> {
//            System.out.println("EL: "+ el.getSecondValue());
            ID elementID = el.getSecondValue().get(0).getID(); //
            ID columnID = column.get(0).getID(); //se obtiene la id de la columna a partir

            return elementID.equals(columnID);
        });
    }

    public void addBurnedColumn(ArrayList<CustomCircle> column) {
        burnedCircles.add(column);
    }

    //quita temporalmente la energía (desactiva la energía) //TODO REVISAR.
    public void deactivateGridObserver() {
        //remueve la energía que tiene el gridPane
        energizedColumns.forEach(pair -> {
            ArrayList<CustomCircle> col = pair.getSecondValue();
            col.forEach(CustomCircle::removeEnergy);
        });

        leds.forEach(led -> LED.UpdatingState(led, false)); //apaga todos los leds del protoboard
    }

    //activa la energía TODO: refactorizar este chancherío.
    public void activateGridObserver() {
        cables.forEach(cable -> GridPaneObserver.refreshProtoboard(this));
        leds.forEach(led -> LED.UpdatingState(led, true)); //enciende todos los leds del protoboard.
    }

    public static void refreshProtoboard(GridPaneObserver gridPane) {
        //se energiza los pares que están registrados.
        gridPane.getEnergizedColumns().forEach(pair -> {
            Integer energy = pair.getFirstValue();
            ArrayList<CustomCircle> col = pair.getSecondValue();
            col.forEach(cir -> cir.setState(energy));
        });

        gridPane.getCables().forEach(cable -> {
            //se obtienen los circulos que están conectados al cable.
            CustomCircle firstCol = cable.getFirstCircle();
            CustomCircle secondCol = cable.getSecondCircle();
            //se obtiene las columnas de los circulos que tiene el cables
            ArrayList<CustomCircle> firstCircles = getCircles(gridPane, firstCol.getID());
            ArrayList<CustomCircle> secondCircles = getCircles(gridPane, secondCol.getID());
            //como los circulos conectados al cable no se actualiza bn su estado, se actualiza manualmente a traves de
            //las columnas del gridpane.
            firstCircles.forEach(el -> {
                if (el.equals(firstCol)) {
                    firstCol.setState(el.getState());
                }
            });
            secondCircles.forEach(el -> {
                if (el.equals(secondCol)) {
                    secondCol.setState(el.getState());
                }
            });
            if(!bateria.getPositivePole().hasCable()) {
                cleanCircles(gridPane,1);
            }if(!bateria.getNegativePole().hasCable()) {
                cleanCircles(gridPane,-1);
            }

            //en caso de tener una columna sin energía conectada a otra CON ENERGÍA, esta se registra en el
            //registro de pares <Energía, Columna> (esto es lo que ocurre en los 2 condicionales)
            if (firstCol.getState() != 0 && secondCol.getState() == 0) {
                ArrayList<CustomCircle> circles = getCircles(gridPane, secondCol.getID());
                //Utils.paintCirclesCollection(this, circles, firstCol.getState());
                GridPaneObserver.addColumn(gridPane, circles, firstCol.getState());
            }
            if (secondCol.getState() != 0 && firstCol.getState() == 0) {
                ArrayList<CustomCircle> circles = getCircles(gridPane, firstCol.getID());
                //Utils.paintCirclesCollection(this, circles, secondCol.getState());
                GridPaneObserver.addColumn(gridPane, circles, secondCol.getState());
            }
        });
        //se vuelve a recorrer la colecciónb de pares para devolverle la energía a la columna que se agregó anteriormente.
        gridPane.getEnergizedColumns().forEach(pair -> {
            Integer energy = pair.getFirstValue();
            ArrayList<CustomCircle> col = pair.getSecondValue();
            col.forEach(cir -> cir.setState(energy));
        });
        //Actualizamos todos los elementos del GridPaneObserver despues de pintar todos los circulos.
        RefreshElements(gridPane.getSwitches(), gridPane.getLeds(), gridPane.getCables());
        refreshCables(gridPane);
    }
    public static void cleanCircles(GridPaneObserver gridPane, int polo) {
        ArrayList<Cable> cables = gridPane.getCables();
        for (Cable cable : cables) {
            CustomCircle firstC = cable.getFirstCircle();
            CustomCircle secondC = cable.getSecondCircle();
            if(cable.getTipodecarga() == polo) {
                Utils.unPaintCircles(gridPane,firstC);
                Utils.unPaintCircles(gridPane,secondC);
                cable.removeTipodecarga();
            }
        }
    }

    public static ArrayList<CustomCircle> getCircles(GridPaneObserver gridPaneObserver, ID id){
        ArrayList<CustomCircle> circles = new ArrayList<>();
        if(id.getGridName().contains(gridPaneObserver.getGridVoltPrefix())){
            circles = Utils.getRowOfCustomCircles(gridPaneObserver, id);
            return circles;
        }else if(id.getGridName().contains(gridPaneObserver.getGridTrailPrefix())){
            circles = Utils.getColumnOfCustomCircles(gridPaneObserver, id);
            return circles;
        }
        return circles;
    }

    //Este metodo lo que hace es actualizar todos los elementos del protoboard(Switch y LED) cuando al momento de Encender y apagar se llamen su funcionalidad correspondiente
    public static void RefreshElements(ArrayList<Switch> switches, ArrayList<LED> leds, ArrayList<Cable> cables){
        //Actualizamos todos los switchs
        for (Switch aSwitch : switches) {
            aSwitch.Function();
        }
        //Actualizamos todos los LEDs
        for (LED led : leds) {
            led.ONorOFF();
        }
    }//Actualizamos todos los switchs

    //Este metodo lo que hace es refrescar todos los tipo de carga
    public static void refreshCables(GridPaneObserver gridPaneObserver){

        for(Cable cable: gridPaneObserver.getCables()){
            if(cable.getFirstCircle().getState() == cable.getSecondCircle().getState()){
                cable.setTipodecarga(cable.getFirstCircle().getState());
            } else {
                //quema arbitrariamente la segunda.
                if (cable.getFirstCircle().hasEnergy() && cable.getSecondCircle().hasEnergy()) {
                    ArrayList<CustomCircle> circleToBurn = GridPaneObserver.getCircles(gridPaneObserver, cable.getSecondCircle().getID());

                    circleToBurn.forEach(CustomCircle::setBurned);

                    System.out.println("quemando columna...");
                    return;
                }
            }
        }
    }

    public ArrayList<CustomCircle> createCirclesCollection(GridPaneObserver gridPaneObserver){
        ArrayList<CustomCircle> CircleCollection = new ArrayList<>();

        CircleCollection.addAll(firstGridPane.getCircles());
        CircleCollection.addAll(secondGridPane.getCircles());

        return CircleCollection;
    }

    public void setRoot(AnchorPane root){
        this.root = root;
    }

    public void setCirclesCollection(ArrayList<CustomCircle> circlesCollection){
        this.CirclesCollection = circlesCollection;
    }

    public void setIsEnergyActivated(boolean isEnergyActivated) {
        this.isEnergyActivated = isEnergyActivated;
    }

    public void toggleObserver() {
        this.isEnergyActivated = !isEnergyActivated;
    }

    public void setLeds(ArrayList<LED> leds) {
        this.leds = leds;
    }

    public void addLeds(LED led) {
        leds.add(led);
    }

    public void removeLeds(LED led) {
        leds.remove(led);
    }

    public void setSwitches(ArrayList<Switch> switches) {
        this.switches = switches;
    }

    public void addSwitches(Switch switchs){
        switches.add(switchs);
    }

    public void removeSwitches(Switch switchs){
        switches.remove(switchs);
    }

    //getters
    public GridPaneController[] getGridPaneTrails() {
        return new GridPaneController[] {
                firstGridPaneVolt,
                secondGridPaneVolt
        };
    }

    public GridPaneTrailController[] getFirstGridPaneTrails() {
        return new GridPaneTrailController[] {
                firstGridPane,
                secondGridPane
        };
    }

    public GridPaneTrailController getFirstGridPaneTrail() {
        return firstGridPane;
    }

    public GridPaneTrailController getSecondGridPaneTrail() {
        return secondGridPane;
    }

    public GridPaneController getFirsGridPaneVolt() {
        return firstGridPaneVolt;
    }

    public GridPaneController getSecondGridPaneVolt() {
        return secondGridPaneVolt;
    }

    public ArrayList<Cable> getCables() {
        return cables;
    }

    public ArrayList<Resistencia> getResistencias() {
        return resistencias;
    }

    public ArrayList<LED> getLeds(){
        return leds;
    }

    public ArrayList<Switch> getSwitches(){
        return switches;
    }

    public ArrayList<Pair<Integer, ArrayList<CustomCircle>>> getEnergizedColumns() {
        return energizedColumns;
    }

    public boolean getIsEnergyActivated() {
        return isEnergyActivated;
    }

    public String[] getElementIDsPrefix() {
        return elementIDsPrefix;
    }

    public String getLedIdPrefix() {
        return elementIDsPrefix[0];
    }

    public String getSwitchIdPrefix() {
        return elementIDsPrefix[1];
    }
    public String getBateryIdPrefix() {
        return elementIDsPrefix[2];
    }
    public Bateria getBatery(){
        return bateria;
    }
    public String getGridTrailPrefix() {
        return gridNamePrefixes[0];
    }

    public String getGridVoltPrefix() {
        return gridNamePrefixes[1];
    }

    public AnchorPane getRoot(){
        return this.root;
    }

    public ArrayList<CustomCircle> getCirclesCollection(){
        return this.CirclesCollection;
    }
}