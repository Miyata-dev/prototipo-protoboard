
package com.example.prototipo;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;

public class Utils {
    public Utils() {
    }
    //pinta una columna dependiendo del estado, este método se creó para mejorar la legibilidad del código.
    public static void paintColumn(ArrayList<CustomCircle> column, int state) {
        column.forEach(circle -> {
            circle.setState(state);
        });
    }
    //este método agrega un círclo a la colección que recibe como parámetro solo si son de la misma columna.
    private static void addCirclesFromSameColumn(ID id, Iterator<Node> fristCircleToPaint, ArrayList<CustomCircle> circles) {
        while(fristCircleToPaint.hasNext()) {
            Node circle = fristCircleToPaint.next();
            String targetID = circle.getId();
            ID temporaryID = new ID(targetID);
            CustomCircle targetedCircle = (CustomCircle) circle;
            int columnToGet = id.getIndexColumn();
            //mira que pertenezca a la misma columna
            if (ID.isThisColumn(temporaryID, columnToGet) && temporaryID.getGridName().equals(id.getGridName())) {
                circles.add(targetedCircle);
            }
        }
    }

    private static void addCircleFromSameRow(ID id, Iterator<Node> secondCircleToPaint, ArrayList<CustomCircle> circles) {
        while(secondCircleToPaint.hasNext()) {
            Node circle = secondCircleToPaint.next();
            String targetID = circle.getId();
            ID temporaryID = new ID(targetID);
            CustomCircle targetedCircle = (CustomCircle) circle;

            int rowToGet = id.getIndexRow();
            //temporaryID.getGridName().equals(id.getGridName())
            if (ID.isThisRow(temporaryID, rowToGet) && temporaryID.getGridName().equals(id.getGridName())) {
                circles.add(targetedCircle);
            }
        }
    }

    //se obtiene una coleccion de CustomCircles a partir de la columna en común, este método solo aplica a los gridPaneTrails.
    public static ArrayList<CustomCircle> getColumnOfCustomCircles(GridPaneObserver gridPaneObserver, ID id) {
        Iterator<Node> fristCircleToPaint = gridPaneObserver.getFirstGridPaneTrail().getGridPane().getChildren().iterator();
        Iterator<Node> secondCircleToPaint = gridPaneObserver.getSecondGridPaneTrail().getGridPane().getChildren().iterator();

        ArrayList<CustomCircle> circles = new ArrayList<>();
        //recorre el primer arreglo que tiene el gridPane.
        addCirclesFromSameColumn(id, fristCircleToPaint, circles);
        //recorre el segundo arreglo de gridpane para ver si pertenece.
        addCirclesFromSameColumn(id, secondCircleToPaint, circles);

        return circles;
    }

