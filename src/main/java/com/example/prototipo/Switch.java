package com.example.prototipo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class Switch extends Group {//Se utiliza un rectangulo para hacer un cuadrado
    private boolean PasoDeCarga; // true -> deja la carga pasar           false -> la carga no pasa


    public Switch(boolean PasoDeCarga, CustomShape customShape) {
        super(customShape);
        this.PasoDeCarga = PasoDeCarga;

        Utils.makeDraggableNode(this, customShape.getStartX(), customShape.getStartY());//Llamamos a la clase Util para poder convertir el Switch en un nodo movible.
        Init(customShape);

        this.setOnMouseClicked(e -> {
            Utils.makeUndraggableNode(this);
            customShape.getLeg1().setisTaken(false);
            customShape.getLeg2().setisTaken(false);
        });
    }


    public void Init(CustomShape customShape){
        double x = customShape.getX();
        double y = customShape.getY();

        ID IdUno= new ID(1,1, "switchvolt1");
        IdUno.setIsForGridpane(false);
        ID IdDos = new ID (2,1,"switchvolt1");
        IdDos.setIsForGridpane(false);
        //Se Crea La primera pata del Switch
        customShape.setLeg1(new CustomCircle(5, IdUno, 0));
        customShape.getLeg1().setisTaken(true);
        customShape.getLeg1().setFill(Color.YELLOW);
        customShape.getLeg1().setTranslateX(x-5);
        customShape.getLeg1().setTranslateY(y+15);

        //Se Crea la Segunda pata del Switch
        customShape.setLeg2(new CustomCircle(5, IdDos, 0));
        customShape.getLeg2().setisTaken(true);
        customShape.getLeg2().setFill(Color.YELLOW);
        customShape.getLeg2().setTranslateX(x+35);
        customShape.getLeg2().setTranslateY(y+15);

        //Se añaden al grupo de la Clase Switch
        this.getChildren().add(customShape.getLeg1());
        this.getChildren().add(customShape.getLeg2());
    }

    public void ChargePass(){
        if(this.PasoDeCarga == true){
            //Preguntar donde lleva el cable y llamar la función para pintar esa fila.
        } else {
            // si no despintar hacia donde lleva el cable
        }
    }

}
