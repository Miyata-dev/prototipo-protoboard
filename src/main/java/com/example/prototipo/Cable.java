package com.example.prototipo;

import javafx.scene.shape.Line;

import java.util.ArrayList;

public class Cable extends Line {
    private int tipodecarga;
    private ID[] ids; //Contiene las id que tienen los CustomCircle a los que se conectan
    private String randomID; //esta id se da valor con un setter, no en el constructor.
    private CustomCircle[] circles;

    public Cable() {
        this.tipodecarga = 0;
    }

    public Cable(double startX, double startY, double endX, double endY) {
        setStartX(startX);
        setStartY(startY);
        setEndX(endX);
        setEndY(endY);
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

    public void RestoreEnergy(){
        this.tipodecarga = 0;
    }

    //Setters
    public void setTipodecarga(int tipodecarga) {
        //si el tipo de energia es neutra (0), no se asigna al cable.
        if (tipodecarga == 0) return;
        this.tipodecarga = tipodecarga;
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

    public void setLine(double startX, double startY, double endX, double endY) {
        setStartX(startX);
        setStartY(startY);
        setEndX(endX);
        setEndY(endY);
    }

    public String getRandomID() {
        return randomID;
    }

    public ID[] getIds() {
        return ids;
    }
    //TODO usarlo en el led para poder prenderlo (solo si tiene energia positiva y negativa)
    public int getTipodecarga() {
        return tipodecarga;
    }

    //TODO mejorar la logica de esta comparacion.
    public static boolean areConnected(Cable one, Cable two) {
        //en estás condiciones (la primera y la segunda) se mira que el indice de los cículos de los cables se conecten entre si.
        boolean firstAtt =
            one.getIds()[0].getIndexColumn() == two.getIds()[1].getIndexColumn() ||
            one.getIds()[0].getIndexColumn() == two.getIds()[0].getIndexColumn();

        boolean secondAtt =
            one.getIds()[1].getIndexColumn() == two.getIds()[0].getIndexColumn() ||
            one.getIds()[1].getIndexColumn() == two.getIds()[1].getIndexColumn();
        //
        boolean firstGridNameNameID =
            one.getIds()[0].getGridName().equals(two.getIds()[1].getGridName()) ||
            one.getIds()[0].getGridName().equals(two.getIds()[0].getGridName());

        boolean secondGridNameID =
            one.getIds()[1].getGridName().equals(two.getIds()[0].getGridName()) ||
            one.getIds()[1].getGridName().equals(two.getIds()[1].getGridName());

        return (firstAtt || secondAtt) && (firstGridNameNameID || secondGridNameID);
    }
    //mira si un cable está PRUEBALO DSP DE AYUDAR AL POLLO MRD.
    public boolean isConnectedToBatery() {
        String bateryID = "BateryVolt";
        //mira si una de las ids relacionadas a los circulos que conecta el cable pertenece a la bateria.
        boolean isFirstConnected = ids[0].getGridName().equals(bateryID);
        boolean isSecondConnected = ids[1].getGridName().equals(bateryID);

        return isFirstConnected || isSecondConnected;
    }

    public void SetCircles(CustomCircle[] circles){
        this.circles = circles;
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
}