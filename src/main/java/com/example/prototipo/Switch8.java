package com.example.prototipo;

import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Switch8 extends Chip{

    private GridPaneObserver gridPaneObserver;
    private Basurero basurero;
    private ArrayList<Pair<Boolean,Rectangle>> activatedButtons = new ArrayList<>(); // True = pasando energia, false = no pasando energia
    private ArrayList<Pair<Boolean,Rectangle>> burnedButtons = new ArrayList<>(); // True = quemado, false = no quemado

    public Switch8(CustomShape customShape, Basurero basurero, GridPaneObserver gridPaneObserver) {
        super(customShape, basurero, gridPaneObserver, 8);
        createButtons(customShape);

        activatedButtons.forEach(pair->{
            pair.getSecondValue().setOnMouseClicked(mouseEvent -> {
                System.out.println("CLICKEANDO");
                pair.setFirstValue(!pair.getFirstValue());
                System.out.println(pair.getFirstValue());

                if(pair.getFirstValue()){
                    pair.getSecondValue().setFill(Color.GRAY);
                    pair.getSecondValue().setY(pair.getSecondValue().getY()-2);
                }else{
                    pair.getSecondValue().setFill(Color.WHITE);
                    pair.getSecondValue().setY(pair.getSecondValue().getY()+2);
                }

            });
        });

    }

    private void createButtons(CustomShape customShape) {
        Bounds parentBounds = customShape.getBoundsInParent();
        double x = parentBounds.getMinX() + 6;
        double y = parentBounds.getMinY();

        for (int i = 0 ; i < 8 ; i++) {
            Rectangle rectangle = new Rectangle(x , (y + 10), 8, 20);
            rectangle.setFill(Color.WHITE);
            x = x + 18;
            activatedButtons.add(new Pair<>(false, rectangle));
            this.getChildren().add(rectangle);
        }

    }

    public void checkColumns(){
        List<ArrayList<CustomCircle>> lowerCols = super.getLowerCols();
        List<ArrayList<CustomCircle>> upperCols = super.getUpperCols();
    }
}
