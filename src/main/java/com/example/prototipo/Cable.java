package com.example.prototipo;

import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

import java.util.ArrayList;

public class Cable {
    private Line line;
    private int tipodecarga;
    private ID[] ids;
    private boolean isDeletable;

    public Cable(Line line) {
        this.line = line;
        this.tipodecarga = 0;
        this.isDeletable = false;
        //TODO implementar un basurero que permita eliminar el cable.
        this.line.setOnMouseClicked(e -> {
            if (!isDeletable) return;
            ((AnchorPane) this.line.getParent()).getChildren().remove(this.line);
        });
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

    //Setters
    public void setTipodecarga(int tipodecarga) {
        //si el tipo de energia es neutra (0), no se asigna al cable.
        if (tipodecarga == 0) return;
        this.tipodecarga = tipodecarga;
    }

    public void setIds(ID[] ids) {
        this.ids = ids;
    }

    public void setLine(Line line) {
        this.line = line;
    }

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
