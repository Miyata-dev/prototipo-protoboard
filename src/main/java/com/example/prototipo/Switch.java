package com.example.prototipo;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.concurrent.atomic.AtomicReference;

public class Switch {
    private boolean PasoDeCarga; // true= deja la carga pasar           false= la carga no pasa
    private AtomicReference<Double> startX = new AtomicReference<>((double) 0);
    private AtomicReference<Double> startY = new AtomicReference<>((double) 0);
    private Rectangle Square;//Se utiliza un rectangulo para hacer un cuadrado

    public Switch(boolean PasoDeCarga ) {
        this.PasoDeCarga = PasoDeCarga;
        this.Square = CreateSquare();
        Utils.makeDraggableNode(this.Square, startX, startY);//Llamamos a la clase Util para poder convertir el Switch en un nodo movible.

        this.Square.setOnMouseClicked(e->{
            Utils.makeUndraggableNode(this.Square);
        });

    }

    public void setPasoDeCarga(boolean PasoDeCarga) {this.PasoDeCarga = PasoDeCarga;}
    public boolean getPasoDeCarga() {return PasoDeCarga;}
    public Rectangle GetSquare() {return Square;}

    public Rectangle CreateSquare(){ //Función que crea el Cuadrado en la posición x=720 e y=554
        Rectangle Square = new Rectangle(50,50, 30,30);
        Square.setFill(Color.WHITE);
        Square.setStroke(Color.BLACK);
        Square.setStrokeWidth(3);
        //Le añadimos la ubicacion en la que aparecera
        Square.setX(720);
        Square.setY(554);
        return Square;
    }

}