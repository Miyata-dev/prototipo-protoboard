package com.example.prototipo;

import javafx.scene.layout.AnchorPane;
import java.util.ArrayList;
import java.util.function.Predicate;

public class GridPaneObserver {
    private GridPaneTrailController firstGridPane;
    private GridPaneTrailController secondGridPane;
    private GridPaneController firstGridPaneVolt;
    private GridPaneController secondGridPaneVolt;
    private ArrayList<Cable> cables = new ArrayList<>();
    private ArrayList<Resistencia> resistencias = new ArrayList<>();
    private ArrayList<LED> leds = new ArrayList<>();
    private ArrayList<Switch> switches = new ArrayList<>();
    private ArrayList<ChipAND> chipsAND = new ArrayList<>();
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
//        System.out.println("adding cable " + cable.getRandomID());
        if (!cables.contains(cable)) {
            cables.add(cable);
        }
    }
    //antes de agregarlos, mira que no estén agregados a la colección.
    public void addResistencia(Resistencia resistencia) {
        if (!cables.contains(resistencia)) {
            cables.add(resistencia);
        }

        if (!resistencias.contains(resistencia)) {
            resistencias.add(resistencia);
        }
    }
    public void removeResistencia(Resistencia resistencia) {
        resistencias.remove(resistencia);
        cables.removeIf(cable -> cable.getRandomID().equals(resistencia.getRandomID()));
    }

    public void removeCable(Cable cable) {
        cables.remove(cable);
    }

    public void removeChipAND(ChipAND chipAND) {
        chipsAND.remove(chipAND);
    }

    public void addChipAND(ChipAND chip) {
        chipsAND.add(chip);
    }

    //TODO ver donde ejecutar este método. este método agrega una columna con su respectiva energía.
    public void addColumn(ArrayList<CustomCircle> column, Integer energy) {
        //si la columna está vacía, no se agrega a la colección
        if (column.isEmpty()) return;
//        System.out.println("nro de elementos: " + column.size());
        System.out.println("adding column: " + column.get(0).getID());
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

        energizedColumns.removeIf(el -> {
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
        refreshProtoboard(this);
    }

    public static void refreshProtoboard(GridPaneObserver gridPane) {
        //se energiza los pares que están registrados.
        gridPane.getEnergizedColumns().forEach(pair -> {
            Integer energy = pair.getFirstValue();
            ArrayList<CustomCircle> col = pair.getSecondValue();
            col.forEach(cir -> cir.setState(energy));
        });

        if(!bateria.getPositivePole().hasCable()) {
            cleanCircles(gridPane,1);
        }if(!bateria.getNegativePole().hasCable()) {
            cleanCircles(gridPane,-1);
        }
        gridPane.getCables().forEach(cable -> {
//            System.out.println("in refresh" + " cable: " + cable.getRandomID() + " tipo: " + cable.getTipo());
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

            if(firstCol.getIsBurned()){
                burntEnergyCleaner(gridPane,firstCol);
            }if(secondCol.getIsBurned()){
                burntEnergyCleaner(gridPane,secondCol);
            }

            if (firstCol.getState() == 0 && secondCol.getState() == 0) {
                cable.removeTipodecarga();
            }
//            System.out.println("first cir state: " + firstCol.getState() + " second cir: " + secondCol.getState());
            //en caso de tener una columna sin energía conectada a otra CON ENERGÍA, esta se registra en el
            //registro de pares <Energía, Columna> (esto es lo que ocurre en los 2 condicionales)
            if (firstCol.getState() != 0 && secondCol.getState() == 0) {
                ArrayList<CustomCircle> circles = getCircles(gridPane, secondCol.getID());
                //Utils.paintCirclesCollection(this, circles, firstCol.getState());
                GridPaneObserver.addColumn(gridPane, circles, firstCol.getState());
            }
            if (secondCol.getState() != 0 && firstCol.getState() == 0) {

//                System.out.println("tipo: " + cable.getTipo());

                if (cable.getTipo() == null) {
                    ArrayList<CustomCircle> circles = getCircles(gridPane, firstCol.getID());
                    //Utils.paintCirclesCollection(this, circles, secondCol.getState());
                    GridPaneObserver.addColumn(gridPane, circles, secondCol.getState());
                } else {
                    //busca una resistencia que tenga la misma id random que el cable que estamos mirando en la iteración.
                    Resistencia founded = gridPane.getResistencias().stream()
                        .filter(el -> el.getRandomID().equals(cable.getRandomID()))
                        .findAny()
                        .orElse(null);

                    founded.setBurned();

                }
            }
        });
        //se vuelve a recorrer la colecciónb de pares para devolverle la energía a la columna que se agregó anteriormente.
        gridPane.getEnergizedColumns().forEach(pair -> {
            Integer energy = pair.getFirstValue();
            ArrayList<CustomCircle> col = pair.getSecondValue();
            col.forEach(cir -> cir.setState(energy));
        });
        //Actualizamos todos los elementos del GridPaneObserver despues de pintar todos los circulos.
        RefreshElements(gridPane);
        refreshCables(gridPane);
        refreshEnergizedColumns(gridPane); //con esto aquí no deja que las columnas de los chips se pinten.
    }
    public static void cleanCircles(GridPaneObserver gridPane, int polo) {
//        System.out.println("cleanCircles || state: " + polo);
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

    public static void burntEnergyCleaner(GridPaneObserver gridPaneObserver,CustomCircle circle) {
        Cable unburnedCables = null;
        ArrayList<CustomCircle> burnedCircle = getCircles(gridPaneObserver,circle.getID());
        for (CustomCircle circulo : burnedCircle) {
            if(circulo.hasCable() && !circulo.getCable().getIsBurned()){
                unburnedCables = circulo.getCable();
            }
        }
        if(unburnedCables != null ){
            ArrayList<Cable> connectedCables = Utils.getConnectedCables(gridPaneObserver.getCables(), unburnedCables, true);
            for (Cable cable : connectedCables) {
                if(!cable.getIsBurned()){
                    Utils.unPaintCircles(gridPaneObserver, cable.getSecondCircle());
                    Utils.unPaintCircles(gridPaneObserver, cable.getFirstCircle());
                }

            }
        }
//        System.out.println("Cables que estan en la columna quemada: "+unburnedCables.size());

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
    public static void RefreshElements(GridPaneObserver gridPaneObserver){
        //Actualizamos todos los switchs
        for (Switch aSwitch : gridPaneObserver.getSwitches()) {
            aSwitch.Function();
        }
        //Actualizamos todos los LEDs
        for (LED led : gridPaneObserver.getLeds()) {
            led.ONorOFF();
        }

        ArrayList<ChipAND> chips = gridPaneObserver.getChipsAND();
        System.out.println("number of chips: " + chips.size());
        //ESTO DE AQUI NO FUNCIONA POR EL REFRESHENERGYZEDCOLUMNS, se debe mejorar esa lógica.
        for (ChipAND c : chips) {
            System.out.println("checking columns in refresh...");
            c.checkColumns();
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
                    cable.setBurned(); // TOMAR ESTE ESTADO Y SACAR LOS GETCONNECTEDCABLES DE LOS CIRCULOS DE LA COLUMNA QUEMADA
                    circleToBurn.forEach(CustomCircle::setBurned);

                    return;
                }
            }
        }
    }
    //TODO mejorar la lógica ya que las columnas de los chips se les quita la energpia ya que no tiene cables
    //y se registran como energizadas en el ChipAND.
    public static void refreshEnergizedColumns(GridPaneObserver gridPaneObserver) {
        ArrayList<Pair<Integer, ArrayList<CustomCircle>>> energized = gridPaneObserver.getEnergizedColumns();
        //mira si una columna tiene cable.
        Predicate<ArrayList<CustomCircle>> hasCable = column -> column.stream().anyMatch(CustomCircle::hasCable);
        //mira las columnas energizadas, si hay una energizada sin cable, se borra.
        for (int i = 0; i < energized.size(); i++) {
            ArrayList<CustomCircle> col = energized.get(i).getSecondValue();

            //si la columna no tiene cable, se saca de las columnas energizadas.
            if (!hasCable.test(energized.get(i).getSecondValue()) && !energized.get(i).getSecondValue().get(0).getIsAffectedByChip()) {
                Utils.unPaintCircles(gridPaneObserver, energized.get(i).getSecondValue().get(0));
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

    public ArrayList<ChipAND> getChipsAND() {
        return chipsAND;
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