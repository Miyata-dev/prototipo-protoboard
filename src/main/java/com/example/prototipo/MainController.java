package com.example.prototipo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class MainController {
    @FXML
    public GridPane Matriz1;
    public GridPane Matriz2;
    public GridPane MatrizCarga2;
    public GridPane MatrizCarga1;
    public AnchorPane parent;

    public int state = -1; //tiene q ser estado local de cada matriz de alimentación.

    public GridPaneTrailController matrizCirculosUnoController;
    public GridPaneTrailController matrizCirculosDosController;
    public GridPaneController matrizCargaUno;
    public GridPaneController matrizCargaDos;
    public Basurero basurero = new Basurero(
            new Image(getClass().getResource("basurero.png").toExternalForm()),
            new Label("Activar")
    );

    public void initialize() {
        //nombres para cada grid para los identificadores unicos.
        String[] gridNames = {
                "gridTrail1",
                "gridTrail2",
                "gridVolt1",
                "gridVolt2",
        };

        matrizCirculosUnoController = new GridPaneTrailController(Matriz1, state, gridNames[0]);
        matrizCirculosDosController = new GridPaneTrailController(Matriz2, state, gridNames[1]);
        matrizCargaUno = new GridPaneController(MatrizCarga1, gridNames[2]);
        matrizCargaDos = new GridPaneController(MatrizCarga2, gridNames[3]);

        Bateria bateria = new Bateria(new Image(getClass().getResource("bateria.png").toExternalForm()));

        parent.getChildren().addAll(basurero, basurero.getLabel(), bateria.getImage(), bateria.getPolos());
        ClickLine clickLineMatrizUno = new ClickLine(parent, matrizCirculosUnoController, matrizCirculosDosController,matrizCargaUno,matrizCargaDos, basurero);
        clickLineMatrizUno.CircleAsignator();
        System.out.println("controller: " + basurero.getParent());
    }

    public void crearLed() {
        CustomShape customShape = new CustomShape(720, 504, 25, 15, Color.YELLOW);
        LED led = new LED(false, customShape, basurero);
        parent.getChildren().add(led);
    }

    public void CreateSwitch(){
        CustomShape customShape = new CustomShape(720, 554, 30, 30, Color.WHITE);

        Switch switch1 = new Switch(false, customShape);
        parent.getChildren().add(switch1);
    }

}