
package com.example.prototipo;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

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
    public static void paintCirclesCollection(GridPaneObserver gridPaneObserver, ArrayList<CustomCircle> column, int state) {
        if (!gridPaneObserver.getIsEnergyActivated()) return;

        System.out.println("is activated?: " + gridPaneObserver.getIsEnergyActivated());
        column.forEach(circle -> {
            circle.setState(state);
        });
        //se registra en el gridPane la columna afectada.
        gridPaneObserver.addColumn(column, state);
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

    //pinta una columna de un color dependiendo del estado que tenga (1 positivo, -1 negativo), pasar ID y calcular el
    // indice de columna, ver si la id es valida o no (a travez de su gridName)
    public static void paintCircles(GridPaneObserver gridPaneObserver, ID id, int state) {
        String[] validGridNames = {
                gridPaneObserver.getFirstGridPaneTrail().getName(),
                gridPaneObserver.getSecondGridPaneTrail().getName()
        };
        if (!Arrays.asList(validGridNames).contains(id.getGridName())) return;
        ArrayList<CustomCircle> circles = getColumnOfCustomCircles(gridPaneObserver, id);
        //se extraen los cables del griPaneController.
        ArrayList<Cable> cables = gridPaneObserver.getCables();

        Cable cableFound = null;

        //se obtiene uno de los cables que tienen los circulos de la coleccion.
        for (CustomCircle circle : circles) {
            if (circle.hasCable()) {
                cableFound = circle.getCable();
            }
        }

        if (cableFound == null) {
            paintCirclesCollection(gridPaneObserver, circles, state);
            /*
            circles.forEach(circle -> {
                circle.setState(state);
            });
            //se registra la columna en el gridPaneObserver
            gridPaneObserver.addColumn(circles, state); */
            return;
        }
        //se obtienen las IDs de los circulos que el cable conecta.
        ID indexOne = cableFound.getIds()[0];
        ID indexTwo = cableFound.getIds()[1];

        if (!indexOne.getGridName().equals(indexTwo.getGridName())) {
            paintCirclesCollection(gridPaneObserver, circles, state);
            //se registra la columna en el gridPaneObserver
            //gridPaneObserver.addColumn(circles, state);
            return;
        }

        //mira que la primera id sea valida, es decir, esté dentro de los nombres del gridpane.
        if (Arrays.asList(validGridNames).contains(indexOne.getGridName())) {
            //se obtiene una colección de cables que están conectados entre si.
            ArrayList<Cable> connectedCables = getConnectedCables(cables, cableFound, true);

//            connectedCables.forEach(el -> {
//                System.out.println(el.getIds()[0].getGeneratedID());
//                System.out.println(el.getIds()[1].getGeneratedID());
//                System.out.println("------------");
//            });

            for (Cable element : connectedCables) {
                ID elementOneID = element.getIds()[1];
                ID elementTwoID = element.getIds()[0];

                ArrayList<CustomCircle> firstColumn = getColumnOfCustomCircles(gridPaneObserver, elementOneID);
                ArrayList<CustomCircle> secondColumn = getColumnOfCustomCircles(gridPaneObserver, elementTwoID);

                paintCirclesCollection(gridPaneObserver, firstColumn, state);
                paintCirclesCollection(gridPaneObserver, secondColumn, state);
            }
            /*
            //se mira que el cable que se esté mirando tenga un círculo conectado a un switch o led.
                if (elementOneID.getGridName().contains(gridPaneObserver.getLedIdPrefix())) {
                    CustomCircle
                } else if () {

                }
            **/
        }

        if (Arrays.asList(validGridNames).contains(indexTwo.getGridName())) {
            ArrayList<Cable> connectedCables = getConnectedCables(cables, cableFound, true);

            connectedCables.forEach(el -> {
                System.out.println(el.getIds()[0].getGeneratedID());
                System.out.println(el.getIds()[1].getGeneratedID());
                System.out.println("------------");
            });

            for (Cable element : connectedCables) {
                ID elementOneID = element.getIds()[0];
                ID elementTwoID = element.getIds()[1];

                ArrayList<CustomCircle> secondColumn = getColumnOfCustomCircles(gridPaneObserver, elementOneID);
                ArrayList<CustomCircle> firstColumn = getColumnOfCustomCircles(gridPaneObserver, elementTwoID);

                paintCirclesCollection(gridPaneObserver, firstColumn, state);
                paintCirclesCollection(gridPaneObserver, secondColumn, state);
            }
        }
    }


    public static void paintCirclesVolt(GridPaneObserver gridPaneObserver, ID id, int state){
        String[] validGridNames = {
                "gridVolt1",
                "gridVolt2"
        };
        int rowToPaint = id.getIndexRow();
        if (!id.getIsForGridpane()) return;
        if (!Arrays.asList(validGridNames).contains(id.getGridName())) return;
        ArrayList<CustomCircle> circles = getRowOfCustomCircles(gridPaneObserver, id);
        paintCirclesCollection(gridPaneObserver, circles, state);
        //se registra la columna afectada.
        gridPaneObserver.addColumn(circles, state);
    }

    public static void unPaintCircles(GridPaneObserver gridPaneObserver, CustomCircle circle/*, boolean ignoreVoltCables*/) {
        ID circleID = circle.getID();
        System.out.println("Gridname: "+ circleID.getGridName());

        ArrayList<CustomCircle> circles = gridPaneObserver.getCircles(gridPaneObserver, circleID);
//        if (hasVoltCable(circles) && ignoreVoltCables) return;

        //mira si el círculo pertenece a un led, si pertenece a un led, se elimina ese circulo de las columnas energizadas.
        if (circleID.getGridName().contains(gridPaneObserver.getLedIdPrefix())) {
            System.out.println("from unpaintCircles LED...");
            ArrayList<CustomCircle> ledCircle = new ArrayList<>();
            ledCircle.add(circle);
            gridPaneObserver.removeColumn(ledCircle);
            ledCircle.forEach(CustomCircle::removeEnergy);
            return;
        }

        if(!circleID.getGridName().equals("BateryVolt")){
            gridPaneObserver.removeColumn(circles);
        }
        circles.forEach(CustomCircle::removeEnergy);
    }
//    public static void unPaintCirclesVolt(GridPaneObserver grid, CustomCircle circle) {
//        ID circleID = circle.getID();
//
//        //mira si el círculo pertenece a un led, si pertenece a un led, se elimina ese circulo de las columnas energizadas.
//        if (circleID.getGridName().contains(grid.getLedIdPrefix())) {
//            System.out.println("from unpaintCircles LED...");
//            ArrayList<CustomCircle> circles = new ArrayList<>();
//            circles.add(circle);
//            grid.removeColumn(circles);
//            circles.forEach(CustomCircle::removeEnergy);
//            return;
//        }
//        System.out.println("from unpaintCircles NOT LED...");
//        ArrayList<CustomCircle> circles = getRowOfCustomCircles(grid, circleID);
//        grid.removeColumn(circles);
//        circles.forEach(CustomCircle::removeEnergy);
//    }

    public static boolean isConnectedToBatery(ArrayList<CustomCircle> circles) {
        for (CustomCircle circle : circles) {
            if (circle.hasCable() && circle.getCable().isConnectedToBatery()) {
                return true;
            }
        }

        return false;
    }
    //TODO pedir el gridPaneObserver
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

    public static Cable getCableByID(ArrayList<Cable> cables, Cable cableToFind) {
        Cable cableFound = null;

        for (Cable cable : cables) {
            if (cableToFind.getRandomID().equals(cable.getRandomID())) {
                cableFound = cable;
            }
        }

        return cableFound;
    }
    //TODO eliminar el cable de la coleccion una vez se borra del gridpane.
    public static void deleteCable(MouseEvent e, GridPaneObserver gridPaneObserver, Bateria bateria) {
        //se obtienen los gridpanes del gridpaneobserver.
        GridPaneTrailController gridOne = gridPaneObserver.getFirstGridPaneTrail();
        GridPaneTrailController gridTwo = gridPaneObserver.getSecondGridPaneTrail();
        GridPaneController gridVoltOne = gridPaneObserver.getFirsGridPaneVolt();
        GridPaneController gridVoltTwo = gridPaneObserver.getSecondGridPaneVolt();

        //se obtiene los cables del gridPaneObserver
        ArrayList<Cable> cables = gridPaneObserver.getCables();

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
        //se tiene que obtener el cable de la coleccion, sino la información de los círculos se pierde.
        Cable cableFound = getCableByID(cables, pressedCable);
        //se recupera las ids del cable para poder ver donde pasar la energia.
        ID firstID = pressedCable.getIds()[0]; //firstCircle = pressedCable.getFirstCircle();
        ID secondID = pressedCable.getIds()[1];

        //se obtiene
        CustomCircle firstCircle = cableFound.getFirstCircle();
        CustomCircle secondCircle = cableFound.getSecondCircle();

        //como se llama a esta instrucción muchas veecs dentro del programa y no es necesario
        //un método para la instrucción, entonces se ocupa el Consumer.
        Consumer<Cable> deleteCableFromGridPane = (cable) -> {
            ResetStateCustomCircles(cable);
            gridPaneObserver.removeCable(cable);
            ((AnchorPane) pressedNode.getParent()).getChildren().remove(cable);
        };

        //se mira el caso específico que el cable esté conectado gridVolt -> led o led -> gridVolt.
        if (firstID.getGridName().contains(gridPaneObserver.getGridVoltPrefix()) && secondID.getGridName().contains(gridPaneObserver.getLedIdPrefix())) {
            deleteCableFromGridPane.accept(cableFound);
            return;
        } else if (secondID.getGridName().contains(gridPaneObserver.getGridVoltPrefix()) && firstID.getGridName().contains(gridPaneObserver.getLedIdPrefix())) {
            deleteCableFromGridPane.accept(cableFound);
            return;
        }

        //se mira el caso específico que el cable esté conectado gridVolt -> switch o switch -> gridVolt.
        if (firstID.getGridName().contains(gridPaneObserver.getGridVoltPrefix()) && secondID.getGridName().contains(gridPaneObserver.getSwitchIdPrefix())) {
            deleteCableFromGridPane.accept(cableFound);
            return;
        } else if (secondID.getGridName().contains(gridPaneObserver.getGridVoltPrefix()) && firstID.getGridName().contains(gridPaneObserver.getSwitchIdPrefix())) {
            deleteCableFromGridPane.accept(cableFound);
            return;
        }

        //antes de entrar a los casos de gridPanes, se mira que uno de los cables esté conectado a un led.
        if (firstID.getGridName().contains(gridPaneObserver.getLedIdPrefix())) {
            System.out.println("first id is from a led");
            unPaintCircles(gridPaneObserver, secondCircle);
            deleteCableFromGridPane.accept(cableFound);
            return;
        } else if (secondID.getGridName().contains(gridPaneObserver.getLedIdPrefix())) {
            System.out.println("second id is from a led");
            unPaintCircles(gridPaneObserver, firstCircle);
            deleteCableFromGridPane.accept(cableFound);
            return;
        }
        //mira si la primera ID pertenece a un switch, si pertenece a uno, entonces
        if (firstID.getGridName().contains(gridPaneObserver.getSwitchIdPrefix())) {
            System.out.println("first id is from a switch");
            unPaintCircles(gridPaneObserver, secondCircle);
            deleteCableFromGridPane.accept(cableFound);
            return;
        } else if (secondID.getGridName().contains(gridPaneObserver.getSwitchIdPrefix())) {
            System.out.println("second id is from a switch");
            unPaintCircles(gridPaneObserver, firstCircle);
            deleteCableFromGridPane.accept(cableFound);
            return;
        }
        //antes de entrar a los casos generales, se mira que el cable esté conectado a 2 volts, TODO hacer que funcione en cadena..
//        if (Arrays.asList(voltNames).contains(secondID.getGridName()) && Arrays.asList(voltNames).contains(firstID.getGridName())) {
//            System.out.println("in volt ");
//            //se obtiene la colección de circulos de las filas de los volts.
//            ArrayList<CustomCircle> firstCirclesRow = getRowOfCustomCircles(gridPaneObserver, firstID);
//            ArrayList<CustomCircle> secondCircleRow = getRowOfCustomCircles(gridPaneObserver, secondID);
//
//            //Pregunta si no estan conectados a una bateria, despinta el volt
//            if (!isConnectedToBatery(firstCirclesRow)) {
//                unPaintCircles(gridPaneObserver, firstCircle);
//            }
//            if (!isConnectedToBatery(secondCircleRow)) {
//                unPaintCircles(gridPaneObserver, secondCircle);
//            }
//            ResetStateCustomCircles(cableFound);
//            gridPaneObserver.removeCable(cableFound);
//            ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
//            return;
//        }
//
        //mira que provenga de una bateria, en ese caso se opera de manera diferente.
//        if (firstID.getGridName().equals("BateryVolt")) {
//            //obtiene el indice del polo para poder obtener el circulo para .
//            int index = firstID.getIndexRow();
//            CustomCircle circle = bateria.getPoloByIndexRow(index);
//            circle.setisTaken(false);
//
//            if (Arrays.asList(validGridNames).contains(secondID.getGridName())) { //BateryVolt --> GridTrails 1 o 2
//                System.out.println("in batery one");
//                unPaintCircles(gridPaneObserver,secondCircle);
//                ResetStateCustomCircles(cableFound);
//                gridPaneObserver.removeCable(cableFound);
//                ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
//                return;
//            } else if (Arrays.asList(voltNames).contains(secondID.getGridName())) { //BateryVolt --> GridVolts
//                System.out.println("in batery one");
//                unPaintCircles(gridPaneObserver, secondCircle);
//                ResetStateCustomCircles(cableFound);
//                gridPaneObserver.removeCable(cableFound);
//                ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
//                return;
//            }
//
//        } else if (secondID.getGridName().equals("BateryVolt")) {
//            //obtiene el indice del circulo que pertenece a la bateria, si es 1, devuelve el polo positivo, si es 2 pasa el polo negativo.
//            int index = secondID.getIndexRow();
//            CustomCircle circle = bateria.getPoloByIndexRow(index);
//            circle.setisTaken(false);
//            System.out.println("in batery two ");
//            //si el id pertenece a uno de los gridpanes de pistas, entonces entra en este condicional.
//            if (Arrays.asList(validGridNames).contains(firstID.getGridName())) { // GridTrails 1 o 2 -->  BateryVolt
//                unPaintCircles(gridPaneObserver, firstCircle);
//                ResetStateCustomCircles(cableFound);
//                gridPaneObserver.removeCable(cableFound);
//                ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
//                return;
//
//            } else if (Arrays.asList(voltNames).contains(firstID.getGridName())) {
//                unPaintCircles(gridPaneObserver, firstCircle);
//                ResetStateCustomCircles(cableFound);
//                gridPaneObserver.removeCable(cableFound);
//                ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
//                return;
//            }
//        }
//        //si el primer ID pertenece a los volts, se extrae el Gridpane del segundo para despintarlo.
//        if (Arrays.asList(voltNames).contains(firstID.getGridName())) {
//            //se obtiene la coleccion de cables conectados al primer cable.
//            ArrayList<Cable> connectedCables = getConnectedCables(cables, pressedCable, false);
//            //si hay un solo cable, entonces no hay necesidad de eliminar la energía en cadena.
//            if (connectedCables.isEmpty()) {
//                unPaintCircles(gridPaneObserver, secondCircle);
//                ResetStateCustomCircles(cableFound);
//                gridPaneObserver.removeCable(cableFound);
//                ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
//                return;
//            }
//
//            for (Cable cable : connectedCables) {
//                unPaintCircles(gridPaneObserver, cable.getSecondCircle());
//                unPaintCircles(gridPaneObserver, cable.getFirstCircle());
//            }
//            ResetStateCustomCircles(cableFound);
//            gridPaneObserver.removeCable(cableFound);
//            ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
//            return;
//        } else if (Arrays.asList(voltNames).contains(secondID.getGridName())) {
//            //se obtiene la coleccion de cables conectados al primer cable.
//            ArrayList<Cable> connectedCables = getConnectedCables(cables, pressedCable, false);
//            if (connectedCables.isEmpty()) {
//                unPaintCircles(gridPaneObserver, firstCircle);
//                ResetStateCustomCircles(cableFound);
//                gridPaneObserver.removeCable(cableFound);
//                ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
//                return;
//            }
//            for (Cable cable : connectedCables) {
//                unPaintCircles(gridPaneObserver, cable.getSecondCircle());
//                unPaintCircles(gridPaneObserver, cable.getFirstCircle());
//            }
//            ResetStateCustomCircles(cableFound);
//            gridPaneObserver.removeCable(cableFound);
//            ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
//            return;
//        }
        ArrayList<Cable> connectedCables = getConnectedCables(cables, pressedCable, false);

        for (Cable cable : connectedCables) {
//            ArrayList<CustomCircle> column = GridPaneObserver.getCircles(gridPaneObserver, cable.getIds()[0]);
//            hasVoltCable(column);

            unPaintCircles(gridPaneObserver, cable.getSecondCircle());
            unPaintCircles(gridPaneObserver, cable.getFirstCircle());
        }
        ResetStateCustomCircles(cableFound);
        gridPaneObserver.removeCable(cableFound);
        ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
    }

    public static void ResetStateCustomCircles(Cable pressedCable) {

        pressedCable.Getcircles()[0].setisTaken(false);
        pressedCable.Getcircles()[1].setisTaken(false);

        pressedCable.Getcircles()[0].setCable(null);
        pressedCable.Getcircles()[1].setCable(null);

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

    public static double findNearestNumber(List<Double> arr, double num) {
        if (arr == null || arr.isEmpty()) {
            //throw new IllegalArgumentException("El arreglo no puede estar vacío.");
            System.out.println("El arreglo no puede estar vacío.");
            return 0;
        }

        double closestNumber = arr.get(0);
        double difference = Math.abs(closestNumber - num);

        for (int i = 1; i < arr.size(); i++) {
            double currentDifference = Math.abs(arr.get(i) - num);

            if (currentDifference < difference) {
                difference = currentDifference;
                closestNumber = arr.get(i);
            }
        }

        return closestNumber;
    }

    public static String createRandomID() {return UUID.randomUUID().toString();}
}