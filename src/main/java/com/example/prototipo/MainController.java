package com.example.prototipo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class MainController {
    @FXML
    public GridPane Matriz1;
    public GridPane Matriz2;
    public GridPane MatrizCarga2;
    public GridPane MatrizCarga1;
    public Button energiaPositivaBtn;
    public Button energiaNegativaBtn;
    public AnchorPane parent;

    public int state = -1; //tiene q ser estado local de cada matriz de alimentación.

    public GridPaneTrailController matrizCirculosUnoController;
    public GridPaneTrailController matrizCirculosDosController;
    public GridPaneController matrizCargaUno;
    public GridPaneController matrizCargaDos;

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

        ClickLine clickLineMatrizUno = new ClickLine(parent, matrizCirculosUnoController, matrizCirculosDosController);
        clickLineMatrizUno.CircleAsignator();

        Label basureroLabel = new Label("Activar");
        Basurero basurero = new Basurero(
                new Image(getClass().getResource("basurero.png").toExternalForm()),
                basureroLabel
        );
        parent.getChildren().addAll(basurero, basureroLabel);

        System.out.println("controller: " + basurero.getParent());
    }

    public void crearLed() {
        LED led = new LED(false);
        parent.getChildren().add(led.getRectangle());
    }
    public void CreateSwitch(){
        Switch switch1 = new Switch(false);
        parent.getChildren().add(switch1.GetSquare());
    }

    //TODO eliminar los ultimos tres metodos.
    public void usarEnergiaNegativa() {
        GridPaneTrailController.setStateToUse(matrizCirculosUnoController, -1);
        GridPaneTrailController.setStateToUse(matrizCirculosDosController, -1);
        System.out.println("Usar energia negativa, state: " + state);
    }

    public void usarEnergiaPositiva() {
        GridPaneTrailController.setStateToUse(matrizCirculosUnoController, 1);
        GridPaneTrailController.setStateToUse(matrizCirculosDosController, 1);

        System.out.println("Usar energia positiva, state: " + state);
    }

    //en este metodo activara el uso de los cables del protoboard.
    public void toggleUsage() {
        System.out.println("Activando uso de protoboard");
    }
}