package com.example.prototipo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.concurrent.atomic.AtomicReference;

public class Switch {
    private boolean PasoDeCarga; // true= deja la carga pasar           false= la carga no pasa
    private AtomicReference<Double> startX = new AtomicReference<>((double) 0);
    private AtomicReference<Double> startY = new AtomicReference<>((double) 0);
    private Rectangle Square;//Se utiliza un rectangulo para hacer un cuadrado
    private CustomCircle pata1;
    private CustomCircle pata2;
    private Group prueba;


    public Switch(boolean PasoDeCarga) {
        this.PasoDeCarga = PasoDeCarga;
        this.Square = CreateSquare();
        this.pata1= new CustomCircle(15, new ID(1,1, "gridVolt1"), 0);
        this.pata2= new CustomCircle(15, new ID(1,1, "gridVolt1"), 0);

        this.prueba=new Group(this.Square, this.pata1, this.pata2);//Igualmente funciona

        /*
        this.prueba.getChildren().add(this.Square);
        this.prueba.getChildren().add(this.pata1);
        this.prueba.getChildren().add(this.pata2);
        */
        this.pata1.setTranslateX(710);
        this.pata1.setTranslateY(554);
        this.pata2.setTranslateY(554);
        this.pata2.setTranslateX(730);
        Utils.makeDraggableNode(this.prueba, startX, startY);//Llamamos a la clase Util para poder convertir el Switch en un nodo movible.

        this.prueba.setOnMouseClicked(e -> {
            Utils.makeUndraggableNode(this.prueba);
        });

    }

    public void setPasoDeCarga(boolean PasoDeCarga) {
        this.PasoDeCarga = PasoDeCarga;
    }

    public boolean getPasoDeCarga() {
        return PasoDeCarga;
    }

    public Rectangle GetSquare() {
        return Square;
    }

    public Rectangle CreateSquare() { //Función que crea el Cuadrado en la posición x=720 e y=554
        Rectangle Square = new Rectangle(50, 50, 30, 30);
        Square.setFill(Color.WHITE);
        Square.setStroke(Color.WHITE);
        Square.setStrokeWidth(3);
        //Le añadimos la ubicacion en la que aparecera
        Square.setX(720);
        Square.setY(554);
        return Square;
    }
    public Group getPrueba(){
        return this.prueba;
    }

}
