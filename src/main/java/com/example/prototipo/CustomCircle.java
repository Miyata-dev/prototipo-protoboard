package com.example.prototipo;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
//1 = positivo, -1 = negativo, neutro = 0
public class CustomCircle extends Circle {
    private int state = 0;
    private ID id;


    public CustomCircle(int radius, ID id, int state) {
        super((double) radius);
        this.id = id;
        this.state = state;
        this.setId("customCircle");
    }

    public int getState() {
        return this.state;
    }
    //TODO implementar energia para los leds.
    public void setState(int state) {
        this.state = state;

        if (state == 1) {
            this.setFill(Color.BLUE);
        } else if (state == -1) {
            this.setFill(Color.ORANGE);
        }
    }

}