package com.example.prototipo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.concurrent.atomic.AtomicReference;

public class LED extends Group {
    private boolean state;  //false -> apagado      true-> encendido
    private AtomicReference<Double> startX = new AtomicReference<>((double) 0);
    private AtomicReference<Double> startY = new AtomicReference<>((double) 0);
    private CustomCircle Leg1;
    private CustomCircle Leg2;

    public LED(boolean state, CustomShape customShape) {
        super(customShape);
        this.state = state;

        Utils.makeDraggableNode(this, startX, startY);
        this.setOnMouseClicked(e -> {
            Utils.makeUndraggableNode(this);
            CreateLegsLed(customShape);
        });
    }
    private void CreateLegsLed(CustomShape led){
        double x = led.getX();
        double y = led.getY();
        //TODO invalidar este tipo de IDS para pintar
        ID id = new ID(1,1,"LedVolt1");
        id.setIsForGridpane(false);
        this.Leg1 = new CustomCircle(5,id,0);
        this.Leg2 = new CustomCircle(5,id,0);
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
    }

    private Rectangle createRectangle() {
        Rectangle rectangle = new Rectangle(50, 50, 25, 15);

        rectangle.setFill(Color.YELLOW);

        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(2);

        rectangle.setY(300);
        rectangle.setX(300);

        return rectangle;
    }


    //Setters
    public void setEstado(boolean state) {
        this.state = state;
    }

    //Getters
    public boolean getEstado() {
        return state;
    }

    public AtomicReference<Double> getStartX() {
        return startX;
    }

    public AtomicReference<Double> getStartY() {
        return startY;
    }
}
