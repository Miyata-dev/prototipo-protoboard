package com.example.prototipo;

import javafx.scene.layout.GridPane;

public class GridPaneController {
    private final String CSS_CLASS = "custom-content";
    private GridPane gridPane;
    private String name;

    public GridPaneController(GridPane gridPane, String name) {
        this.gridPane = gridPane;
        this.name = name;
        fillGridPaneWithCircles();
    }

    //agrega energia positiva o negativa dependiendo del numero de fila.
    private void fillGridPaneWithCircles() {
        for (int i = 0; i < gridPane.getRowCount(); i++) { //row
            for (int j = 0; j < gridPane.getColumnCount(); j++) { //column
                ID temporaryID = new ID(i, j, name); // (column, row)
                CustomCircle circle = new CustomCircle(8, temporaryID, 0);

                circle.setId(temporaryID.getGeneratedID());
                circle.getStyleClass().add(CSS_CLASS);

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
