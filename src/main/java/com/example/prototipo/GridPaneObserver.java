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
    private ArrayList<Cable> burnedCables = new ArrayList<>();
    private ArrayList<Resistencia> burnedResistencias = new ArrayList<>();
    private ArrayList<Resistencia> resistencias = new ArrayList<>();
    private ArrayList<LED> leds = new ArrayList<>();
    private ArrayList<Switch> switches = new ArrayList<>();
    private ArrayList<ChipAND> chipsAND = new ArrayList<>();
    private ArrayList<ChipOR> chipsOR = new ArrayList<>();
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

    //Metodos...

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
        refreshCables(gridPane);
        //se energiza los pares que están registrados.
        gridPane.getEnergizedColumns().forEach(pair -> {
            Integer energy = pair.getFirstValue();
            ArrayList<CustomCircle> col = pair.getSecondValue();
            col.forEach(cir -> cir.setState(energy));
        });
        if(bateria.getPositivePole().hasCable()){
            simplifiedRefresh(gridPane,bateria.getPositivePole());
            freeEnergy(gridPane,bateria.getPositivePole());
        }if(bateria.getNegativePole().hasCable()){
            simplifiedRefresh(gridPane,bateria.getNegativePole());
            freeEnergy(gridPane,bateria.getNegativePole());
        }

        for(Cable c: gridPane.getCables()){
            if(c.getFirstCircle().getState() == 0 && c.getSecondCircle().getState() == 0){
                c.removeTipodecarga();
            }
        }



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
        checkEnergyzedColumns(gridPane);
    }

    public static void simplifiedRefresh(GridPaneObserver gridPane, CustomCircle pole) {
        ArrayList<Cable> poleCables = Utils.getConnectedCables(gridPane.getCables(),pole.getCable(),gridPane);
        for(Cable cable : poleCables){
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
            if (firstCol.getState() == 0 && secondCol.getState() == 0) {
                cable.removeTipodecarga();
            }
            //en caso de tener una columna sin energía conectada a otra CON ENERGÍA, esta se registra en el
            //registro de pares <Energía, Columna> (esto es lo que ocurre en los 2 condicionales)
            if (firstCol.getState() != 0 && secondCol.getState() == 0) {
                ArrayList<CustomCircle> circles = getCircles(gridPane, secondCol.getID());
                //Utils.paintCirclesCollection(this, circles, firstCol.getState());
                GridPaneObserver.addColumn(gridPane, circles, firstCol.getState());
            }
            if (secondCol.getState() != 0 && firstCol.getState() == 0) {

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
                    if(founded != null){
                        founded.setBurned();
                    }

                }
            }
        }
    }

    public static void burntEnergyCleaner(GridPaneObserver gridPane,ArrayList<CustomCircle> burnedCircles) {  // Este metodo busca los cables que estan en la columna quemada, para luego eliminarlos de la coleccion
        for (CustomCircle circulo : burnedCircles) {
            if(circulo.hasCable()){
                gridPane.getBurnedCables().add(circulo.getCable());
            }
        }
    }

    public static void freeEnergy(GridPaneObserver gridPane,CustomCircle pole) {
        ArrayList<Cable> ConnectWithBatery = Utils.getConnectedCables(gridPane.getCables(),pole.getCable(),gridPane);
        ArrayList<Cable> notConnectedWithBatery = new ArrayList<>(gridPane.getCables());
        notConnectedWithBatery.removeAll(ConnectWithBatery);
        for (Cable cable: notConnectedWithBatery) {
            if(cable.getTipodecarga() == pole.getState()){
                Utils.unPaintCircles(gridPane, cable.getSecondCircle());
                Utils.unPaintCircles(gridPane, cable.getFirstCircle());
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
        ArrayList<ChipOR> chipsOr = gridPaneObserver.getChipsOR();
//        System.out.println("number of chips: " + chips.size());
        //ESTO DE AQUI NO FUNCIONA POR EL REFRESHENERGYZEDCOLUMNS, se debe mejorar esa lógica.
        for (ChipAND c : chips) {
//            System.out.println("checking columns in refresh...");
            c.checkColumns();
        }

        for (ChipOR c : chipsOr) {
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
                    if(cable.getTipo() == null){
                        ArrayList<CustomCircle> circleToBurn = GridPaneObserver.getCircles(gridPaneObserver, cable.getSecondCircle().getID());
                        cable.setBurned();
                        circleToBurn.forEach(CustomCircle::setBurned);
                        burntEnergyCleaner(gridPaneObserver, circleToBurn);
                    }else{
                        Resistencia founded = gridPaneObserver.getResistencias().stream()
                                .filter(el -> el.getRandomID().equals(cable.getRandomID()))
                                .findAny()
                                .orElse(null);

                        founded.setBurned();

                    }
                    return;
                }
            }
        }
    }


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

    public void toggleObserver() {
        this.isEnergyActivated = !isEnergyActivated;
    }


    //Este metodo lo que hace es verificar si las Columnas realmente deberian o no tener energia
    public static void checkEnergyzedColumns(GridPaneObserver gridPaneObserver){
        //Creamos la colleccion de cables que estan conectados.
        ArrayList<Cable> cablesPositive = new ArrayList<>();
        ArrayList<Cable> cablesNegative = new ArrayList<>();
        ArrayList<Cable> cablesConnected =  new ArrayList<>();

        //Preguntamos si el polo negativo de la bateria tiene cable y si es asi los agregamos a la coleccion de los cables conectados
        if(gridPaneObserver.getBatery().getNegativePole().hasCable()){
            cablesNegative = Utils.getConnectedCables(gridPaneObserver.getCables(), gridPaneObserver.getBatery().getNegativePole().getCable(), gridPaneObserver);
            cablesConnected.addAll(cablesNegative);
        }
        if(gridPaneObserver.getBatery().getPositivePole().hasCable()){
            cablesPositive = Utils.getConnectedCables(gridPaneObserver.getCables(), gridPaneObserver.getBatery().getPositivePole().getCable(), gridPaneObserver);
            cablesConnected.addAll(cablesPositive);
        }

        for (Cable cable : gridPaneObserver.getCables()) {
            if(!cablesConnected.contains(cable)){
                ArrayList<CustomCircle> circle1 = GridPaneObserver.getCircles(gridPaneObserver, cable.getFirstCircle().getID());
                ArrayList<CustomCircle> circle2 = GridPaneObserver.getCircles(gridPaneObserver, cable.getSecondCircle().getID());

                circle1.forEach(circle->{
                    circle.removeEnergy();
                });
                circle2.forEach(circle->{
                    circle.removeEnergy();
                });
                gridPaneObserver.removeColumn(circle1);
                gridPaneObserver.removeColumn(circle2);
            }
        }
    }
    //Setters, adds y removes de elementos del Protoboard

    //Cable
    public void setCables(ArrayList<Cable> cables) {
        this.cables = cables;
    }

    public void addCable(Cable cable) {
        if (!cables.contains(cable)) {
            cables.add(cable);
        }
    }
    public void removeCable(Cable cable) {
        cables.remove(cable);
    }

    //Resistencias
    public void addResistencia(Resistencia resistencia) {
        //antes de agregarlos, mira que no estén agregados a la colección.
        if (Cable.getCableFromCollection(cables, resistencia) == null) {
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

    public void addChipOR(ChipOR chipor) {
        chipsOR.add(chipor);
    }

    //ChipAND
    public void addChipAND(ChipAND chip) {
        chipsAND.add(chip);
    }

    public void removeChipAND(ChipAND chipAND) {
        chipsAND.remove(chipAND);
    }

    //Column
    public void addColumn(ArrayList<CustomCircle> column, Integer energy) {
        //si la columna está vacía, no se agrega a la colección
        if (column.isEmpty()) return;
        energizedColumns.add(new Pair<>(energy, column));
    }
    public static void addColumn(GridPaneObserver gridPaneObserver, ArrayList<CustomCircle> column, Integer energy) {
        if (column.isEmpty()) return;

        //solo se agrega la columna si no está quemada.
        if (!column.get(0).getIsBurned()) {
            gridPaneObserver.addColumn(column, energy);
        }
    }

    public void removeColumn(ArrayList<CustomCircle> column) {
        //se elimina la columna que tenga la misma id que la columna que se pasa por parametro.
        if (column.isEmpty()) return;

        energizedColumns.removeIf(el -> {
            ID elementID = el.getSecondValue().get(0).getID();
            ID columnID = column.get(0).getID(); //se obtiene la id de la columna a partir

            return elementID.equals(columnID);
        });
    }

    //LEDs
    public void addLeds(LED led) {
        leds.add(led);
    }

    public void removeLeds(LED led) {
        leds.remove(led);
    }

    //Switchs
    public void addSwitches(Switch switchs){
        switches.add(switchs);
    }

    public void removeSwitches(Switch switchs){
        switches.remove(switchs);
    }

    //BurnedColumn
    public void addBurnedColumn(ArrayList<CustomCircle> column) {
        burnedCircles.add(column);
    }



    //Setters...
    public void setRoot(AnchorPane root){
        this.root = root;
    }

    public void setCirclesCollection(ArrayList<CustomCircle> circlesCollection){
        this.CirclesCollection = circlesCollection;
    }

    public void setIsEnergyActivated(boolean isEnergyActivated) {
        this.isEnergyActivated = isEnergyActivated;
    }

    public void setLeds(ArrayList<LED> leds) {
        this.leds = leds;
    }

    public void setSwitches(ArrayList<Switch> switches) {
        this.switches = switches;
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

    public ArrayList<Cable> getBurnedCables() {return burnedCables;}

    public ArrayList<Resistencia> getBurnedResistencias() {return burnedResistencias;}

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

    public ArrayList<ChipOR> getChipsOR() {
        return chipsOR;
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