package com.example.prototipo;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Arrays;

public class Cable extends Line {
    private int tipodecarga;
    private ID[] ids; //Contiene las id que tienen los CustomCircle a los que se conectan
    private String randomID; //esta id se da valor con un setter, no en el constructor.
    private CustomCircle[] circles;
    private String tipo; //null == Normal, resistencia == resistencia
    private boolean isBurned = false, isGhostCable = false;

    public Cable() {
        this.tipodecarga = 0;
    }

    public Cable(double startX, double startY, double endX, double endY) {
        setStartX(startX);
        setStartY(startY);
        setEndX(endX);
        setEndY(endY);
    }

    public Cable(CustomCircle firstCircle, CustomCircle secondCircle) {
        this.SetCircles(new CustomCircle[]{
                firstCircle,
                secondCircle
        });
        setStartX(firstCircle.getX());
        setEndX(secondCircle.getX());
        setStartY(firstCircle.getY());
        setEndY(secondCircle.getY());
        setRandomID();
        this.setIds(new ID[]{
                firstCircle.getID(),
                secondCircle.getID()
        });
    }

    //Metodos...

    //Este metodo lo que hace es conseguir el circulo que es diferente
    public static CustomCircle getCircleDiferentfromCable(CustomCircle circle, Cable cable) {
        if (circle.getID().equals(cable.getFirstCircle().getID())) {
            return cable.getSecondCircle();
        } else {
            return cable.getFirstCircle();
        }
    }

    public static boolean compareCables(Cable c1, Cable c2) {
        return ID.isSameID(c1.ids[0], c2.ids[0]) && ID.isSameID(c1.ids[1], c2.ids[1]);
    }


    //retorna null si no encuentra un cable.
    public static Cable getCableFromCollection(ArrayList<Cable> cables, Cable cableToFind) {
        for (Cable c : cables) {
            if (Cable.compareCables(c, cableToFind)) {
                return c;
            }
        }
        return null;
    }

    //Este metodo lo que hace es remover la carga de cable
    public void removeTipodecarga() {
        this.tipodecarga = 0;
    }


    //Este metodo lo que hace es comprobar si dos cables estan conectados mediante la misma columna o Fila
    public static boolean areConnected(Cable one, Cable two, GridPaneObserver gridPaneObserver) {
        //Creamos unas variables que preguntan si los cables tienen al menos un volt
        boolean isOneVolt = one.getIds()[0].getGridName().contains(gridPaneObserver.getGridVoltPrefix()) ||
                one.getIds()[1].getGridName().contains(gridPaneObserver.getGridVoltPrefix());

        boolean isTwoVolt = two.getIds()[0].getGridName().contains(gridPaneObserver.getGridVoltPrefix()) ||
                two.getIds()[1].getGridName().contains(gridPaneObserver.getGridVoltPrefix());

        boolean firstAtt, secondAtt;

        //Preguntamos si en el caso de que los dos cables tengan al menos un ciruclo que este conectado al switch se retorne
        if(one.getIds()[1].getGridName().equals("switchvolt1") || one.getIds()[0].getGridName().equals("switchvolt1") && two.getIds()[0].getGridName().equals("switchvolt1") || two.getIds()[1].getGridName().equals("switchvolt1")){
            return false;
        }
        if ( (one.getIds()[1].getGridName().equals("LedVolt1") || one.getIds()[0].getGridName().equals("LedVolt1") )  || (two.getIds()[0].getGridName().equals("LedVolt1") || two.getIds()[1].getGridName().equals("LedVolt1"))){
            return false;
        }//Y preguntamos lo mismo pero con el LED

        //preguntamos si los dos tienen al menos una id de Volt
        if (isOneVolt && isTwoVolt) {
            // Comparaciones por filas si ambos cables son volts
            firstAtt = one.getIds()[0].getIndexRow() == two.getIds()[1].getIndexRow() ||
                    one.getIds()[0].getIndexRow() == two.getIds()[0].getIndexRow();

            secondAtt = one.getIds()[1].getIndexRow() == two.getIds()[0].getIndexRow() ||
                    one.getIds()[1].getIndexRow() == two.getIds()[1].getIndexRow();
        } else {
            // Comparaciones por columnas si ambos cables son grid trails
            firstAtt = one.getIds()[0].getIndexColumn() == two.getIds()[1].getIndexColumn() ||
                    one.getIds()[0].getIndexColumn() == two.getIds()[0].getIndexColumn();

            secondAtt = one.getIds()[1].getIndexColumn() == two.getIds()[0].getIndexColumn() ||
                    one.getIds()[1].getIndexColumn() == two.getIds()[1].getIndexColumn();
        }

        boolean firstGridNameID =
                one.getIds()[0].getGridName().equals(two.getIds()[1].getGridName()) ||
                        one.getIds()[0].getGridName().equals(two.getIds()[0].getGridName());

        boolean secondGridNameID =
                one.getIds()[1].getGridName().equals(two.getIds()[0].getGridName()) ||
                        one.getIds()[1].getGridName().equals(two.getIds()[1].getGridName());

        return (firstAtt || secondAtt) && (firstGridNameID || secondGridNameID);
    }


