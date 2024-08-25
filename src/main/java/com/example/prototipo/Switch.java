package com.example.prototipo;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Switch {
    private boolean PasoDeCarga;
    private Image image;

    public Switch(boolean PasoDeCarga,  Image image) {
        this.PasoDeCarga = PasoDeCarga;
        this.image = image;
    }

    public void setPasoDeCarga(boolean PasoDeCarga) {this.PasoDeCarga = PasoDeCarga;}
    public boolean getPasoDeCarga() {return PasoDeCarga;}
    public ImageView getImage() {
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setY(500);
        imageView.setX(700);

        return imageView;
    }
    public void setImage(Image image) {
        this.image = image;
    }
}