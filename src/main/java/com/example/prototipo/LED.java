package com.example.prototipo;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import java.util.concurrent.atomic.AtomicReference;

public class LED extends Group {
    private boolean state;  //false -> apagado      true-> encendido
    private AtomicReference<Double> startX = new AtomicReference<>((double) 0);
    private AtomicReference<Double> startY = new AtomicReference<>((double) 0);
    private Basurero basurero;
    private AnchorPane root;

    public LED(boolean state, CustomShape customShape, Basurero basurero, AnchorPane root) {
        super(customShape);
        //TODO invalidar este tipo de IDS para pintar

        this.state = state;
        this.basurero = basurero;
        this.root = root;

        Utils.makeDraggableNode(this, startX, startY);
        init(customShape);

        this.setOnMouseReleased(e -> {
            customShape.getLeg1().setisTaken(false);
            customShape.getLeg2().setisTaken(false);
            Utils.makeUndraggableNode(this);
            //CreateLegsLed(customShape);

            if (basurero.getIsActive()) {
                System.out.println("this: " + this);

                System.out.println(customShape.getLeg2().getCable() + " has cable: " + customShape.getLeg2().hasCable());
                System.out.println(customShape.getLeg1().getCable() + " has cable: " + customShape.getLeg1().hasCable());

                Node node = (Node) e.getTarget();

                System.out.println(node);

                if (customShape.getLeg2().hasCable()) {
                    System.out.println("im here");
                    Cable cableToRemove = customShape.getLeg2().getCable();
                    System.out.println(cableToRemove.getRandomID());

                    System.out.println(node.getParent().getParent());
                    root.getChildren().removeIf(element -> {
                        return element instanceof Cable && ((Cable) element).getRandomID().equals(cableToRemove.getRandomID());
                    });
                }

                if (customShape.getLeg1().hasCable()) {
                    System.out.println("im here");
                    Cable cableToRemove = customShape.getLeg1().getCable();
                    System.out.println(cableToRemove.getRandomID());
                    System.out.println("hola" + node.getParent());

                    root.getChildren().removeIf(element -> {
                        return element instanceof Cable && ((Cable) element).getRandomID().equals(cableToRemove.getRandomID());
                    });
                }

                //como node es Rectangle, el getParent devuelve LED, y no AnchorPane, por lo cual hay que obtener el Parent de LED, por eso está el método getparent 2 veces.

                root.getChildren().remove(this);
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

        customShape.getLeg1().setisTaken(true);
        customShape.getLeg1().setFill(Color.RED);
        customShape.getLeg1().setTranslateX(x-5);
        customShape.getLeg1().setTranslateY(y+7.5);

        customShape.getLeg2().setisTaken(true);
        customShape.getLeg2().setFill(Color.RED);
        customShape.getLeg2().setTranslateX(x+30);
        customShape.getLeg2().setTranslateY(y+7.5);

        this.getChildren().add(customShape.getLeg1());
        this.getChildren().add(customShape.getLeg2());
    }

    /*
    private void CreateLegsLed(CustomShape led){
        double x = led.getX();
        double y = led.getY();
        //TODO invalidar este tipo de IDS para pintar
        ID id = new ID(1,1,"LedVolt1");
        id.setIsForGridpane(false);

        this.Leg1.setisTaken(false);
        this.Leg2.setisTaken(false);
        this.Leg1.setFill(Color.RED);
        this.Leg2.setFill(Color.RED);
        this.Leg1.setTranslateX(x-5);
        this.Leg1.setTranslateY(y+7.5);
        this.Leg2.setTranslateX(x+30);
        this.Leg2.setTranslateY(y+7.5);
        this.getChildren().add(Leg1);
        this.getChildren().add(Leg2);
    } */
}
