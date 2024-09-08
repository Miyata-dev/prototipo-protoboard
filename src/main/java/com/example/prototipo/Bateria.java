package com.example.prototipo;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.paint.Color;

public class Bateria {
    private Image image;
    private CustomCircle PoloNegativo,PoloPositivo;
    private Group Polos;

    public Bateria(Image image) {
        this.image = image;
        PoloNegativo = new CustomCircle(5,new ID(0,0,"volt1"),-1);
        PoloPositivo = new CustomCircle(5,new ID(0,0,"volt1"),1);

        Polos = new Group(this.PoloNegativo,this.PoloPositivo);

        this.PoloNegativo.setCenterX(750);
        this.PoloNegativo.setCenterY(350);

        this.PoloPositivo.setCenterX(750);
        this.PoloPositivo.setCenterY(250);

        PoloPositivo.setFill(Color.ORANGE);
        PoloNegativo.setFill(Color.BLUE);
    }
    public ImageView getImage() {
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setX(700);
        imageView.setY(250);
        return imageView;
    }

    public Group getPolos() {return Polos;}
}