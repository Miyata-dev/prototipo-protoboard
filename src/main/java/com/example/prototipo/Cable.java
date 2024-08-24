package com.example.prototipo;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;

public class Cable extends Line {
    private int tipodecarga;
    private Punto origenpunto;
    private Punto destinopunto;
    private Image image;

    public Cable(int tipodecarga, Punto origenpunto, Punto destinopunto, Image image) {
        this.tipodecarga = tipodecarga;
        this.origenpunto = origenpunto;
        this.destinopunto = destinopunto;
        this.image = image;
    }
    //Setters
    public void SetTipodecarga(int tipodecarga){
        this.tipodecarga = tipodecarga;
    }
    public void SetOrigenpunto(Punto origenpunto){
        this.origenpunto = origenpunto;
    }
    public void SetDestinopunto(Punto destinopunto){
        this.destinopunto = destinopunto;
    }
    //Getters
    public int GetTipodecarga(){
        return tipodecarga;
    }
    public Punto GetOrigenpunto(){
        return origenpunto;
    }
    public Punto GetDestinopunto(){
        return destinopunto;
    }

    public ImageView getImage() {
        ImageView imageView = new ImageView(image);
        System.out.println(image);

        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setY(0);
        imageView.setX(0);

        return imageView;
    }
}
