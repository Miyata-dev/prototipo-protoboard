package com.example.prototipo;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class LED {
    private boolean estado;
    private double startX, startY;
    private Rectangle rectangle;

    public LED(boolean estado) {
        this.estado = estado;
        this.rectangle = createRectangle();
        Utils.makeDraggableNode(this.rectangle); //TODO hacer que no se pueda draggear cuando la linea se conecte con un circulo.
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

    public Rectangle getRectangle() {
        return rectangle;
    }

    //Setters
    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    //Getters
    public boolean getEstado() {
        return estado;
    }
}
