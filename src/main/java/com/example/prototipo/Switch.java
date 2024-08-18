package com.example.prototipo;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Switch {
    private boolean PasoDeCarga;
    private Punto origenpunto;
    private Punto destinopunto;
    private Image image;

    public Switch(boolean PasoDeCarga, Punto origenpunto, Punto destinopunto, Image image) {
        this.PasoDeCarga = PasoDeCarga;
        this.origenpunto = origenpunto;
        this.destinopunto = destinopunto;
        this.image = image;
    }
    public void setPasoDeCarga(boolean PasoDeCarga) {this.PasoDeCarga = PasoDeCarga;}
    public void setOrigenpunto(Punto origenpunto) {this.origenpunto = origenpunto;}
    public void setDestinopunto(Punto destinopunto){this.destinopunto = destinopunto;}

    public boolean getPasoDeCarga() {return PasoDeCarga;}
    public Punto getOrigenpunto() {return origenpunto;}
    public Punto getDestinopunto() {return destinopunto;}

    public ImageView getImage() {
        ImageView imageView = new ImageView(image);

        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setY(500);
        imageView.setX(700);

        return imageView;
    }
}