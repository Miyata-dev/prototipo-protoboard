package com.example.prototipo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class MainController {
    @FXML
    public GridPane Matriz1;
    public GridPane Matriz2;
    public GridPane MatrizCarga2;
    public GridPane MatrizCarga1;
    public AnchorPane parent;
    public GridPaneTrailController matrizCirculosUnoController;
    public GridPaneTrailController matrizCirculosDosController;
    public GridPaneController matrizCargaUno;
    public GridPaneController matrizCargaDos;
    public Basurero basurero = new Basurero(
            new Image(getClass().getResource("basurero.png").toExternalForm()),
            new Label("Activar")
    );
    GridPaneObserver gridPaneObserver;
    //se coloca esta variable en el controlador par poder darle acceso los switches y leds.
    public ArrayList<Cable> cables = new ArrayList<>();
    boolean isProtoboardActive = false;
    public ArrayList<LED> leds = new ArrayList<>();
    public ArrayList<Switch> switches = new ArrayList<>();
    public Bateria bateria;
    public void initialize() {
        //nombres para cada grid para los identificadores unicos.
        String[] gridNames = {
                "gridTrail1",
                "gridTrail2",
                "gridVolt1",
                "gridVolt2",
        };

        matrizCirculosUnoController = new GridPaneTrailController(Matriz1,gridNames[0]);
        matrizCirculosDosController = new GridPaneTrailController(Matriz2,gridNames[1]);
        matrizCargaUno = new GridPaneController(MatrizCarga1, gridNames[2]);
        matrizCargaDos = new GridPaneController(MatrizCarga2, gridNames[3]);

        gridPaneObserver = new GridPaneObserver(
            new GridPaneTrailController(Matriz1,gridNames[0]),
            new GridPaneTrailController(Matriz2,gridNames[1]),
            new GridPaneController(MatrizCarga1, gridNames[2]),
            new GridPaneController(MatrizCarga2, gridNames[3]),
            parent
        );

        this.bateria = new Bateria(new Image(getClass().getResource("bateria.png").toExternalForm()));

        parent.getChildren().addAll(basurero, basurero.getLabel(), bateria.getImage(), bateria.getPolos());
        ClickLine clickLineMatrizUno = new ClickLine(
            parent,
            gridPaneObserver,
            basurero,
            bateria,
            cables,
                leds,
                switches
        );
        clickLineMatrizUno.CircleAsignator();
        System.out.println("controller: " + basurero.getParent());
    }

    public void crearLed() {
        CustomShape customShape = new CustomShape(720, 504, 25, 15, Color.YELLOW, "LED");
        LED led = new LED(false, customShape, basurero, parent, gridPaneObserver);
        parent.getChildren().add(led);
    }

    public void CreateSwitch(){
        CustomShape customShape = new CustomShape(720, 554, 30, 30, Color.WHITE, "Switch");
        Switch switch1 = new Switch(true, customShape, gridPaneObserver, basurero, parent, cables);
        parent.getChildren().add(switch1);
    }
    //in mainController
    public void createChip() {
        CustomShape customShape = new CustomShape(720, 554, 70, 34, Color.BLACK, "CHIP");
        Chip chip = new Chip(customShape, basurero, parent);
        parent.getChildren().add(chip);
    }

    public void toggleProtoBoard(ActionEvent a) {
        gridPaneObserver.toggleObserver();

        Button button = (Button) a.getSource();

        if (!gridPaneObserver.getIsEnergyActivated()) {
            System.out.println("apagando Protoboard");
            this.bateria.setNegativePole(0);
            this.bateria.setPositive(0);
            gridPaneObserver.deactivateGridObserver();
            button.setText("Encender protoboard");
        } else {
            System.out.println("prendiendo protoboard");
            this.bateria.setNegativePole(-1);
            this.bateria.setPositive(1);
            gridPaneObserver.activateGridObserver();
            button.setText("Apagar protoboard");
        }
        System.out.println("state: " + gridPaneObserver.getIsEnergyActivated());
    }

}
