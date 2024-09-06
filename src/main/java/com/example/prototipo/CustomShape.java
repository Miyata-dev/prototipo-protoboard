package com.example.prototipo;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CustomShape extends Rectangle {
    private CustomCircle Leg1;
    private CustomCircle Leg2;

    public CustomShape(int x, int y, int width, int height, Color color) {
        super(x, y, width, height);
        //El grosor de la linea de la figura independientemente del tamano que tenga
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(3);
        this.setFill(color);
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
}