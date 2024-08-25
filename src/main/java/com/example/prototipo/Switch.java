package com.example.prototipo;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Switch {
    private boolean PasoDeCarga; // true= deja la carga pasar           false= la carga no pasa
    private Rectangle Square;

    public Switch(boolean PasoDeCarga ) {
        this.PasoDeCarga = PasoDeCarga;
        this.Square = CreateSquare();
        Utils.makeDraggableNode(this.Square);
    }

    public void setPasoDeCarga(boolean PasoDeCarga) {this.PasoDeCarga = PasoDeCarga;}
    public boolean getPasoDeCarga() {return PasoDeCarga;}
    public Rectangle GetSquare() {return Square;}

    public Rectangle CreateSquare(){
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