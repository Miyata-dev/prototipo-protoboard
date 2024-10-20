package com.example.prototipo;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Resistencia extends Cable {
    private double voltaje;

    public Resistencia(double voltaje){
        this.voltaje = voltaje;
        this.setStroke(Color.RED);
        this.setStrokeWidth(5);
    }

    public Resistencia(double startX, double startY, double endX, double endY, double voltaje) {
        setStartX(startX);
        setStartY(startY);
        setEndX(endX);
        setEndY(endY);
        this.setStroke(Color.RED);
        this.setStrokeWidth(5);
        this.setRandomID();
    }

    public void createRectangle() {
        Rectangle rectangle = new Rectangle(14, 14);

        //coloca el angulo de forma correcta.
        double deltaX = this.getEndX() - this.getStartX();
        double deltaY = this.getEndY() - this.getStartY();
        double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));

        double medioX = (this.getStartX() + this.getEndX()) / 2;
        double medioY = (this.getStartY() + this.getEndY()) / 2;

        // Posicionar el rectángulo en el punto medio de la línea
        rectangle.setX(medioX - rectangle.getWidth() / 2);
        rectangle.setY(medioY - rectangle.getHeight() / 2);

        // Rotar el rectángulo para que siga el ángulo de la línea
        rectangle.setRotate(angle);
        rectangle.setFill(Color.BLACK);

        ((AnchorPane) this.getParent()).getChildren().add(rectangle);
    }

    public void setVoltaje(double voltaje) {
        this.voltaje = voltaje;
    }
    public double getVoltaje(){
        return voltaje;
    }
}
