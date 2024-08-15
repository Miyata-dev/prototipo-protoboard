package com.example.prototipo;

import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;

public class MainController {

    public GridPane Matriz1;
    public GridPane Matriz2;
    public GridPane MatrizCarga2;
    public GridPane MatrizCarga1;


    //en este metodo activara el uso de los cables del protoboard.
    public void toggleUsage() {
        System.out.println("Activando uso de protoboard");
    }
}