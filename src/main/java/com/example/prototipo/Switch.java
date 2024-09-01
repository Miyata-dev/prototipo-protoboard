package com.example.prototipo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.concurrent.atomic.AtomicReference;

public class Switch extends Rectangle {//Se utiliza un rectangulo para hacer un cuadrado
    private boolean PasoDeCarga; // true -> deja la carga pasar           false -> la carga no pasa
    private AtomicReference<Double> startX = new AtomicReference<>((double) 0);
    private AtomicReference<Double> startY = new AtomicReference<>((double) 0);
    private CustomCircle pata1;
    private CustomCircle pata2;
    private Group SwitchGroup;


    public Switch(boolean PasoDeCarga) {
        super();
        this.PasoDeCarga = PasoDeCarga;

        Rectangle square = CreateSquare();

        this.SwitchGroup=new Group(square);

        Utils.makeDraggableNode(this.SwitchGroup, startX, startY);//Llamamos a la clase Util para poder convertir el Switch en un nodo movible.

        this.SwitchGroup.setOnMouseClicked(e -> {
            Utils.makeUndraggableNode(this.SwitchGroup);
            TranslatePatas(square);
        });
    }


    public void TranslatePatas(Rectangle square){
        double x = square.getX();
        double y = square.getY();
        ID id= new ID(0,0, "volt1");
        id.setIsForGridpane(false);
        System.out.println("is for gridPane?: " + id.getIsForGridpane());
        this.pata1= new CustomCircle(5, id, 0);
        this.pata2= new CustomCircle(5, id, 0);
        this.pata1.setisTaken(false);
        this.pata2.setisTaken(false);
        this.pata1.setFill(Color.RED);
        this.pata2.setFill(Color.RED);
        this.pata1.setTranslateX(x-5);
        this.pata1.setTranslateY(y+15);
        this.pata2.setTranslateX(x+35);
        this.pata2.setTranslateY(y+15);
        this.SwitchGroup.getChildren().add(this.pata1);
        this.SwitchGroup.getChildren().add(this.pata2);
    }
//prueba
    public Rectangle CreateSquare() { //Función que crea el Cuadrado en la posición x=720 e y=554
        Rectangle Square = new Rectangle(50, 50, 30, 30);
        Square.setFill(Color.WHITE);
        Square.setStroke(Color.BLACK);
        Square.setStrokeWidth(3);
        //Le añadimos la ubicacion en la que aparecera
        Square.setX(720);
        Square.setY(554);
        return Square;
    }

    //Setters
    public void setPasoDeCarga(boolean PasoDeCarga) {
        this.PasoDeCarga = PasoDeCarga;
    }


    //Getters
    public boolean getPasoDeCarga() {
        return PasoDeCarga;
    }
    public Group getPrueba(){
        return this.SwitchGroup;
    }

}
