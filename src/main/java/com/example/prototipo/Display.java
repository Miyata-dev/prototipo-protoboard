package com.example.prototipo;

import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;

public class Display extends Chip {
    private GridPaneObserver gridPaneObserver;
    private Basurero basurero;

    //la columnna del indice 2 no se concidera en estos hashmaps.
    private HashMap<String, ArrayList<CustomCircle>> columns = new HashMap<>(); // Caracter asociado, columna asociada al caracter.
    private HashMap<String, Rectangle> displayedLines = new HashMap<>();

    public Display(CustomShape customShape, Basurero basurero, GridPaneObserver gridPaneObserver) {
        super(customShape, basurero, gridPaneObserver, 5);
        this.gridPaneObserver = gridPaneObserver;
        this.basurero = basurero;

        Runnable removeAffectedCols = () -> {
            System.out.println("number of ghostCables " + getGhostCables().size());
        };

        this.setOnMouseClicked(e -> {
            if (super.getAffectedColumns() == null) return;

            super.mouseClicked(e, customShape, removeAffectedCols);
        });

        this.setOnMouseReleased(e -> {
            super.mouseReleased(e);
            super.getCols(); //se o
        });

    }

    public void addLine(boolean isHorizontalLine, CustomShape customShape) {
        Bounds parentBounds = customShape.getBoundsInParent();
        double y = parentBounds.getCenterY();
        double x = parentBounds.getCenterX();

        System.out.println();
        Rectangle line = new Rectangle(x, y, 50, 5);

        line.setFill(Color.WHITE);
        this.getChildren().add(line);
    }

}