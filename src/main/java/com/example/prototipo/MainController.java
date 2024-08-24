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

    public int state = -1;

    public GridPaneTrailController matrizCirculosUnoController;
    public GridPaneTrailController matrizCirculosDosController;
    public GridPaneController matrizCargaUno;
    public GridPaneController matrizCargaDos;

    public void initialize() {
        matrizCirculosUnoController = new GridPaneTrailController(Matriz1, state);
        matrizCirculosDosController = new GridPaneTrailController(Matriz2, state);
        matrizCargaUno = new GridPaneController(MatrizCarga1);
        matrizCargaDos = new GridPaneController(MatrizCarga2);

        Image bateriaImg = new Image(getClass().getResource("bateria.png").toExternalForm());
        Bateria bateria = new Bateria(bateriaImg, 1000);

        parent.getChildren().add(bateria.getImage());
        ClickLine.CreateLine(parent, Matriz1);
        ClickLine.CreateLine(parent, Matriz2);

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