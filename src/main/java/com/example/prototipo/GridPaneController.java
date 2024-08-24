package com.example.prototipo;

import javafx.scene.layout.GridPane;

public class    GridPaneController {
    private final String CSS_CLASS = "volt";
    private GridPane gridPane;
    private String name;
    //private CustomCircle[][] customCircles; //esto es para monitorear

    public GridPaneController(int rows, int columns) {
        this.gridPane = createGridPane(rows, columns);
    }

    public GridPaneController(GridPane gridPane, String name) {
        this.gridPane = gridPane;
        this.name = name;
        fillGridPaneWithCircles();
    }

    //to improve readability
    private GridPane createGridPane(int rows, int columns) {
        GridPane grid = new GridPane();

        for (int i = 0; i < rows; i++) { //row
            for (int j = 0; j < columns; j++) { //column
                ID temporaryID = new ID(i, j, name); // (column, row)
                CustomCircle circle = new CustomCircle(10, temporaryID, 0);
                circle.setId(temporaryID.getGeneratedID());
                grid.add(circle, j, i); //(column, row)
            }
        }
        return grid;
    }

    //agrega energia positiva o negativa dependiendo del numero de fila.
    private void fillGridPaneWithCircles() {
        for (int i = 0; i < gridPane.getRowCount(); i++) { //row
            for (int j = 0; j < gridPane.getColumnCount(); j++) { //column
                ID temporaryID = new ID(i, j, name); // (column, row)
                CustomCircle circle = new CustomCircle(8, temporaryID, 0);

                circle.setId(temporaryID.getGeneratedID());
                circle.getStyleClass().add(CSS_CLASS);
                //la fila es negativa
                if (i == 0) {
                    circle.setState(-1);
                } else if (i == 1) {
                    circle.setState(1);
                }

                gridPane.add(circle, j, i); //(column, row)
            }
        }
    }

    //getters
    public GridPane getGridPane() {
        return gridPane;
    }
}
