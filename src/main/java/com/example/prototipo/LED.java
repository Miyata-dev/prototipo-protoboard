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
                System.out.println(customShape.getLeg2().getCable().getRandomID());
                System.out.println(customShape.getLeg1().getCable().getRandomID());
            }
        });
    }

    public void init(CustomShape customShape) {
        ID idUno = new ID(1,1,"LedVolt1");
        idUno.setIsForGridpane(false);

        ID idDos = new ID(2,1,"LedVolt1");
        idDos.setIsForGridpane(false);

        double x = customShape.getX();
        double y = customShape.getY();

        customShape.setLeg1(new CustomCircle(5,idUno,0));
        customShape.setLeg2(new CustomCircle(5,idDos,0));
        //Creamos la primera pata del LED
        customShape.getLeg1().setisTaken(true);
        customShape.getLeg1().setFill(Color.ROYALBLUE);
        customShape.getLeg1().setTranslateX(x-5);
        customShape.getLeg1().setTranslateY(y+7.5);
        //Creamos la segunda pata del LED
        customShape.getLeg2().setisTaken(true);
        customShape.getLeg2().setFill(Color.ROYALBLUE);
        customShape.getLeg2().setTranslateX(x+30);
        customShape.getLeg2().setTranslateY(y+7.5);

        this.getChildren().add(customShape.getLeg1());
        this.getChildren().add(customShape.getLeg2());
    }

    public void LedFunction(CustomShape customShape){
        //false -> apagado      true-> encendido
        if(this.state == true){
            customShape.setFill(Color.GREEN);
        } else{
            customShape.setFill(Color.RED);
        }
    }

}
