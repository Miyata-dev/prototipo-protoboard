package com.example.prototipo;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class LED {
    private boolean estado;
    private Punto ubicacion1;
    private Punto ubicacion2;
    private Rectangle rectangle;

    public LED(boolean estado, Punto ubicacion1, Punto ubicacion2) {
        this.estado = estado;
        this.ubicacion1 = ubicacion1;
        this.ubicacion2 = ubicacion2;
        this.rectangle = createRectangle();
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

    public void setUbicacion1(Punto ubicacion1) {
        this.ubicacion1 = ubicacion1;
    }

    public void setUbicacion2(Punto ubicacion2) {
        this.ubicacion2 = ubicacion2;
    }

    //Getters
    public boolean getEstado() {
        return estado;
    }

    public Punto getUbicacion1() {
        return ubicacion1;
    }

    public Punto getUbicacion2() {
        return ubicacion2;
    }
}
