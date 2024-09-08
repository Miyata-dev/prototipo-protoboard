package com.example.prototipo;

import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.concurrent.atomic.AtomicReference;

public class LED extends Group {
    private boolean state;  //false -> apagado      true-> encendido
    private Basurero basurero;


    public LED(boolean state, CustomShape customShape, Basurero basurero) {
        super(customShape);
        this.state = state;
        this.basurero = basurero;
        LedFunction(customShape);

        Utils.makeDraggableNode(this, customShape.getStartX(), customShape.getStartY());
        init(customShape);

        this.setOnMouseClicked(e -> {
            //Al momento de soltar el LED se pueden crear Cables
            customShape.getLeg1().setisTaken(false);
            customShape.getLeg2().setisTaken(false);
            Utils.makeUndraggableNode(this);




            if (basurero.getIsActive()) {

                ONorOFF(customShape);
                System.out.println(customShape.getLeg2().getCable().getRandomID());
                System.out.println(customShape.getLeg1().getCable().getRandomID());
            }
        });
    }

    //Este metodo crea las patas del LED de manera ordenada
    public void init(CustomShape customShape) {
        ID idUno = new ID(1, 1, "LedVolt1");
        idUno.setIsForGridpane(false);

        ID idDos = new ID(2, 1, "LedVolt1");
        idDos.setIsForGridpane(false);

        double x = customShape.getX();
        double y = customShape.getY();

        customShape.setLeg1(new CustomCircle(5, idUno, 0));
        customShape.setLeg2(new CustomCircle(5, idDos, 0));
        //Creamos la primera pata del LED
        customShape.getLeg1().setisTaken(true);
        customShape.getLeg1().setFill(Color.ROYALBLUE);
        customShape.getLeg1().setTranslateX(x - 5);
        customShape.getLeg1().setTranslateY(y + 7.5);
        //Creamos la segunda pata del LED
        customShape.getLeg2().setisTaken(true);
        customShape.getLeg2().setFill(Color.ROYALBLUE);
        customShape.getLeg2().setTranslateX(x + 30);
        customShape.getLeg2().setTranslateY(y + 7.5);

        this.getChildren().add(customShape.getLeg1());
        this.getChildren().add(customShape.getLeg2());
    }

    //Este metodo lo que hace es cambiar el color del LED cuando Cambia su estado
    public void LedFunction(CustomShape customShape) {
        //false -> apagado      true-> encendido
        if (this.state == true) {
            customShape.setFill(Color.GREEN);
        } else {
            customShape.setFill(Color.RED);
        }
    }


    //TODO confirmar que se este cumpliendo la condicion
    public void ONorOFF(CustomShape customShape) {
        //Preguntamos si las dos patas tienen energía y ademas la energia que tienen es distinta entre si
        if ((customShape.getLeg2().hasEnergy() && customShape.getLeg1().hasEnergy()) && (customShape.getLeg2().getState() != customShape.getLeg1().getState())) {
            System.out.println("hola");
            this.state=true;
            LedFunction(customShape);
        } else {
            this.state=false;
            System.out.println("hola");
            LedFunction(customShape);
        }
    }
}