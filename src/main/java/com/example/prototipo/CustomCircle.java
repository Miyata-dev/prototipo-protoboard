package com.example.prototipo;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class CustomCircle extends Circle {
    private int state = 0;
    private ID id;

    public CustomCircle(int radius, Color color, ID id, int state) {
        super((double) radius, color);
        this.id = id;
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
        if (state == 1) {
            this.setFill(Color.BLUE);
        } else if (state == -1) {
            this.setFill(Color.ORANGE);
        }

    }
}