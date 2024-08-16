package com.example.prototipo;

import javafx.scene.layout.GridPane;

public class MainController {

    public GridPane Matriz1;
    public GridPane Matriz2;
    public GridPane MatrizCarga2;
    public GridPane MatrizCarga1;

    public void initialize() {
        GridPaneTrailController matrizCirculosUnoController = new GridPaneTrailController(Matriz1);
        GridPaneTrailController matrizCirculosDosController = new GridPaneTrailController(Matriz2);
        GridPaneController matrizCargaUno = new GridPaneController(MatrizCarga1);
        GridPaneController matrizCargaDos = new GridPaneController(MatrizCarga2);

        /*
        System.out.println(MatrizCarga2.getColumnCount());
        System.out.println(MatrizCarga2.getRowCount()); */
    }

    //en este metodo activara el uso de los cables del protoboard.
    public void toggleUsage() {
        System.out.println("Activando uso de protoboard");
    }
}