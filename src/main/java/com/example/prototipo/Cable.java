package com.example.prototipo;

import javafx.scene.shape.Line;

import java.util.ArrayList;

public class Cable extends Line {
    private int tipodecarga;
    private ID[] ids; //Contiene las id que tienen los CustomCircle a los que se conectan
    private String randomID; //esta id se da valor con un setter, no en el constructor.

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
}
