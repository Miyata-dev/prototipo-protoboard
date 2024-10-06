package com.example.prototipo;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class CustomCircle extends Circle {
    private int state = 0;// state = 0 -> Neutro    state=1 -> Positivo,    state=-1 ->Negativo
    private ID id;
    private boolean isTaken, isBurned;//Este atributo revisa si el circulo tiene un cable, si está quemado no se va a pintar.
    private Cable Wire;

    public CustomCircle(int radius, ID id, int state) {
        super((double) radius);
        this.id = id;
        this.state = state;
        this.setId("customCircle");
        this.isTaken = false;
        this.isBurned = false; //solo se quema una columna si es que
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
        if (state == 0) return; //si el estado es igual a 0, entonces no se toma encuanta como un vaLor valido.
        //si el círculo está quemado sin importar la carga que tenga se coloca como negro pq está qyemado.
        if (isBurned) {
            this.setFill(Color.BLACK);
            return;
        }; //si está quemado no se pasa el estado.

        this.state = state;

        if (state == 1) {
            this.setFill(Color.ORANGE);
        } else if (state == -1) {
            this.setFill(Color.BLUE);
        }
    }

    public void removeEnergy() {
        if (isBurned) return;

        this.state = 0;
        this.setFill(Color.GRAY);
    }

    public boolean getIsTaken(){return this.isTaken;}
    public int getState() {
        return this.state;
    }
    public ID getID() {
        return this.id;
    }

    public boolean getIsBurned(){
        return this.isBurned;
    }

    public void setBurned() {
        this.removeEnergy();
        this.setisTaken(true);
        this.isBurned = true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof CustomCircle)) return false;

        CustomCircle other = (CustomCircle) obj;

        boolean sameID = ID.isSameID(this.getID(), other.getID());
        //boolean sameID = this.id.equals(other.getID());

        return sameID;
    }
}