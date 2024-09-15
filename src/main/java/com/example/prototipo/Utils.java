
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
import javafx.scene.shape.Circle;

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

    public static boolean hasVoltCable(ArrayList<CustomCircle> circles) {
        String[] voltNames = {
                "gridVolt1",
                "gridVolt2"
        };

        boolean value = false;

        for (CustomCircle circle : circles) {
            if (circle.hasCable()) {
                if (
                        Arrays.asList(voltNames).contains(circle.getCable().getIds()[1].getGridName()) ||
                                Arrays.asList(voltNames).contains(circle.getCable().getIds()[0].getGridName())
                ) {
                    value = true;
                }
            }
        }

        return value;
    }

    //TODO resolver bug cuando se contecta 2 cables en los grid de voltios. propuesta, debe tomar un gridpanetrail.
    //pinta una columna de un color dependiendo del estado que tenga (1 ¿ positivo, -1 negativo), pasar ID y calcular el
    // indice de columna, ver si la id es valida o no (a travez de su gridName)
    public static void paintCircles(GridPane grid, ID id, int state, ArrayList<Cable> cables) {
        String[] validGridNames = {
                "gridTrail1",
                "gridTrail2"
        };
        int columnToPaint = id.getIndexColumn();
        //if (!id.getIsForGridpane()) return;
        if (!Arrays.asList(validGridNames).contains(id.getGridName())) return;
        ArrayList<CustomCircle> circles = getColumnOfCustomCircles(grid, columnToPaint);

        Cable cableFound = null;

        //se obtiene un los cables que tienen los circulos de la coleccion.
        for (CustomCircle circle : circles) {
            if (circle.hasCable()) {
                System.out.println("has cable");
                System.out.println(circle.getCable().getRandomID());
                cableFound = circle.getCable();
            }
        }

        if (cableFound == null) {
            circles.forEach(circle -> {
                circle.setState(state);
            });
            return;
        }

        System.out.println("paso de energia en cadena.");

        ID indexOne = cableFound.getIds()[0];
        ID indexTwo = cableFound.getIds()[1];

        System.out.println("index one: " + indexOne.getIndexColumn() + " gridpane: " + indexOne.getGridName());
        System.out.println("index two: " + indexTwo.getIndexColumn() + " gridpane: " + indexTwo.getGridName());

        //si son iguales solo se pinta una vez.
        if (!indexOne.getGridName().equals(indexTwo.getGridName())) {

            circles.forEach(circle -> {
                circle.setState(state);
            });

            return;
        }

        //mira que la primera id sea valida, es decir, esté dentro de los nombres del gridpane.
        if (Arrays.asList(validGridNames).contains(indexOne.getGridName())) {
            ArrayList<CustomCircle> firstColumn = getColumnOfCustomCircles(grid, indexOne.getIndexColumn());

            firstColumn.forEach(circle -> {
                System.out.println("circle gridpane: " + circle.getID().getGridName());
                System.out.println("index id: " + indexTwo.getGridName());
                circle.setState(state);
            });
        }

        if (Arrays.asList(validGridNames).contains(indexTwo.getGridName())) {
            ArrayList<CustomCircle> secondColumn = getColumnOfCustomCircles(grid, indexTwo.getIndexColumn());

            secondColumn.forEach(circle -> {
                System.out.println("circle gridpane: " + circle.getID().getGridName());
                System.out.println("index id: " + indexTwo.getGridName());
                circle.setState(state);
            });
        }
        /*
        circles.forEach(circle -> {
            circle.setState(state);
        }); */

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

    public static void unPaintCircles(GridPane grid, int columnToUnPaint, boolean ignoreVoltCables) {
        ArrayList<CustomCircle> circles = getColumnOfCustomCircles(grid, columnToUnPaint);

        if (hasVoltCable(circles) && ignoreVoltCables) return;

        circles.forEach(circle -> {
            circle.setState(0);
        });
    }

    public static void unPaintCirclesVolt(GridPane grid, int rowToUnPaint) {
        ArrayList<CustomCircle> circles = getRowOfCustomCircles(grid, rowToUnPaint);
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

    public static int getIndexOfCable(ArrayList<Cable> cables, Cable previousCable) {
        int initialIndex = -1; //si no encuentra el indice, se deja en -1

        for (int i = 0; i < cables.size(); i++) {
            if (Cable.compareCables(cables.get(i), previousCable)) {
                initialIndex = i;
            }
        }

        return initialIndex;
    }

    public static ArrayList<Cable> getConnectedCables(ArrayList<Cable> cables, Cable cableToConnect) {
        String[] voltNames = {
                "gridVolt1",
                "gridVolt2"
        };

        ArrayList<Cable> connectedCables = new ArrayList<>();

        Cable previousCable = cableToConnect; //para la iteracion, queremos ver si el cable por parametro es el cable anterior.

        boolean isFirstIDinVolt = Arrays.asList(voltNames).contains(cableToConnect.getIds()[0].getGridName());
        boolean isSecondIDinVolt = Arrays.asList(voltNames).contains(cableToConnect.getIds()[1].getGridName());
        //si el cable no está conectado a uno de los buses.
        if (!isFirstIDinVolt && !isSecondIDinVolt) {
            //obtiene el indice del cable que quiere obtener
            int initialIndex = getIndexOfCable(cables, previousCable);

            for (int i = initialIndex; i < cables.size(); i++) {
                if (connectedCables.isEmpty()) {
                    if (Cable.areConnected(cables.get(i), cableToConnect)) {
                        connectedCables.add(cables.get(i));
                    }
                } else {
                    //se obtiene el ultimo cable de la coleccion para poder comparar la conectivida con el siguiente.
                    previousCable = connectedCables.get(connectedCables.size() - 1);

                    if (Cable.areConnected(cables.get(i), previousCable)) {
                        connectedCables.add(cables.get(i));
                    }
                }
            }

            return connectedCables;
        }

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

    public static GridPane getGridpaneByGridName(String GridName, GridPaneTrailController gridOne, GridPaneTrailController gridTwo) {
        String[] validGridNames = {
                "gridTrail1",
                "gridTrail2"
        };
        System.out.println("gridname is: " + GridName);

        GridPane gridpane = null;

        if (GridName.equals(validGridNames[0])) {
            gridpane = gridOne.getGridPane();
        } else if (GridName.equals(validGridNames[1])) {
            gridpane = gridTwo.getGridPane();
        }

        return gridpane;
    }
    //obtiene el gridpane de acuerdo al nombre que se asigno, este metodo recibe los GridPaneTarilController.
    public static GridPane getGridpaneByGridName(String GridName, GridPaneController gridVoltOne, GridPaneController gridVoltTwo) {
        String[] voltNames = {
                "gridVolt1",
                "gridVolt2"
        };

        GridPane gridpane = null;

        if (GridName.equals(voltNames[0])) {
            gridpane = gridVoltOne.getGridPane();
        } else if (GridName.equals(voltNames[1])) {
            gridpane = gridVoltTwo.getGridPane();
        }

        return gridpane;
    }
    //GridPaneController gridVoltOne, GridPaneController gridVoltTwo
    public static void deleteCable(MouseEvent e, GridPaneTrailController gridOne, GridPaneTrailController gridTwo, GridPaneController gridVoltOne, GridPaneController gridVoltTwo, Bateria bateria, ArrayList<Cable> cables) {
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

        //mira que provenga de una bateria, en ese caso se opera de manera diferente.
        if (firstID.getGridName().equals("BateryVolt")) {

            GridPane gridpane = null;
            //obtiene el indice del polo para poder obtener el circulo para .
            int index = firstID.getIndexRow();
            CustomCircle circle = bateria.getPoloByIndexRow(index);
            circle.setisTaken(false);

            if (Arrays.asList(validGridNames).contains(secondID.getGridName())) {
                /*
                if (firstID.getGridName().equals(validGridNames[0])) {
                    gridpane = gridOne.getGridPane();
                } else if (firstID.getGridName().equals(validGridNames[1])) {
                    gridpane = gridTwo.getGridPane();
                } */

                ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
                return;
            } else if (Arrays.asList(voltNames).contains(secondID.getGridName())) {
                /*
                if (secondID.getGridName().equals(voltNames[0])) {
                    gridpane = gridVoltOne.getGridPane();
                } else if (secondID.getGridName().equals(voltNames[1])) {
                    gridpane = gridVoltTwo.getGridPane();
                } */

                gridpane = getGridpaneByGridName(secondID.getGridName(), gridVoltOne, gridVoltTwo);

                unPaintCirclesVolt(gridpane, secondID.getIndexRow());
                ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
                return;
            }

        } else if (secondID.getGridName().equals("BateryVolt")) {
            GridPane gridpane = null;

            //obtiene el indice del circulo que pertenece a la bateria, si es 1, devuelve el polo positivo, si es 2 pasa el polo negativo.
            int index = secondID.getIndexRow();
            CustomCircle circle = bateria.getPoloByIndexRow(index);
            circle.setisTaken(false);

            //si el id pertenece a uno de los gridpanes de pistas, entonces entra en este condicional.
            if (Arrays.asList(validGridNames).contains(firstID.getGridName())) {
                gridpane = getGridpaneByGridName(firstID.getGridName(), gridOne, gridTwo);

                ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
                return;

            } else if (Arrays.asList(voltNames).contains(firstID.getGridName())) {

                gridpane = getGridpaneByGridName(firstID.getGridName(), gridVoltOne, gridVoltTwo);
                unPaintCirclesVolt(gridpane, firstID.getIndexRow());

                ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
                return;
            }
        }

        //ver si esta en volts
        if (Arrays.asList(voltNames).contains(firstID.getGridName()) && Arrays.asList(voltNames).contains(secondID.getGridName())) {
            ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
            return;
        }

        //si el primer ID pertenece a los volts, se extrae el Gridpane del segundo para despintarlo.
        if (Arrays.asList(voltNames).contains(firstID.getGridName())) {
            //se obtiene la coleccion de cables conectados al primer cable.
            ArrayList<Cable> connectedCables = getConnectedCables(cables, pressedCable);

            for (Cable cable : connectedCables) {
                GridPane firstCircleGridPane = null;
                GridPane secondCircleGridPane = null;

                secondCircleGridPane = getGridpaneByGridName(
                        cable.getIds()[1].getGridName(),
                        gridOne,
                        gridTwo
                );

                if (cable.getIds()[0].getGridName().equals(gridTwo.getName())) {
                    firstCircleGridPane = gridTwo.getGridPane();
                } else if (cable.getIds()[0].getGridName().equals(gridOne.getName())) {
                    firstCircleGridPane = gridOne.getGridPane();
                }
                /*
                firstCircleGridPane = getGridpaneByGridName(
                    cable.getIds()[0].getGridName(),
                    gridOne,
                    gridTwo
                ); */

                unPaintCircles(secondCircleGridPane, cable.getIds()[1].getIndexColumn(), false);
                //unPaintCircles(firstCircleGridPane, cable.getIds()[1].getIndexColumn(), false);

                unPaintCircles(secondCircleGridPane, cable.getIds()[0].getIndexColumn(), false);
                //unPaintCircles(firstCircleGridPane, cable.getIds()[0].getIndexColumn(), false);
            }

            ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
            return;
        } else if (Arrays.asList(voltNames).contains(secondID.getGridName())) {
            //se obtiene la coleccion de cables conectados al primer cable.
            ArrayList<Cable> connectedCables = getConnectedCables(cables, pressedCable);

            for (Cable cable : connectedCables) {
                GridPane secondCircleGridPane = null;

                secondCircleGridPane = getGridpaneByGridName(
                        cable.getIds()[0].getGridName(),
                        gridOne,
                        gridTwo
                );

                unPaintCircles(secondCircleGridPane, cable.getIds()[1].getIndexColumn(), false);
                unPaintCircles(secondCircleGridPane, cable.getIds()[0].getIndexColumn(), false);
                //System.out.println(cable.getIds()[1].getIndexColumn());
            }

            ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
            return;
        }

        ArrayList<Cable> connectedCables = getConnectedCables(cables, pressedCable);

        for (Cable cable : connectedCables) {
            GridPane secondCircleGridPane = null;

            secondCircleGridPane = getGridpaneByGridName(
                    cable.getIds()[0].getGridName(),
                    gridOne,
                    gridTwo
            );

            ArrayList<CustomCircle> column = getColumnOfCustomCircles(secondCircleGridPane, cable.getIds()[0].getIndexColumn());
            hasVoltCable(column);

            unPaintCircles(secondCircleGridPane, cable.getIds()[1].getIndexColumn(), true);
            unPaintCircles(secondCircleGridPane, cable.getIds()[0].getIndexColumn(), true);
        }

        ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
    }
    //como los switches y los leds extienden rectangle, entonces solo aceptan clases que extiendan rectangle.
    public static void deleteNode(Group nodeToDelete) {
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
    public static void IdentifiedFunction(CustomCircle StartHandler, CustomCircle EndHandler, CustomShape customShape, ArrayList<Cable> cables){
        String[] GridNames = {
                "LedVolt1",
                "switchvolt1"
        };
        //Esta condicion comprueba si uno de los dos CustomCircle son del LED
        if (StartHandler.getID().getGridName().equals(GridNames[0]) || (EndHandler.getID().getGridName().equals(GridNames[0]))){
            LED.ONorOFF(customShape);
            //Esta condicion comprueba si uno de los CustomCircle pertenece al Switch
        } else if (StartHandler.getID().getGridName().equals(GridNames[1]) || (EndHandler.getID().getGridName().equals(GridNames[1]))) {
            Switch.ChargePass(customShape, cables);
        }
    }

    public static String createRandomID() {
        return UUID.randomUUID().toString();
    }
}