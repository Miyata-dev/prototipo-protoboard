package com.example.prototipo;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class Bateria {
    private Image image;
    private CustomCircle NegativePole, PositivePole;
    private Group Polos;

    public Bateria(Image image) {
        this.image = image;

        ID IdForNegative = new ID(2,1,"BateryVolt");
        ID IdForPositive = new ID(1,1,"BateryVolt");
        IdForNegative.setIsForGridpane(false);
        IdForPositive.setIsForGridpane(false);

        NegativePole = new CustomCircle(5,IdForNegative,-1);
        PositivePole = new CustomCircle(5,IdForPositive,1);

        Polos = new Group(this.NegativePole,this.PositivePole);

        this.NegativePole.setCenterX(750);
        this.NegativePole.setCenterY(350);

        this.PositivePole.setCenterX(750);
        this.PositivePole.setCenterY(250);

        PositivePole.setFill(Color.ORANGE);
        NegativePole.setFill(Color.BLUE);
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