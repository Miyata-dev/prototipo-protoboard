package com.example.prototipo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

        Image bateriaImg = new Image(getClass().getResource("bateria.png").toExternalForm());
        Image LEDImg = new Image(getClass().getResource("LED.png").toExternalForm());
        Image CablesImg = new Image(getClass().getResource("Cables.png").toExternalForm());
        Image SwitchImg = new Image(getClass().getResource("Switch.png").toExternalForm());

        LED led = new LED( false,new Punto(0,0),new Punto(0,0),LEDImg);
        Bateria bateria = new Bateria(bateriaImg, 1000);
        Cable cable = new Cable(0,new Punto(0,0),new Punto(0,0),CablesImg);
        Switch switchC = new Switch(false,new Punto(0,0),new Punto(0,0),SwitchImg);


        parent.getChildren().add(switchC.getImage());
        parent.getChildren().add(cable.getImage());
        parent.getChildren().add(led.getImage());
        parent.getChildren().add(bateria.getImage());
        ClickLine.CircleAsignator(parent, Matriz1);
        ClickLine.CircleAsignator(parent, Matriz2);

    }

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