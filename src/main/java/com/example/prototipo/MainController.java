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
import javafx.stage.Stage;


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
    private Stage stage;
    public Runnable getCoordsFromScreen; //esta funcion se obtiene después de configurar el ledComboBox.

    //setea la propiedad stage para poder agregar los eventos de la pantalla (cuando cambian de tamaño)
    public void setStage(Stage stage) {
        this.stage = stage;

        stage.xProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Posición X cambiada: " + newValue);
            getCoordsFromScreen.run();
        });

        // Listener para detectar cambios en la posición Y
        stage.yProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Posición Y cambiada: " + newValue);
            getCoordsFromScreen.run();
        });

        // Now you can use the stage, for example, to listen for window size changes
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            getCoordsFromScreen.run();
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            getCoordsFromScreen.run();
        });
    }

    ObservableList<String> options =
            FXCollections.observableArrayList(
                    "Chip AND",
                    "Chip OR",
                    "Chip NOT"
            );

    ObservableList<String> optionsForLed =
            FXCollections.observableArrayList(
                    "BLUE",
                    "GREEN",
                    "ORANGE",
                    "WHITE",
                    "YELLOW"
            );


    public ComboBox<String> comboBox = new ComboBox(options);
    public ComboBox<String> ledComboBox = new ComboBox(optionsForLed);

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
                ChipAND chip = new ChipAND(customShape, basurero, gridPaneObserver);

                gridPaneObserver.addChipAND(chip);
                parent.getChildren().add(chip);
            } else if (selectedOption.equals(options.get(1))) {
                ChipOR chip = new ChipOR(customShape, basurero, gridPaneObserver);

                gridPaneObserver.addChipOR(chip);
                parent.getChildren().add(chip);
            } else if (selectedOption.equals(options.get(2))) {
                ChipNOT chip = new ChipNOT(customShape, basurero, gridPaneObserver);

                gridPaneObserver.addChipNOT(chip);
                parent.getChildren().add(chip);
            }

            // Resetear la selección del ComboBox
            Platform.runLater(() -> comboBox.getSelectionModel().clearSelection());
        });

        parent.getChildren().add(comboBox);
        parent.getChildren().add(ledComboBox);

        ledComboBox.setLayoutX(400);
        ledComboBox.setLayoutY(542);
        ledComboBox.setOnAction(e -> {
            if (ledComboBox.getSelectionModel().getSelectedItem() == null) return;

            String selectedOption = ledComboBox.getSelectionModel().getSelectedItem().toString();

            CustomShape customShape = new CustomShape(720, 504, 25, 15, Color.YELLOW, "LED");
            LED led = new LED(false, customShape, basurero, parent, gridPaneObserver)
                .setColorOption(selectedOption);

            parent.getChildren().add(led);
            // Resetear la selección del ComboBox
            Platform.runLater(() -> ledComboBox.getSelectionModel().clearSelection());
        });

        getCoordsFromScreen = Utils.getUpdateCoordsRunnable(gridPaneObserver);

        //se calculan las coordenadas de los círculos de los gridTrails
        //después de que se renderizen los nodos.
        Platform.runLater(() -> getCoordsFromScreen.run());
    }

    public void CreateSwitch(){
        Switch switch2 = new Switch(false, gridPaneObserver, parent, basurero);
        gridPaneObserver.addSwitches(switch2);
        parent.getChildren().add(switch2);
    }

    public void createDisplay() {
        CustomShape customShape = new CustomShape(720, 644, 88, 95, Color.BLACK, "CHIP");
        Display display = new Display(customShape, basurero, gridPaneObserver);
        parent.getChildren().add(display);
        gridPaneObserver.addDisplay(display);
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