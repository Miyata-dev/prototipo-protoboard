package com.example.prototipo;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.concurrent.atomic.AtomicReference;

public class CustomShape extends Rectangle {
    private CustomCircle Leg1;
    private CustomCircle Leg2;
    private AtomicReference<Double> startX = new AtomicReference<>((double) 0);
    private AtomicReference<Double> startY = new AtomicReference<>((double) 0);
    private String UniqueID;
    private String Type;
    private boolean hasMoved = false;

    public CustomShape(int x, int y, int width, int height, Color color, String type) {
        super(x, y, width, height);
        //El grosor de la linea de la figura independientemente del tamano que tenga
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(3);
        this.setFill(color);
        this.UniqueID= Utils.createRandomID();
        this.Type = type;
    }

    public void setLeg1(CustomCircle leg1) {
        this.Leg1 = leg1;
    }

    public void setLeg2(CustomCircle leg2) {
        this.Leg2 = leg2;
    }

    public CustomCircle getLeg1() {
        return Leg1;
    }

    public CustomCircle getLeg2() {
        return Leg2;
    }

    public boolean getHasMoved() {
        return hasMoved;
    }

    public AtomicReference<Double> getStartX(){return this.startX;}

    public AtomicReference<Double> getStartY(){return this.startY;}

    public String getUniqueID(){
        return this.UniqueID;
    }
    public String getType(){return this.Type;}

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
}