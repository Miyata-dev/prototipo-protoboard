package com.example.prototipo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class Utils {
    public Utils() {
    }
    //se obtiene una coleccion de CustomCircles a partir de la columna en comun.
    public static ArrayList<CustomCircle> getColumnOfCustomCircles(GridPane grid, int columnToGet) {
        Iterator<Node> circleToPaint = grid.getChildren().iterator();
        ArrayList<CustomCircle> circles = new ArrayList<>();

        while(circleToPaint.hasNext()) {
            Node circle = circleToPaint.next();
            String targetID = circle.getId();
            ID temporaryID = new ID(targetID);
            CustomCircle targetedCircle = (CustomCircle) circle;

            if (ID.isThisColumn(temporaryID, columnToGet)) {
                circles.add(targetedCircle);
            }
        }

        return circles;
    }

    public static ArrayList<CustomCircle> getRowOfCustomCircles(GridPane grid, int rowToGet) {
        Iterator<Node> circleToPaint = grid.getChildren().iterator();
        ArrayList<CustomCircle> circles = new ArrayList<>();

        while(circleToPaint.hasNext()) {
            Node circle = circleToPaint.next();
            String targetID = circle.getId();
            ID temporaryID = new ID(targetID);
            CustomCircle targetedCircle = (CustomCircle) circle;

            if (ID.isThisRow(temporaryID, rowToGet)) {
                circles.add(targetedCircle);
            }
        }

        return circles;
    }

    //TODO resolver bug cuando se contecta 2 cables en los grid de voltios. propuesta, debe tomar un gridpanetrail.
    //pinta una columna de un color dependiendo del estado que tenga (1 ¿ positivo, -1 negativo), pasar ID y calcular el
    // indice de columna, ver si la id es valida o no (a travez de su gridName)
    public static void paintCircles(GridPane grid, ID id, int state) {
        String[] validGridNames = {
                "gridTrail1",
                "gridTrail2"
        };
        int columnToPaint = id.getIndexColumn();
        //if (!id.getIsForGridpane()) return;
        if (!Arrays.asList(validGridNames).contains(id.getGridName())) return;
        ArrayList<CustomCircle> circles = getColumnOfCustomCircles(grid, columnToPaint);
        circles.forEach(circle -> {
            circle.setState(state);
        });
    }
    public static void paintCirclesVolt(GridPane grid, ID id, int state){
        String[] validGridNames = {
                "gridVolt1",
                "gridVolt2"
        };
        int rowToPaint = id.getIndexRow();
        if (!id.getIsForGridpane()) return;
        if (!Arrays.asList(validGridNames).contains(id.getGridName())) return;
        ArrayList<CustomCircle> circles = getRowOfCustomCircles(grid, rowToPaint);

        circles.forEach(circle -> {
            circle.setState(state);
        });

    }

    public static void unPaintCircles(GridPane grid, int columnToUnPaint) {
        ArrayList<CustomCircle> circles = getColumnOfCustomCircles(grid, columnToUnPaint);

        circles.forEach(circle -> {
            circle.setState(0);
        });
    }

    //retorna null si no encuentra el nodo.
    public static CustomCircle getCustomCircleByID(GridPane grid, ID id) {
        CustomCircle foundCircle = null;
        Iterator<Node> circleToPaint = grid.getChildren().iterator();

        while(circleToPaint.hasNext()) {
            Node circle = (Node) circleToPaint.next();
            String targetID = circle.getId();
            ID temporaryID = new ID(targetID);
            CustomCircle targetedCircle = (CustomCircle) circle;

            if (ID.isSameID(temporaryID, id)) {
                foundCircle = targetedCircle;
            }
        }

        return foundCircle;
    }
    //devuelve el numero de cables que tiene una columna.
    public static int numberOfCables(GridPane grid, int columnToInspect) {
        int count = 0;
        ArrayList<CustomCircle> circles = getColumnOfCustomCircles(grid, columnToInspect);

        for (CustomCircle circle : circles) {
            if (circle.getIsTaken()) {
                count++;
            }
        }

        return count;
    }

    public static ArrayList<Cable> getConnectedCables(ArrayList<Cable> cables, Cable cableToConnect) {
        ArrayList<Cable> connectedCables = new ArrayList<>();

        Cable previousCable = cableToConnect; //para la iteracion, queremos ver si el cable por parametro es el cable anterior.

        //TODO revisar.
        for (Cable cable : cables) {
            if (connectedCables.isEmpty()) {
                if (Cable.areConnected(cable, cableToConnect)) {
                    connectedCables.add(cable);
                }
            } else {
                //se obtiene el ultimo cable de la coleccion para poder comparar la conectivida con el siguiente.
                previousCable = connectedCables.get(connectedCables.size() - 1);

                if (Cable.areConnected(cable, previousCable)) {
                    connectedCables.add(cable);
                }
            }
        }

        return connectedCables;
    }

    public static void deleteCable(MouseEvent e, GridPaneTrailController gridOne, GridPaneTrailController gridTwo, ArrayList<Cable> cables) {
        //estos son los nombres que usa internamente las ID de los circulos pertenecientes al centro.
        String[] validGridNames = {
                "gridTrail1",
                "gridTrail2"
        };
        //si una de las ids provienen de los volts (buses), entonces se realiza una eliminación de energía en cadena.
        //en caso de que el cable sea el único proveedoor se pierde la energia.
        String[] voltNames = {
                "gridVolt1",
                "gridVolt2"
        };

        //se recupera el nodo que se preciona.
        Node pressedNode = (Node) e.getTarget();
        if (!(e.getTarget() instanceof Cable pressedCable)) return;

        //se recupera las ids del cable para poder ver donde pasar la energia.
        ID firstID = pressedCable.getIds()[0];
        ID secondID = pressedCable.getIds()[1];

        //TODO revisar.
        //si el primer ID pertenece a los volts, se extrae el Gridpane del segundo para despintarlo.
        if (Arrays.asList(voltNames).contains(firstID.getGridName())) {

            //se obtiene la coleccion de cables conectados al primer cable.
            ArrayList<Cable> connectedCables = getConnectedCables(cables, pressedCable);

            for (Cable cable : connectedCables) {
                GridPane secondCircleGridPane = null;

                if (cable.getIds()[1].getGridName().equals(gridTwo.getName())) {
                    secondCircleGridPane = gridTwo.getGridPane();
                } else if (cable.getIds()[1].getGridName().equals(gridOne.getName())) {
                    secondCircleGridPane = gridOne.getGridPane();
                }

                System.out.println(cable.getIds()[1].getGridName());

                unPaintCircles(secondCircleGridPane, cable.getIds()[1].getIndexColumn());
                unPaintCircles(secondCircleGridPane, cable.getIds()[0].getIndexColumn());
            }

            ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
            return;
        } else if (Arrays.asList(voltNames).contains(secondID.getGridName())) {

            //se obtiene la coleccion de cables conectados al primer cable.
            ArrayList<Cable> connectedCables = getConnectedCables(cables, pressedCable);

            for (Cable cable : connectedCables) {
                GridPane secondCircleGridPane = null;

                if (cable.getIds()[0].getGridName().equals(gridTwo.getName())) {
                    secondCircleGridPane = gridTwo.getGridPane();
                } else if (cable.getIds()[0].getGridName().equals(gridOne.getName())) {
                    secondCircleGridPane = gridOne.getGridPane();
                }
                System.out.println(cable.getIds()[0].getGridName());

                unPaintCircles(secondCircleGridPane, cable.getIds()[1].getIndexColumn());
                unPaintCircles(secondCircleGridPane, cable.getIds()[0].getIndexColumn());
                //System.out.println(cable.getIds()[1].getIndexColumn());
            }

            ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
            return;
        }

        ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
    }
    //como los switches y los leds extienden rectangle, entonces solo aceptan clases que extiendan rectangle.
    public static void deleteNode(Group nodeToDelete) {
        System.out.println("deleting...");
        ((AnchorPane) nodeToDelete.getParent()).getChildren().remove(nodeToDelete);
    }

    //necesita el evento y el nodo para poder aplicar los calculos.
    private static void calculateOffSet(MouseEvent e, Node node, AtomicReference<Double> startX, AtomicReference<Double> startY) {
        startX.set(e.getSceneX() - node.getTranslateX()); //get x value  or the cursor
        startY.set(e.getSceneY() - node.getTranslateY()); //get y value of the cursor.
    }

    private static void setOnDragFigure(MouseEvent e, Node node, AtomicReference<Double> startX, AtomicReference<Double> startY) {
        node.setTranslateX(e.getSceneX() - startX.get());
        node.setTranslateY(e.getSceneY() - startY.get());
    }

    public static void makeDraggableNode(Node node, AtomicReference<Double> startX, AtomicReference<Double> startY) {
        //Para hacer que funcione necesitamos utilizar AtomicReference
        node.setOnMousePressed(e -> {
            calculateOffSet(
                    e,
                    node,
                    startX,
                    startY
            );
        });

        node.setOnMouseDragged(e -> {
            setOnDragFigure(
                    e,
                    node,
                    startX,
                    startY
            );
        });
    }

    public static void makeUndraggableNode(Node node) {
        //
        node.setOnMousePressed(null);
        node.setOnMouseDragged(null);
    }

    //Metodo que realiza la funcion que corresponde a un Switch o un LED
    public static void IdentifiedFunction(CustomCircle StartHandler, CustomCircle EndHandler, CustomShape customShape){
        String[] GridNames = {
                "LedVolt1",
                "switchvolt1"
        };
        //Esta condicion comprueba si uno de los dos CustomCircle son del LED
        if (StartHandler.getID().getGridName().equals(GridNames[0]) || (EndHandler.getID().getGridName().equals(GridNames[0]))){
            LED.ONorOFF(customShape);
            //Esta condicion comprueba si uno de los CustomCircle pertenece al Switch
        } else if (StartHandler.getID().getGridName().equals(GridNames[1]) || (EndHandler.getID().getGridName().equals(GridNames[1]))) {
            Switch.ChargePass(customShape);
        }
    }

    public static String createRandomID() {
        return UUID.randomUUID().toString();
    }
}