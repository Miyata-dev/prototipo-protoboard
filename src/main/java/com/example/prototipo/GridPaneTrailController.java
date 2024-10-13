package com.example.prototipo;


import javafx.scene.layout.GridPane;

import java.util.ArrayList;


public class GridPaneTrailController {
    final private static String CSS_CLASS = "custom-content";
    private GridPane gridPane;
    private String name;
    private ArrayList<CustomCircle> circles = new ArrayList<>();

    public GridPaneTrailController(GridPane gridPane, String name) {
        this.gridPane = gridPane;

        this.name = name;
        fillGridPaneWithCircles();
    }
    //toma un gridPane y agrega circulos con la clase CustomCircle.
    private void fillGridPaneWithCircles() {
        for (int i = 0; i < gridPane.getRowCount(); i++) { //row
            for (int j = 0; j < gridPane.getColumnCount(); j++) { //column
                ID temporaryID = new ID(i, j, name); // (column, row)
                CustomCircle circle = new CustomCircle(7, temporaryID, 0);

                circle.getStyleClass().add(CSS_CLASS);

                circle.setId(temporaryID.getGeneratedID());
                gridPane.add(circle, j, i); //(column, row)
                circles.add(circle);
            }
        }
    }

    //getters
    public GridPane getGridPane() {
        return gridPane;
    }
    public ArrayList<CustomCircle> getCircles() {
        return circles;
    }
    public String getName() {
        return name;
    }
}