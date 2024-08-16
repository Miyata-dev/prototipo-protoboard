package com.example.prototipo;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;

public class GridPaneTrailController {
    final private static String CSS_CLASS = "custom-content";
    private GridPane gridPane;
    public int stateToUse; //controlla el flujo de energia que se pasa en los trails.
    //private CustomCircle[][] customCircles; //esto es para monitorear

    public GridPaneTrailController(int rows, int columns, int stateToUse) {
        this.gridPane = createGridPane(rows, columns);
        this.stateToUse = stateToUse;
        addClickEvent();
    }

    public GridPaneTrailController(GridPane gridPane, int stateToUse) {
        this.gridPane = gridPane;
        this.stateToUse = stateToUse;
        fillGridPaneWithCircles();
        addClickEvent();
    }

    public static void setStateToUse(GridPaneTrailController gridpane, int stateToUse) {
        System.out.println("Changing state to " + stateToUse);
        gridpane.stateToUse = stateToUse;
    }

    //to improve readability
    private static GridPane createGridPane(int rows, int columns) {
        GridPane grid = new GridPane();

        for (int i = 0; i < rows; i++) { //row
            for (int j = 0; j < columns; j++) { //column
                ID temporaryID = new ID(i, j); // (column, row)
                CustomCircle circle = new CustomCircle(7, temporaryID, 0);

                circle.getStyleClass().add(CSS_CLASS);

                circle.setId(temporaryID.getGeneratedID());
                grid.add(circle, j, i); //(column, row)
            }
        }
        return grid;
    }
    //toma un gridPane y agrega circulos con la clase CustomCircle.
    private void fillGridPaneWithCircles() {
        for (int i = 0; i < gridPane.getRowCount(); i++) { //row
            for (int j = 0; j < gridPane.getColumnCount(); j++) { //column
                ID temporaryID = new ID(i, j); // (column, row)
                CustomCircle circle = new CustomCircle(7, temporaryID, 0);

                circle.getStyleClass().add(CSS_CLASS);

                circle.setId(temporaryID.getGeneratedID());
                gridPane.add(circle, j, i); //(column, row)
            }
        }
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

                Utils.paintCircles(gridPane, indexColumn, stateToUse);
                System.out.println(circledClikedID.getGeneratedID() + " state: " + circleClicked.getState());
            });
        }
    }

    //getters
    public GridPane getGridPane() {
        return gridPane;
    }
}