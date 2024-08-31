
package com.example.prototipo;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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

    public boolean getIsActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        this.isActive = active;
    }
}