    public static ArrayList<CustomCircle> getRowOfCustomCircles(GridPaneObserver gridPaneObserver, ID id) {
        Iterator<Node> firstCircleToPaint = gridPaneObserver.getFirsGridPaneVolt().getGridPane().getChildren().iterator();
        Iterator<Node> secondCircleToPaint = gridPaneObserver.getSecondGridPaneVolt().getGridPane().getChildren().iterator();
        ArrayList<CustomCircle> circles = new ArrayList<>();

        addCircleFromSameRow(id, firstCircleToPaint, circles);
        addCircleFromSameRow(id, secondCircleToPaint, circles);

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
    public static void paintCircles(GridPaneObserver gridPaneObserver, ID id, int state, ArrayList<Cable> cables) {
        String[] validGridNames = {
                gridPaneObserver.getFirstGridPaneTrail().getName(),
                gridPaneObserver.getSecondGridPaneTrail().getName()
        };
        //int columnToPaint = id.getIndexColumn();
        //if (!id.getIsForGridpane()) return;
        if (!Arrays.asList(validGridNames).contains(id.getGridName())) return;
        ArrayList<CustomCircle> circles = getColumnOfCustomCircles(gridPaneObserver, id);

        Cable cableFound = null;

        //se obtiene uno de los cables que tienen los circulos de la coleccion.
        for (CustomCircle circle : circles) {
            if (circle.hasCable()) {
                cableFound = circle.getCable();
            }
        }

        if (cableFound == null) {
            circles.forEach(circle -> {
                circle.setState(state);
            });
            return;
        }
        //se obtienen las IDs de los circulos que el cable conecta.
        ID indexOne = cableFound.getIds()[0];
        ID indexTwo = cableFound.getIds()[1];

        if (!indexOne.getGridName().equals(indexTwo.getGridName())) {
            paintColumn(circles, state);
            return;
        }

        //mira que la primera id sea valida, es decir, esté dentro de los nombres del gridpane.
        if (Arrays.asList(validGridNames).contains(indexOne.getGridName())) {
            //se obtiene una colección de cables que están conectados entre si.
            ArrayList<Cable> connectedCables = getConnectedCables(cables, cableFound, true);
            //getColumnOfCustomCircles(grid, indexTwo.getIndexColumn());

            connectedCables.forEach(el -> {
                System.out.println(el.getIds()[0].getGeneratedID());
                System.out.println(el.getIds()[1].getGeneratedID());
                System.out.println("------------");
            });

            connectedCables.forEach(element -> {
                ID elementOneID = element.getIds()[1];
                ID elementTwoID = element.getIds()[0];

                ArrayList<CustomCircle> firstColumn = getColumnOfCustomCircles(gridPaneObserver, elementOneID);
                ArrayList<CustomCircle> secondColumn = getColumnOfCustomCircles(gridPaneObserver, elementTwoID);

                paintColumn(firstColumn, state);
                paintColumn(secondColumn, state);
            });
        }

        if (Arrays.asList(validGridNames).contains(indexTwo.getGridName())) {
            ArrayList<Cable> connectedCables = getConnectedCables(cables, cableFound, true);

            connectedCables.forEach(el -> {
                System.out.println(el.getIds()[0].getGeneratedID());
                System.out.println(el.getIds()[1].getGeneratedID());
                System.out.println("------------");
            });

            connectedCables.forEach(element -> {
                ID elementOneID = element.getIds()[0];
                ID elementTwoID = element.getIds()[1];

                ArrayList<CustomCircle> secondColumn = getColumnOfCustomCircles(gridPaneObserver, elementOneID);
                ArrayList<CustomCircle> firstColumn = getColumnOfCustomCircles(gridPaneObserver, elementTwoID);

                paintColumn(firstColumn, state);
                paintColumn(secondColumn, state);
            });
        }
    }
    //TODO dejar los gridNameVolts dinamicos.
    public static void paintCirclesVolt(GridPaneObserver gridPaneObserver, ID id, int state){
        String[] validGridNames = {
                "gridVolt1",
                "gridVolt2"
        };
        int rowToPaint = id.getIndexRow();
        if (!id.getIsForGridpane()) return;
        if (!Arrays.asList(validGridNames).contains(id.getGridName())) return;
        ArrayList<CustomCircle> circles = getRowOfCustomCircles(gridPaneObserver, id);

        circles.forEach(circle -> {
            circle.setState(state);
        });

    }
    //TODO se tiene que ver que la columna sea proveniente de un volt (la energia que recibe)
    public static void unPaintCircles(GridPaneObserver gridPaneObserver, ID id, boolean ignoreVoltCables) {
        ArrayList<CustomCircle> circles = getColumnOfCustomCircles(gridPaneObserver, id);

        if (hasVoltCable(circles) && ignoreVoltCables) return;

        circles.forEach(CustomCircle::removeEnergy);
    }

    public static void unPaintCirclesVolt(GridPaneObserver grid, ID id) {
        ArrayList<CustomCircle> circles = getRowOfCustomCircles(grid, id);
        circles.forEach(CustomCircle::removeEnergy);
    }

    //retorna null si no encuentra el nodo.
    public static CustomCircle getCustomCircleByID(GridPaneObserver gridPaneObserver, ID id) {
        CustomCircle foundCircle = null;

        Iterator<Node> firstGridTraiIterator = gridPaneObserver.getFirstGridPaneTrail().getGridPane().getChildren().iterator();
        Iterator<Node> secondGridTraiIterator = gridPaneObserver.getSecondGridPaneTrail().getGridPane().getChildren().iterator();
        Iterator<Node> firstVoltIterator = gridPaneObserver.getFirsGridPaneVolt().getGridPane().getChildren().iterator();
        Iterator<Node> secondVoltIterator = gridPaneObserver.getSecondGridPaneVolt().getGridPane().getChildren().iterator();
        //TODO pasar esta abominacion a método
        while(firstGridTraiIterator.hasNext()) {
            Node circle = (Node) firstGridTraiIterator.next();
            String targetID = circle.getId();
            ID temporaryID = new ID(targetID);
            CustomCircle targetedCircle = (CustomCircle) circle;

            if (ID.isSameID(temporaryID, id) && temporaryID.getGridName().equals(id.getGridName())) {
                foundCircle = targetedCircle;
            }
        }

        while(secondGridTraiIterator.hasNext()) {
            Node circle = (Node) secondGridTraiIterator.next();
            String targetID = circle.getId();
            ID temporaryID = new ID(targetID);
            CustomCircle targetedCircle = (CustomCircle) circle;

            if (ID.isSameID(temporaryID, id) && temporaryID.getGridName().equals(id.getGridName())) {
                foundCircle = targetedCircle;
            }
        }

        while(firstVoltIterator.hasNext()) {
            Node circle = (Node) firstVoltIterator.next();
            String targetID = circle.getId();
            ID temporaryID = new ID(targetID);
            CustomCircle targetedCircle = (CustomCircle) circle;

            if (ID.isSameID(temporaryID, id) && temporaryID.getGridName().equals(id.getGridName())) {
                foundCircle = targetedCircle;
            }
        }

        while(secondVoltIterator.hasNext()) {
            Node circle = (Node) secondVoltIterator.next();
            String targetID = circle.getId();
            ID temporaryID = new ID(targetID);
            CustomCircle targetedCircle = (CustomCircle) circle;

            if (ID.isSameID(temporaryID, id) && temporaryID.getGridName().equals(id.getGridName())) {
                foundCircle = targetedCircle;
            }
        }

        return foundCircle;
    }
    //devuelve el numero de cables que estan conectadas a un volt que tiene una columna.
    public static int numberOfCablesConnectedToVolts(GridPaneObserver gridPaneObserver, ID id) {
        int count = 0;
        ArrayList<CustomCircle> circles = getColumnOfCustomCircles(gridPaneObserver, id);

        for (CustomCircle circle : circles) {
            if (circle.hasCable()) {
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

    public static boolean isConnectedToBatery(ArrayList<CustomCircle> circles) {
        for (CustomCircle circle : circles) {
            if (circle.hasCable() && circle.getCable().isConnectedToBatery()) {
                System.out.println("it's conected to batery. ");
                System.out.println(circle.getCable().getIds()[0].getGridName());
                System.out.println(circle.getCable().getIds()[1].getGridName());

                return true;
            }
        }

        return false;
    }

    public static ArrayList<Cable> getConnectedCables(ArrayList<Cable> cables, Cable cableToConnect, boolean ignoreVoltCables) {
        String[] voltNames = {
                "gridVolt1",
                "gridVolt2"
        };

        HashSet<Cable> connectedCablesHashSet = new HashSet<>();
        Cable previousCable = cableToConnect; //para la iteracion, queremos ver si el cable por parametro es el cable anterior.

        //recorre todos los elementos de la colección de cables entregada.
        for (int i = 0; i < cables.size(); i++) {
            //se mira que cada uno de los cables de la colección, no provenga de un gridVolt
            boolean isFirstIDinVolt = Arrays.asList(voltNames).contains(cables.get(i).getIds()[0].getGridName());
            boolean isSecondIDinVolt = Arrays.asList(voltNames).contains(cables.get(i).getIds()[1].getGridName());
            
            //se agregan a la colección de cables si está la opción de ignorar el caso de que pertenezcan a los volts.
            boolean areVoltCablesValid = (!isFirstIDinVolt && !isSecondIDinVolt) || !ignoreVoltCables;

            //mira que los cables estén conectados entre si Y ADEMÁS, que no sea un cable que provenga de los gridVolts.
            if (Cable.areConnected(cables.get(i), cableToConnect) && areVoltCablesValid) {
                connectedCablesHashSet.add(cables.get(i));
            }
            //si es hashset NO está vacío, mira todos los elementos del hashset
            //y ve si está conectado a uno de los elementos de la colección.
            if (!connectedCablesHashSet.isEmpty()) {

                //mira si el cable que estamos mirando en la iteración (cables.get(i)) está conectadoa uno de los cables
                for (int j = 0; j < connectedCablesHashSet.size(); j++) {
                    ArrayList<Cable> arrayListAux = new ArrayList<>(connectedCablesHashSet);
                    //recorre toda la coleccion de cables entregada y mira si el elemento iterado está conectado
                    //con uno de los elementos del hashset
                    for (Cable cable : cables) {
                        //se mira que cada uno de los cables de la colección, no provenga de un gridVolt
                        boolean isFirstIDcableinVolt = Arrays.asList(voltNames).contains(cable.getIds()[0].getGridName());
                        boolean isSecondIDcableinVolt = Arrays.asList(voltNames).contains(cable.getIds()[1].getGridName());

                        //se agregan a la colección de cables si está la opción de ignorar el caso de que pertenezcan a los volts.
                        boolean areVoltsIDValid = (!isFirstIDcableinVolt && !isSecondIDcableinVolt) || !ignoreVoltCables;

                        if (Cable.areConnected(arrayListAux.get(j), cable) && areVoltsIDValid) {
                            connectedCablesHashSet.add(cable);
                        }
                    }
                }
            }
        }

        return new ArrayList<>(connectedCablesHashSet);
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
    //TODO pasarle el gridPaneObserevr para dsp calcular los grids.
    public static void deleteCable(MouseEvent e, GridPaneObserver gridPaneObserver, Bateria bateria, ArrayList<Cable> cables) {
        //se obtienen los gridpanes del gridpaneobserver.
        GridPaneTrailController gridOne = gridPaneObserver.getFirstGridPaneTrail();
        GridPaneTrailController gridTwo = gridPaneObserver.getSecondGridPaneTrail();
        GridPaneController gridVoltOne = gridPaneObserver.getFirsGridPaneVolt();
        GridPaneController gridVoltTwo = gridPaneObserver.getSecondGridPaneVolt();

        //estos son los nombres que usa internamente las ID de los circulos pertenecientes al centro.
        String[] validGridNames = {
            gridOne.getName(),
            gridTwo.getName()
        };
        //si una de las ids provienen de los volts (buses), entonces se realiza una eliminación de energía en cadena.
        //en caso de que el cable sea el único proveedoor se pierde la energia.
        String[] voltNames = {
            gridVoltOne.getName(),
            gridVoltTwo.getName()
        };

        //se recupera el nodo que se presiona.
        Node pressedNode = (Node) e.getTarget();
        if (!(e.getTarget() instanceof Cable pressedCable)) return;

        //se recupera las ids del cable para poder ver donde pasar la energia.
        ID firstID = pressedCable.getIds()[0];
        ID secondID = pressedCable.getIds()[1];
        CustomCircle resetState;

        //antes de entrar a los casos generales, se mira que el cable esté conectado a 2 volts, TODO hacer que funcione en cadena..
        if (Arrays.asList(voltNames).contains(secondID.getGridName()) && Arrays.asList(voltNames).contains(firstID.getGridName())) {
            System.out.println("el cable proviene de los 2 volts.");

            //se obtiene la colección de circulos de las filas de los volts.
            ArrayList<CustomCircle> firstCirclesRow = getRowOfCustomCircles(gridPaneObserver, firstID);
            ArrayList<CustomCircle> secondCircleRow = getRowOfCustomCircles(gridPaneObserver, secondID);

            boolean isFirstConnected = isConnectedToBatery(firstCirclesRow);
            boolean isSecondConnected = isConnectedToBatery(secondCircleRow);

            if (!isFirstConnected) {
                unPaintCirclesVolt(gridPaneObserver, firstID);
            }

            if (!isSecondConnected) {
                unPaintCirclesVolt(gridPaneObserver, secondID);
            }

            ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
            return;
        }

        //mira que provenga de una bateria, en ese caso se opera de manera diferente.
        if (firstID.getGridName().equals("BateryVolt")) {

            GridPane gridpane = null;
            //obtiene el indice del polo para poder obtener el circulo para .
            int index = firstID.getIndexRow();
            CustomCircle circle = bateria.getPoloByIndexRow(index);
            circle.setisTaken(false);

            if (Arrays.asList(validGridNames).contains(secondID.getGridName())) { //BateryVolt --> GridTrails 1 o 2

                System.out.println("in batery one");

                gridpane = getGridpaneByGridName(secondID.getGridName(), gridOne, gridTwo); //gridOne, gridTwo type is = gridPaneTarilController.
                unPaintCircles(gridPaneObserver, secondID, false);
                resetState = getCustomCircleByID(gridPaneObserver, secondID); // Para reiniciar el estado del circulo que esta tomando el cable cuando se elimina
                resetState.setisTaken(false);
                ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
                return;
            } else if (Arrays.asList(voltNames).contains(secondID.getGridName())) { //BateryVolt --> GridVolts
                System.out.println("in batery one");

                gridpane = getGridpaneByGridName(secondID.getGridName(), gridVoltOne, gridVoltTwo); //estos grid son del tipo gridPaneController (volts)
                unPaintCirclesVolt(gridPaneObserver, secondID);
                resetState = getCustomCircleByID(gridPaneObserver, secondID); // Para reiniciar el estado del circulo que esta tomando el cable cuando se elimina
                resetState.setisTaken(false);
                ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
                return;
            }

        } else if (secondID.getGridName().equals("BateryVolt")) {
            GridPane gridpane = null;

            //obtiene el indice del circulo que pertenece a la bateria, si es 1, devuelve el polo positivo, si es 2 pasa el polo negativo.
            int index = secondID.getIndexRow();
            CustomCircle circle = bateria.getPoloByIndexRow(index);
            circle.setisTaken(false);
            System.out.println("in batery two ");
            //si el id pertenece a uno de los gridpanes de pistas, entonces entra en este condicional.
            if (Arrays.asList(validGridNames).contains(firstID.getGridName())) { // GridTrails 1 o 2 -->  BateryVolt

                gridpane = getGridpaneByGridName(firstID.getGridName(), gridOne, gridTwo); //son del tipo GridPaneTrailController.
                unPaintCircles(gridPaneObserver, firstID, false);
                resetState = getCustomCircleByID(gridPaneObserver, firstID); // Para reiniciar el estado del circulo que esta tomando el cable cuando se elimina
                resetState.setisTaken(false);
                ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
                return;

            } else if (Arrays.asList(voltNames).contains(firstID.getGridName())) {

                gridpane = getGridpaneByGridName(firstID.getGridName(), gridVoltOne, gridVoltTwo); //son del tipo GridPaneController (volts)
                unPaintCirclesVolt(gridPaneObserver, firstID);
                resetState = getCustomCircleByID(gridPaneObserver, firstID); // Para reiniciar el estado del circulo que esta tomando el cable cuando se elimina
                resetState.setisTaken(false);
                ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
                return;
            }
        }

        //ver si esta en volts.
        if (Arrays.asList(voltNames).contains(firstID.getGridName()) && Arrays.asList(voltNames).contains(secondID.getGridName())) {
            ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
            return;
        }

        //si el primer ID pertenece a los volts, se extrae el Gridpane del segundo para despintarlo.
        if (Arrays.asList(voltNames).contains(firstID.getGridName())) {
            //se obtiene la coleccion de cables conectados al primer cable.
            ArrayList<Cable> connectedCables = getConnectedCables(cables, pressedCable, true);
            //si hay un solo cable, entonces no hay necesidad de eliminar la energía en cadena. //TODO revisar que pasa al conectar 2 gridpaneVolts.
            if (connectedCables.isEmpty()) {
                unPaintCircles(gridPaneObserver, secondID, false);

                ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
                return;
            }

            for (Cable cable : connectedCables) {
                unPaintCircles(gridPaneObserver, cable.getIds()[1], false);
                //unPaintCircles(firstCircleGridPane, cable.getIds()[1].getIndexColumn(), false);
                unPaintCircles(gridPaneObserver, cable.getIds()[0], false);
                //unPaintCircles(firstCircleGridPane, cable.getIds()[0].getIndexColumn(), false);
            }

            ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
            return;
        } else if (Arrays.asList(voltNames).contains(secondID.getGridName())) {
            //se obtiene la coleccion de cables conectados al primer cable.
            ArrayList<Cable> connectedCables = getConnectedCables(cables, pressedCable, true);
            //TODO revisar que pasa al conectar 2 gridpaneVolts.
            if (connectedCables.isEmpty()) {
                unPaintCircles(gridPaneObserver, firstID, false);

                ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
                return;
            }


            for (Cable cable : connectedCables) {
                GridPane secondCircleGridPane = null;

                unPaintCircles(gridPaneObserver, cable.getIds()[1], false);
                unPaintCircles(gridPaneObserver, cable.getIds()[0], false);
                //System.out.println(cable.getIds()[1].getIndexColumn());
            }

            ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
            return;
        }

        ArrayList<Cable> connectedCables = getConnectedCables(cables, pressedCable, true);

        for (Cable cable : connectedCables) {
            ArrayList<CustomCircle> column = getColumnOfCustomCircles(gridPaneObserver, cable.getIds()[0]);
            hasVoltCable(column);

            unPaintCircles(gridPaneObserver, cable.getIds()[1], true);
            unPaintCircles(gridPaneObserver, cable.getIds()[0], true);
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