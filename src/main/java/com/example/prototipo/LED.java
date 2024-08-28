package com.example.prototipo;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.concurrent.atomic.AtomicReference;

public class LED {
    private boolean state;
    private AtomicReference<Double> startX = new AtomicReference<>((double) 0);
    private AtomicReference<Double> startY = new AtomicReference<>((double) 0);
    private Rectangle rectangle;

    public LED(boolean state) {
        this.state = state;
        this.rectangle = createRectangle();
        Utils.makeDraggableNode(this.rectangle, startX, startY); //TODO hacer que no se pueda draggear cuando la linea se conecte con un circulo.

        this.rectangle.setOnMouseClicked(e -> {
            Utils.makeUndraggableNode(this.rectangle);
        });
    }

    private Rectangle createRectangle() {
        Rectangle rectangle = new Rectangle(50, 50, 25, 15);

        rectangle.setFill(Color.YELLOW);
        rectangle.setTranslateX(300);
        rectangle.setTranslateY(300);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(2);

        rectangle.setY(100);
        rectangle.setX(420);

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

    public Rectangle getRectangle() {
        return rectangle;
    }
}
