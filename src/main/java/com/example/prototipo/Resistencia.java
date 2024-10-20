package com.example.prototipo;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;

public class Resistencia extends Cable {
    private double voltaje;
    private Rectangle rec;
    private Path arrow;


    public Resistencia(double voltaje){
        this.voltaje = voltaje;
        this.setStroke(Color.RED);
        this.setStrokeWidth(5);
        this.setTipo("resistencia");
    }

    public Resistencia(double startX, double startY, double endX, double endY, double voltaje) {
        setStartX(startX);
        setStartY(startY);
        setEndX(endX);
        setEndY(endY);
        this.setStroke(Color.RED);
        this.setStrokeWidth(5);
        this.setRandomID();
        this.setTipo("resistencia");
        System.out.println("tipo en constructor: " + this.getTipo());
    }

    public void createRectangle() {
        rec = new Rectangle(14, 14);

        //coloca el angulo de forma correcta.
        double deltaX = this.getEndX() - this.getStartX();
        double deltaY = this.getEndY() - this.getStartY();
        double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));

        double medioX = (this.getStartX() + this.getEndX()) / 2;
        double medioY = (this.getStartY() + this.getEndY()) / 2;

        // Posicionar el rectángulo en el punto medio de la línea
        rec.setX(medioX - rec.getWidth() / 2);
        rec.setY(medioY - rec.getHeight() / 2);

        // Rotar el rectángulo para que siga el ángulo de la línea
        rec.setRotate(angle);
        rec.setFill(Color.BLACK);

        ((AnchorPane) this.getParent()).getChildren().add(rec);
        createArrow(medioX,medioY,angle);
    }
    public void createArrow(double x,double y,double angle){
        arrow = new Path();
        double arrowLength = 15;
        double arrowWidth = 4;
        MoveTo moveTo = new MoveTo(x, y);
        LineTo lineTo1 = new LineTo(x - arrowLength * Math.cos(Math.toRadians(angle - 30)),
                y - arrowLength * Math.sin(Math.toRadians(angle - 30)));
        LineTo lineTo2 = new LineTo(x - arrowLength * Math.cos(Math.toRadians(angle + 30)),
                y - arrowLength * Math.sin(Math.toRadians(angle + 30)));
        LineTo lineTo3 = new LineTo(x, y); // Regresar al punto central

        // Agregar los elementos al Path
        arrow.getElements().addAll(moveTo, lineTo1, lineTo2, lineTo3);
        arrow.setFill(Color.YELLOW);
        ((AnchorPane) this.getParent()).getChildren().add(arrow);
    }

    public void setVoltaje(double voltaje) {
        this.voltaje = voltaje;
    }
    public double getVoltaje(){
        return voltaje;
    }
    public Rectangle getRec() {
        return rec;
    }
    public Path getArrow(){
        return arrow;
    }

    @Override
    public void setBurned() {
        super.setBurned();
        arrow.setFill(Color.BROWN);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Resistencia)) return false;

        Resistencia other = (Resistencia) obj;

        return this.getRandomID().equals(other.getRandomID());
    }
}