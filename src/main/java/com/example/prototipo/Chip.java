package com.example.prototipo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Chip extends Group {
    private Rectangle[] LEGS;


    public Chip(CustomShape customShape) {
        super(customShape);
        addPatitas(customShape);
        this.setTranslateY(-100);

    }

    public void addPatitas(CustomShape customShape) {
        System.out.println("width: " + customShape.getWidth() + ", height: " + customShape.getHeight());

        double x = customShape.getLayoutX();
        double y = customShape.getLayoutY();
        double width = customShape.getWidth() / 4;
        Rectangle leg1 = new Rectangle(100 , 5);
        leg1.setX(x - width);
        leg1.setY(y + 15);
        leg1.setFill(Color.RED);
        Rectangle leg2 = new Rectangle(100 , 5);
        leg2.setX(x - (width * 2));
        leg2.setY(y + 15);
        leg2.setFill(Color.BLUE);
        Rectangle leg3 = new Rectangle(10 , 5);
        leg3.setX(x - (width * 3));
        leg3.setY(y + 15);
        leg3.setFill(Color.GREEN);
        Rectangle leg4 = new Rectangle(10 , 5);
        leg4.setX(x - (width * 4));
        leg4.setY(y + 15);
        leg4.setFill(Color.BLACK);

        this.LEGS = new Rectangle[]{leg1, leg2, leg3, leg4};
        this.getChildren().add(leg1);
        this.getChildren().add(leg2);
        this.getChildren().add(leg3);
        this.getChildren().add(leg4);

//        Rectangle leg5 = new Rectangle(10 , 5);
//
//        Rectangle leg6 = new Rectangle(10 , 5);
//        Rectangle leg7 = new Rectangle(10 , 5);
//        Rectangle leg8 = new Rectangle(10 , 5);
//

    }



}