package com.example.prototipo;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LED {
    private boolean estado;
    private Punto ubicacion1;
    private Punto ubicacion2;
     private Image image;

    public LED(boolean estado, Punto ubicacion1, Punto ubicacion2, Image image) {
        this.estado = estado;
        this.ubicacion1 = ubicacion1;
        this.ubicacion2 = ubicacion2;
        this.image = image;
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

    public ImageView getImage() {
        ImageView imageView = new ImageView(image);

        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setY(100);
        imageView.setX(700);

        return imageView;
    }

}
