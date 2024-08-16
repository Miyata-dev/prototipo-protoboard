package com.example.prototipo;

import java.util.Iterator;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public class Utils {
    public Utils() {
    }

    public static void paintCircles(GridPane grid, int columnToPaint, int state) {
        Iterator circleToPaint = grid.getChildren().iterator();

        while(circleToPaint.hasNext()) {
            Node circle = (Node) circleToPaint.next();
            String targetID = circle.getId();
            ID temporaryID = new ID(targetID);
            CustomCircle targetedCircle = (CustomCircle) circle;
            if (ID.isThisColumn(temporaryID, columnToPaint)) {
                targetedCircle.setState(state);
            }
        }
    }
}