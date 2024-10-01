package com.example.prototipo;

import java.util.ArrayList;

public class GridPaneObserver {
    private GridPaneTrailController firstGridPane;
    private GridPaneTrailController secondGridPane;
    private GridPaneController firstGridPaneVolt;
    private GridPaneController secondGridPaneVolt;
    private ArrayList<Cable> cables = new ArrayList<>();
    private ArrayList<LED> leds = new ArrayList<>();
    private ArrayList<Switch> switches = new ArrayList<>();
    //guarda las columnas con energía y la energía que tienen.
    private ArrayList<Pair<Integer, ArrayList<CustomCircle>>> energizedColumns = new ArrayList<>();
    private boolean isEnergyActivated = true; //al principio, el protoboard no está apagado.

    public GridPaneObserver(GridPaneTrailController firstGridPane, GridPaneTrailController secondGridPane, GridPaneController firstGridPaneVolt, GridPaneController secondGridPaneVolt) {
        this.firstGridPane = firstGridPane;
        this.secondGridPane = secondGridPane;
        this.firstGridPaneVolt = firstGridPaneVolt;
        this.secondGridPaneVolt = secondGridPaneVolt;
    }

    //Setters, adds y removes de elementos del Protoboard
    public void setCables(ArrayList<Cable> cables) {
        this.cables = cables;
    }

    public void addCable(Cable cable) {
        cables.add(cable);
    }

    public void removeCable(Cable cable) {
        cables.remove(cable);
    }
    //TODO ver donde ejecutar este método. este método agrega una columna con su respectiva energía.
    public void addColumn(ArrayList<CustomCircle> column, Integer energy) {
        energizedColumns.add(new Pair<>(energy, column));
    }

    public static void addColumn(GridPaneObserver gridPaneObserver, ArrayList<CustomCircle> column, Integer energy) {
        gridPaneObserver.addColumn(column, energy);
    }

    public void removeColumn(ArrayList<CustomCircle> column) {
        //se elimina la columna que tenga la misma id que la columna que se pasa por parametro.
        energizedColumns.removeIf(el -> {
            System.out.println("EL: "+ el);
            ID elementID = el.getSecondValue().get(0).getID(); //
            ID columnID = column.get(0).getID(); //se obtiene la id de la columna a partir

            return elementID.equals(columnID);
        });
    }

    //quita temporalmente la energía (desactiva la energía) //TODO REVISAR.
    public void deactivateGridObserver() {
        //remueve la energía que tiene el gridPane
        energizedColumns.forEach(pair -> {
            ArrayList<CustomCircle> col = pair.getSecondValue();

            col.forEach(CustomCircle::removeEnergy);
        });
        leds.forEach(led -> LED.updateState(led, false)); //apaga todos los leds del protoboard
    }

    //activa la energía TODO: refactorizar este chancherío.
    public void activateGridObserver() {
        cables.forEach(cable -> GridPaneObserver.refreshProtoboard(this));
        leds.forEach(led -> LED.updateState(led, true)); //enciende todos los leds del protoboard.
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
            ArrayList<CustomCircle> firstCircles = Utils.getColumnOfCustomCircles(gridPane, firstCol.getID());
            ArrayList<CustomCircle> secondCircles = Utils.getColumnOfCustomCircles(gridPane, secondCol.getID());

            //como los circulos conectados al cable no se actualiza bn su estado, se actualiza manualmente a travez de
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
            //en caso de tener una columna sin energía conectada a otra CON ENERGÍA, esta se registra en el
            //registro de pares <Energía, Columna> (esto es lo que ocurre en los 2 condicionales)
            if (firstCol.getState() != 0 && secondCol.getState() == 0) {
                ArrayList<CustomCircle> circles = Utils.getColumnOfCustomCircles(gridPane, secondCol.getID());
                //Utils.paintCirclesCollection(this, circles, firstCol.getState());
                GridPaneObserver.addColumn(gridPane, circles, firstCol.getState());
            }

            if (secondCol.getState() != 0 && firstCol.getState() == 0) {
                ArrayList<CustomCircle> circles = Utils.getColumnOfCustomCircles(gridPane, firstCol.getID());
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
        RefreshElements(gridPane.getSwitches(), gridPane.getLeds(), gridPane.getCables());
    }

    //Este metodo lo que hace es actualizar todos los elementos del protoboard(Switch y LED) cuando al momento de Encender y apagar se llamen su funcionalidad correspondiente
    public static void RefreshElements(ArrayList<Switch> switches, ArrayList<LED> leds, ArrayList<Cable> cables){
        //Actualizamos todos los switchs
        for (Switch aSwitch : switches) {
            aSwitch.ChargePass(cables);
        }
        //Actualizamos todos los LEDs
        for (LED led : leds) {
            led.ONorOFF();
        }
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
}