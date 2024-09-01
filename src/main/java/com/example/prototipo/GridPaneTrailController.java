package com.example.prototipo;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;

public class GridPaneTrailController {
    final private static String CSS_CLASS = "custom-content";
    private GridPane gridPane;
    private String name;
    public int stateToUse; //controlla el flujo de energia que se pasa en los trails.

    public GridPaneTrailController(int rows, int columns, int stateToUse, String name) {
        this.gridPane = createGridPane(rows, columns);
        this.stateToUse = stateToUse;
        this.name = name;
    }

    public GridPaneTrailController(GridPane gridPane, int stateToUse, String name) {
        this.gridPane = gridPane;
        this.stateToUse = stateToUse;
        this.name = name;
        fillGridPaneWithCircles();
    }
    //TODO pasarlo a no estatico.
    public static void setStateToUse(GridPaneTrailController gridpane, int stateToUse) {
        System.out.println("Changing state to " + stateToUse);
        gridpane.stateToUse = stateToUse;
    }

    //to improve readability
    private GridPane createGridPane(int rows, int columns) {
        GridPane grid = new GridPane();

        for (int i = 0; i < rows; i++) { //row
            for (int j = 0; j < columns; j++) { //column
                ID temporaryID = new ID(i, j, name); // (column, row)
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
                ID temporaryID = new ID(i, j, name); // (column, row)
                CustomCircle circle = new CustomCircle(7, temporaryID, 0);

                circle.getStyleClass().add(CSS_CLASS);

                circle.setId(temporaryID.getGeneratedID());
                gridPane.add(circle, j, i); //(column, row)
            }
        }
    }

    //getters
    public GridPane getGridPane() {
        return gridPane;
    }

    public String getName() {
        return name;
    }
}