    public boolean isConnectedToBatery() {
        String bateryID = "BateryVolt";
        //mira si una de las ids relacionadas a los circulos que conecta el cable pertenece a la bateria.
        boolean isFirstConnected = ids[0].getGridName().equals(bateryID);
        boolean isSecondConnected = ids[1].getGridName().equals(bateryID);

        return isFirstConnected || isSecondConnected;
    }

    public boolean isConnectedToVolts() {
        String[] voltNames = {
                "gridVolt1",
                "gridVolt2"
        };
        //ids[0].getGridName()
        boolean isFirstConnected = Arrays.asList(voltNames).contains(ids[0].getGridName());
        boolean isSecondConnected = Arrays.asList(voltNames).contains(ids[1].getGridName());

        return isFirstConnected || isSecondConnected;
    }

    //Setters
    public void setTipodecarga(int tipodecarga) {
        //si el tipo de energia es neutra (0), no se asigna al cable.
        if (tipodecarga == 0) return;
        this.tipodecarga = tipodecarga;
    }

    public void setIsGhostCable(boolean ghostCable) {
        this.isGhostCable = ghostCable;
    }

    public void setIds(ID[] ids) {
        this.ids = ids;
    }

    public void setRandomID(String randomID) {
        this.randomID = randomID;
    }

    public void setRandomID() {
        this.randomID = Utils.createRandomID();
    }

    public void setBurned() {
        System.out.println("burning...");
        this.isBurned = true;
        this.setStroke(Color.BROWN);
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setLine(double startX, double startY, double endX, double endY) {
        setStartX(startX);
        setStartY(startY);
        setEndX(endX);
        setEndY(endY);
    }

    public void SetCircles(CustomCircle[] circles){
        this.circles = circles;
    }

    //Getters..

    public String getRandomID() {
        return randomID;
    }

    public ID[] getIds() {
        return ids;
    }

    public int getTipodecarga() {
        return tipodecarga;
    }

    public CustomCircle getFirstCircle(){
        return circles[0];
    }

    public CustomCircle getSecondCircle(){
        return circles[1];
    }

    public CustomCircle[] Getcircles(){
        return this.circles;
    }

    public String getTipo() {
        return tipo;
    }

    public boolean getIsGhostCable() { return isGhostCable; }

    //calcula el largo de la linea
    public double getLineWidth() {
        double deltaX = this.getEndX() - this.getStartX();
        double deltaY = this.getEndY() - this.getStartY();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    public boolean getIsBurned(){
        return this.isBurned;
    }
    //si uno de los 2 ciruclos en los que el cable está conectado, entonces se concidera afectado por un chip.
    public boolean isAffectedByChip() {
        return getFirstCircle().getIsAffectedByChip() && getSecondCircle().getIsAffectedByChip();
    }

    @Override
    public String toString() {
        return "FirsCircle: " + getFirstCircle().getID().getGridName() + " SecondCircle: "+ getSecondCircle().getID().getGridName() + " carga: " + tipodecarga + " is ghost cable?: " + isGhostCable;
    }

    //TODO revisar.
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Cable)) return false;

        Cable cable = (Cable) obj;

        return this.randomID.equals(cable.getRandomID());
    }
}