package com.example.prototipo;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;


import java.util.ArrayList;
import java.util.function.Consumer;

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
    public Basurero basurero;
    GridPaneObserver gridPaneObserver;
    //se coloca esta variable en el controlador par poder darle acceso los switches y leds.
    public ArrayList<Cable> cables = new ArrayList<>();
    boolean isProtoboardActive = false;
    public ArrayList<LED> leds = new ArrayList<>();
    public ArrayList<Switch> switches = new ArrayList<>();
    public Bateria bateria;
    public ClickLine clickLineMatrizUno;

    ObservableList<String> options =
            FXCollections.observableArrayList(
                    "Chip AND",
                    "Chip OR",
                    "Chip NOT"
            );

    public ComboBox<String> comboBox = new ComboBox(options);

    public void initialize() {

        //Creamos un label para poder cambiarle el color al texto a NEGRO
        Label label = new Label("Activar");
        label.setStyle("-fx-text-fill: black;");
        this.basurero = new Basurero(
                new Image(getClass().getResource("basurero.png").toExternalForm()),
                label
        );
        //nombres para cada grid para los identificadores unicos.
        String[] gridNames = {
                "gridTrail1",
                "gridTrail2",
                "gridVolt1",
                "gridVolt2",
        };

        ImageView motor = new ImageView();
        motor.setImage(new Image(getClass().getResource("motor.png").toExternalForm()));
        motor.setFitHeight(50);
        motor.setFitWidth(50);
        motor.setX(790);
        motor.setY(280);
        motor.setOnMouseClicked(e -> {
            gridPaneObserver.toggleObserver();

            if (!gridPaneObserver.getIsEnergyActivated()) {
                System.out.println("apagando Protoboard");
                this.bateria.setNegativePole(0);
                this.bateria.setPositive(0);
                gridPaneObserver.deactivateGridObserver();
            } else {
                System.out.println("prendiendo protoboard");
                this.bateria.setNegativePole(-1);
                this.bateria.setPositive(1);
                gridPaneObserver.activateGridObserver();
            }
            System.out.println("state: " + gridPaneObserver.getIsEnergyActivated());
        });

        this.bateria = new Bateria(new Image(getClass().getResource("bateria.png").toExternalForm()));
        matrizCirculosUnoController = new GridPaneTrailController(Matriz1,gridNames[0]);
        matrizCirculosDosController = new GridPaneTrailController(Matriz2,gridNames[1]);
        matrizCargaUno = new GridPaneController(MatrizCarga1, gridNames[2]);
        matrizCargaDos = new GridPaneController(MatrizCarga2, gridNames[3]);

        gridPaneObserver = new GridPaneObserver(
                new GridPaneTrailController(Matriz1,gridNames[0]),
                new GridPaneTrailController(Matriz2,gridNames[1]),
                new GridPaneController(MatrizCarga1, gridNames[2]),
                new GridPaneController(MatrizCarga2, gridNames[3]),
                bateria,
                parent
        );


        parent.getChildren().addAll(basurero, basurero.getLabel(), bateria.getImage(), bateria.getPolos(), motor);
        clickLineMatrizUno = new ClickLine(
                parent,
                gridPaneObserver,
                basurero,
                bateria,
                cables,
                leds,
                switches
        );
        clickLineMatrizUno.CircleAsignator();

        ObservableList<String> options = comboBox.getItems(); //obtiene las opciones de la lista

        comboBox.setLayoutX(720);
        comboBox.setLayoutY(500);

        comboBox.setOnAction(e -> {
            if (comboBox.getSelectionModel().getSelectedItem() == null) return;

            String selectedOption = comboBox.getSelectionModel().getSelectedItem().toString();

            CustomShape customShape = new CustomShape(720, 554, 125, 34, Color.BLACK, "CHIP");

            if (selectedOption.equals(options.get(0))) {
                System.out.println("creating chip and...");
                ChipAND chip = new ChipAND(customShape, basurero, gridPaneObserver);

                gridPaneObserver.addChipAND(chip);
                parent.getChildren().add(chip);
            } else if (selectedOption.equals(options.get(1))) {
                System.out.println("creating chip or...");
            } else if (selectedOption.equals(options.get(2))) {
                System.out.println("creating chip NOT...");
            }

            // Resetear la selección del ComboBox
            Platform.runLater(() -> comboBox.getSelectionModel().clearSelection());
        });

        parent.getChildren().add(comboBox);

        //se calculan las coordenadas de los círculos de los gridTrails
        //después de que se renderizen los nodos.
        Platform.runLater(() -> {
            //toma un gridTrial y le setea las coordenadas a los circulos uno por uno.
            Consumer<GridPane> addCoords = (gridPane) -> {
                gridPane.getChildren().forEach(child -> {
                    if (child instanceof CustomCircle circ) {
                        Bounds boundsInScene = circ.localToScreen(circ.getBoundsInLocal());
                        double x = boundsInScene.getCenterX();
                        double y = boundsInScene.getCenterY();

                        circ.setCoords(x, y);
                    }
                });
            };
            System.out.println("size of circle: " + matrizCirculosUnoController.getCircles().get(0).getRadius());
            // Código para actualizar la UIA
            addCoords.accept(matrizCirculosUnoController.getGridPane());
            addCoords.accept(matrizCirculosDosController.getGridPane());
            addCoords.accept(matrizCargaUno.getGridPane());
            addCoords.accept(matrizCargaDos.getGridPane());
        });
    }

    public void crearLed() {
        CustomShape customShape = new CustomShape(720, 504, 25, 15, Color.YELLOW, "LED");
        LED led = new LED(false, customShape, basurero, parent, gridPaneObserver);
        parent.getChildren().add(led);
    }

    public void CreateSwitch(){
        Switch switch2 = new Switch(false, gridPaneObserver, parent, basurero);
        gridPaneObserver.addSwitches(switch2);
        parent.getChildren().add(switch2);
    }
    //in mainController
    public void createChip() {
        CustomShape customShape = new CustomShape(720, 554, 125, 34, Color.BLACK, "CHIP");
        Chip chip = new Chip(customShape, basurero, gridPaneObserver);
        parent.getChildren().add(chip);
    }

    public void setModoResistencia(ActionEvent event) {

        Button button = (Button) event.getSource();
        if(clickLineMatrizUno.getResistenciaMode()){
            button.setText("Activar resistencia");
            clickLineMatrizUno.setisResistenciaModeActive(false);
        }else{
            button.setText("Desactivar resistencia");
            clickLineMatrizUno.setisResistenciaModeActive(true);
        }
    }

}