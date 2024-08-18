package com.example.prototipo;

import java.util.Iterator;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public class Utils {
    public Utils() {
    }
    //pinta una columna de un color dependiendo del estado que tenga (1 ¿ positivo, -1 negativo)
    public static void paintCircles(GridPane grid, int columnToPaint, int state) {
        Iterator circleToPaint = grid.getChildren().iterator();

        while(circleToPaint.hasNext()) {
            Node circle = (Node) circleToPaint.next();
            String targetID = circle.getId();
            ID temporaryID = new ID(targetID);
            CustomCircle targetedCircle = (CustomCircle) circle;

            if (ID.isThisColumn(temporaryID, columnToPaint)) {
                System.out.println("state in paintCircles: " + state);
                targetedCircle.setState(state);
            }
        }
    }
}