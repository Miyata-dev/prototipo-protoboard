package com.example.prototipo;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GridPaneController {
    private GridPane gridPane;
    //private CustomCircle[][] customCircles; //esto es para monitorear

    public GridPaneController(int rows, int columns) {
        this.gridPane = createGridPane(rows, columns);
        addClickEvent();
    }
    //to improve readability
    private static GridPane createGridPane(int rows, int columns) {
        GridPane grid = new GridPane();

        for (int i = 0; i < rows; i++) { //row
            for (int j = 0; j < columns; j++) { //column
                ID temporaryID = new ID(i, j); // (column, row)
                CustomCircle circle = new CustomCircle(10, Color.RED, temporaryID, 0);
                circle.setId(temporaryID.getGeneratedID());
                grid.add(circle, j, i); //(column, row)
            }
        }
        return grid;
    }
    //Agregamos el onMouseClicked a todos los circulos.
    private void addClickEvent() {
        for (Node circle : gridPane.getChildren()) {
            String targetID  = circle.getId();
            Circle targetedCircle = (Circle) circle;

            targetedCircle.setOnMouseClicked(e -> {
                CustomCircle circleClicked = (CustomCircle) e.getTarget();
                ID circledClikedID = new ID(circleClicked.getId());
                int indexColumn = circledClikedID.getIndexColumn();

                //Utils.paintCircles(gridPane, indexColumn, 1);
                //circleClicked.setState(-1);
                System.out.println(circledClikedID.getGeneratedID() + " state: " + circleClicked.getState());
            });
            //System.out.println(circle.getId() + " " + circle.getId().getClass());
        }
    }

    //getters
    public GridPane getGridPane() {
        return gridPane;
    }
}
