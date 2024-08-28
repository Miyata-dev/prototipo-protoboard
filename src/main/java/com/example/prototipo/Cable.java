package com.example.prototipo;

import javafx.scene.shape.Line;

import java.util.ArrayList;

public class Cable {
    private Line line;
    private int tipodecarga;//0=Carga Neutra    1=Carga Positiva    -1=Carga Negativa
    private ID[] ids; //El Cable guarda las id de los circulos que esta utilizando

    public Cable(Line line) {
        this.line = line;
        this.tipodecarga = 0;//Se crea con carga neutra ya que no tiene ningun tipo de carga
    }

    public static boolean compareCables(Cable c1, Cable c2) {
        return ID.isSameID(c1.ids[0], c2.ids[0]) && ID.isSameID(c1.ids[1], c2.ids[1]);
    } //Retorna true=los dos cables son iguales.    false=los dos cables no son iguales

    public static Cable getCableFromCollection(ArrayList<Cable> cables, Cable cableToFind) {
        for (Cable c : cables) {
            if (Cable.compareCables(c, cableToFind)) {
                return c;
            }
        }
        return null; //Retorna null si no encuentra un cable
    }

    //Setters
    public void setTipodecarga(int tipodecarga) {
        this.tipodecarga = tipodecarga;
    }

    public void setIds(ID[] ids) {
        this.ids = ids;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    //Getters
    public ID[] getIds() {
        return ids;
    }

    public int getTipodecarga() {
        return tipodecarga;
    }

    public Line getLine() {
        return line;
    }
}
