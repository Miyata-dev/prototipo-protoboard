package com.example.prototipo;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class CustomCircle extends Circle {
    private int state = 0;// state = 0 -> Neutro    state=1 -> Positivo,    state=-1 ->Negativo
    private ID id;
    private boolean isTaken;//Este atributo revisa si el circulo tiene un cable
    private Cable Wire;

    public CustomCircle(int radius, ID id, int state) {
        super((double) radius);
        this.id = id;
        this.state = state;
        this.setId("customCircle");
        this.isTaken = false;
    }

    public void setCable(Cable cable){
        this.Wire = cable;
    }
    public Cable getCable(){
        return this.Wire;
    }

    public void setisTaken(boolean isTaken) {this.isTaken = isTaken;}

    public boolean hasCable() {
        return this.Wire != null;
    }

    public boolean hasEnergy() {
        return state != 0;
    }

    //TODO implementar energia para los leds.
    public void setState(int state) {
        this.state = state;

        if (state == 1) {
            this.setFill(Color.ORANGE);
        } else if (state == -1) {
            this.setFill(Color.BLUE);
        } else if (state == 0) {
            this.setFill(Color.GRAY);
        }
    }
    public boolean getIsTaken(){return this.isTaken;}
    public int getState() {
        return this.state;
    }
    public ID getID() {
        return this.id;
    }

}