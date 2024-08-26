package com.example.prototipo;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
//1 = positivo, -1 = negativo, neutro = 0
public class CustomCircle extends Circle {
    private int state = 0;
    private ID id;
    private boolean isTaken; //Este atributo revisa si el circulo tiene un cable

    public CustomCircle(int radius, ID id, int state) {
        super((double) radius);
        this.id = id;
        this.state = state;
        this.setId("customCircle");
        this.isTaken = false;
    }

    public void setisTaken(boolean isTaken) {this.isTaken = isTaken;}

    public int getState() {
        return this.state;
    }
    public ID getID() {
        return this.id;
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