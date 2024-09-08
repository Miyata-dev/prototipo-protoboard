
package com.example.prototipo;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

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
            } else {
                popup.setHeader("Haz desactivado el basurero");
                popup.setContent("Para activarlo debes darle click de nuevo");
                label.setText("Activar");
            }
            popup.show();
            this.isActive = !this.isActive;
        });
    }

    public void EliminateElements(CustomShape customShape, MouseEvent e, AnchorPane root){
        System.out.println("this: " + this);

        System.out.println(customShape.getLeg2().getCable() + " has cable: " + customShape.getLeg2().hasCable());
        System.out.println(customShape.getLeg1().getCable() + " has cable: " + customShape.getLeg1().hasCable());

        Node node = (Node) e.getTarget();

        System.out.println(node);

        if (customShape.getLeg2().hasCable()) {
            System.out.println("im here");
            Cable cableToRemove = customShape.getLeg2().getCable();
            System.out.println(cableToRemove.getRandomID());

            System.out.println(node.getParent().getParent());
            root.getChildren().removeIf(element -> {
                return element instanceof Cable && ((Cable) element).getRandomID().equals(cableToRemove.getRandomID());
            });
        }

        if (customShape.getLeg1().hasCable()) {
            System.out.println("im here");
            Cable cableToRemove = customShape.getLeg1().getCable();
            System.out.println(cableToRemove.getRandomID());
            System.out.println("hola" + node.getParent());

            root.getChildren().removeIf(element -> {
                return element instanceof Cable && ((Cable) element).getRandomID().equals(cableToRemove.getRandomID());
            });
        }

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
