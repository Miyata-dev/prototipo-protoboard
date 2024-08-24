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
        this.rectangle.setOnMouseClicked(e -> {
            System.out.println("creando led");
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

    public void makeDraggableNode(Node node) {
        node.setOnMousePressed(e -> {
            //calculate offset points
            startX = e.getSceneX() - node.getTranslateX(); //get x value  or teh cursor
            startY = e.getSceneY() - node.getTranslateY(); //get x value or teh cursor
        });

        node.setOnMouseDragged(e -> {
            //set values every time the mouse drags.
            node.setTranslateX(e.getSceneX() - startX);
            node.setTranslateY(e.getSceneY() - startY);
        });
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
