
package com.example.prototipo;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.function.Consumer;

public class Basurero extends ImageView {
    private Image image;
    private Label label;
    private boolean isActive;

    public Basurero(Image image, Label label) {
        this.image = image;
        this.label = label;
        this.isActive = false;
        init();
    }

    public void init() {
        this.setImage(image);
        this.setFitHeight(50);
        this.setFitWidth(50);
        this.setId("basurero");

        label.setId("basureroLabel");
        label.setTranslateY(60);

        //Lo que hacemos es Mostrar un mensaje de alerta que muestra cuando el Basurero esta activo o Desactivado al Usuario
        Popup popup = new Popup(
                "Haz dado click al basurero",
                "Al darle click",
                "Este es el contenido del mensaje de alerta"
        );

        this.setOnMouseClicked(e -> {
            if (!isActive) {
                popup.setHeader("Haz activado el basurero");
                popup.setContent("Para desactivarlo debes darle click de nuevo");
                label.setText("Desactivar");
                label.setTextFill(Color.BLACK);
            } else {
                popup.setHeader("Haz desactivado el basurero");
                popup.setContent("Para activarlo debes darle click de nuevo");
                label.setText("Activar");
                label.setTextFill(Color.BLACK);
            }
            popup.show();
            this.isActive = !this.isActive;
        });
    }

    //Este metodo lo que hace es eliminar los cables que pertenecen a un elemento del Protoboard que puede ser un Switch o un LED
    public void EliminateElements(CustomShape customShape, MouseEvent e, AnchorPane root, GridPaneObserver gridPaneObserver){
        Node node = (Node) e.getTarget();

        String resistenciaPrefix = "resistencia";

        Consumer<Resistencia> deleteResistenciaFromGridPane = (resistencia) -> {
            Utils.ResetStateCustomCircles(resistencia);
            gridPaneObserver.removeResistencia(resistencia);
            gridPaneObserver.removeCable(resistencia);
            root.getChildren().remove(resistencia.getRec());
            root.getChildren().remove(resistencia.getArrow());
            root.getChildren().remove(resistencia);
        };

        if (customShape.getLeg2().hasCable()) {
            Cable cableToRemove = customShape.getLeg2().getCable();
            //se mira que no sea una resistencia.
            if (cableToRemove.getTipo() == null) {
                //Se realiza un eliminacion si el elemento es un cable y ademas las ID son iguales al cable a remover del Elemento Correspondiente
                root.getChildren().removeIf(element -> {
                    Utils.ResetStateCustomCircles(cableToRemove);
                    return element instanceof Cable && ((Cable) element).getRandomID().equals(cableToRemove.getRandomID());
                });
                //Despues de eliminar el cable del elemento los eliminamos tambien de la colección del griPaneObserver
                gridPaneObserver.removeCable(cableToRemove);
            } else if (cableToRemove.getTipo().equals(resistenciaPrefix)) {
                //busca una resistencia que tenga la misma id random que el cable que estamos mirando en la iteración.
                Resistencia founded = gridPaneObserver.getResistencias().stream()
                    .filter(el -> el.getRandomID().equals(cableToRemove.getRandomID()))
                    .findAny()
                    .orElse(null);

                deleteResistenciaFromGridPane.accept(founded);
            }
        }
        if (customShape.getLeg1().hasCable()) {
            Cable cableToRemove = customShape.getLeg1().getCable();
            //se mira que no sea una resistencia.
            if (cableToRemove.getTipo() == null) {
                //Se realiza un eliminacion si el elemento es un cable y ademas las ID son iguales al cable a remover del Elemento Correspondiente
                root.getChildren().removeIf(element -> {
                    Utils.ResetStateCustomCircles(cableToRemove);
                    return element instanceof Cable && ((Cable) element).getRandomID().equals(cableToRemove.getRandomID());
                });
                //Despues de eliminar el cable del elemento los eliminamos tambien de la colección del griPaneObserver
                gridPaneObserver.removeCable(cableToRemove);
            } else if (cableToRemove.getTipo().equals(resistenciaPrefix)) {
                Resistencia founded = gridPaneObserver.getResistencias().stream()
                        .filter(el -> el.getRandomID().equals(cableToRemove.getRandomID()))
                        .findAny()
                        .orElse(null);

                deleteResistenciaFromGridPane.accept(founded);
            }
        }

        gridPaneObserver.getCables().forEach(cable -> System.out.println(cable));
        System.out.println("numero de resistencias: " + gridPaneObserver.getResistencias().size());
    }


    public boolean getIsActive() {
        return isActive;
    }

    public Label getLabel() {
        return label;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
}
