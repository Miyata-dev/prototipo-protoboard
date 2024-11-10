
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

    public static void unPaintCircles(GridPaneObserver gridPaneObserver, CustomCircle circle/*, boolean ignoreVoltCables*/) {
        ID circleID = circle.getID();

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
        gridPaneObserver.removeColumn(circles);
    }
    public static ArrayList<Cable> getConnectedCables(ArrayList<Cable> cables, Cable cableToConnect,GridPaneObserver gridPane, Boolean isFromSwitch) {
        HashSet<Cable> connectedCablesHashSet = new HashSet<>();

        //recorre todos los elementos de la colección de cables entregada.
        for (int i = 0; i < cables.size(); i++) {
            //mira que los cables estén conectados entre si Y ADEMÁS, que no sea un cable que provenga de los gridVolts.
            if (Cable.areConnected(cables.get(i), cableToConnect,gridPane)) {
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
                        //se agregan a la colección de cables si está la opción de ignorar el caso de que pertenezcan a los volts.
                        if (Cable.areConnected(arrayListAux.get(j), cable,gridPane)) {
                            if(!isFromSwitch && cable.getIds()[0].getGridName().equals("switchvolt1") || cable.getIds()[1].getGridName().equals("switchvolt1")) {
                                gridPane.getSwitches().forEach(switc -> {
                                    switc.getCables().forEach(cable1 -> {
                                        if(cable1.getRandomID().equals(cable.getRandomID())){
                                            if(switc.getConnectedCablesSwitch() != null){
                                                connectedCablesHashSet.addAll(switc.getConnectedCablesSwitch());
                                            }
                                        }
                                    });
                                });
                            }
                            connectedCablesHashSet.add(cable);
                        }
                    }
                }
            }
        }

        return new ArrayList<>(connectedCablesHashSet);
    }

    public static ArrayList<Cable> getCablesFromCollection(ArrayList<CustomCircle> circles) {
        ArrayList<Cable> cables = new ArrayList<>();

        circles.forEach(cir -> {
            if (cir.hasCable()) {
                cables.add(cir.getCable());
            }
        });

        return cables;
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

        System.out.println("id from node presses: " + pressedCable.getRandomID());

        //se recupera las ids del cable para poder ver donde pasar la energia.
        ID firstID = pressedCable.getIds()[0]; //firstCircle = pressedCable.getFirstCircle();
        ID secondID = pressedCable.getIds()[1];

        //como se llama a esta instrucción muchas veecs dentro del programa y no es necesario
        //un método para la instrucción, entonces se ocupa el Consumer.
        Consumer<Cable> deleteCableFromGridPane = (cable) -> {
            ResetStateCustomCircles(cable);
            gridPaneObserver.removeCable(cable);
            ((AnchorPane) pressedNode.getParent()).getChildren().remove(cable);
        };

        Consumer<Resistencia> deleteResistenciaFromGridPane = (resistencia) -> {
            ResetStateCustomCircles(resistencia);
            gridPaneObserver.removeResistencia(resistencia);
            gridPaneObserver.removeCable(resistencia);
            ((AnchorPane) resistencia.getParent()).getChildren().remove(resistencia.getRec());
            ((AnchorPane) resistencia.getParent()).getChildren().remove(resistencia.getArrow());
            ((AnchorPane) resistencia.getParent()).getChildren().remove(resistencia);
        };
        System.out.println("tipo: " + pressedCable.getTipo());
        //si es un cable entra aquí.
        if (pressedCable.getTipo() == null) {
            //se tiene que obtener el cable de la coleccion, sino la información de los círculos se pierde.
            Cable cableFound = getCableByID(cables, pressedCable);
            //se obtiene
            CustomCircle firstCircle = cableFound.getFirstCircle();
            CustomCircle secondCircle = cableFound.getSecondCircle();

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
            ArrayList<Cable> connectedCables = getConnectedCables(cables, pressedCable,gridPaneObserver, false);

            for (Cable cable : connectedCables) {
                unPaintCircles(gridPaneObserver, cable.getSecondCircle());
                unPaintCircles(gridPaneObserver, cable.getFirstCircle());
            }
            ResetStateCustomCircles(cableFound);
            gridPaneObserver.removeCable(cableFound);
            ((AnchorPane) pressedNode.getParent()).getChildren().remove(pressedNode);
        } else { //si el tipo NO ES null quiere decir que es una resistencia.
            System.out.println("removing resistencia...");

            Resistencia resistenciaFound = null;

            for (Resistencia c : gridPaneObserver.getResistencias()) {
                System.out.println("resistencia: " + c + " id: " + c.getRandomID());
                if (c.equals(pressedCable)) {
                    resistenciaFound = c;
                }
            }
            CustomCircle firstCircle = resistenciaFound.getFirstCircle();
            CustomCircle secondCircle = resistenciaFound.getSecondCircle();

            //se mira el caso específico que el cable esté conectado gridVolt -> led o led -> gridVolt.
            if (firstID.getGridName().contains(gridPaneObserver.getGridVoltPrefix()) && secondID.getGridName().contains(gridPaneObserver.getLedIdPrefix())) {
                deleteResistenciaFromGridPane.accept(resistenciaFound);
                return;
            } else if (secondID.getGridName().contains(gridPaneObserver.getGridVoltPrefix()) && firstID.getGridName().contains(gridPaneObserver.getLedIdPrefix())) {
                deleteResistenciaFromGridPane.accept(resistenciaFound);
                return;
            }

            //se mira el caso específico que el cable esté conectado gridVolt -> switch o switch -> gridVolt.
            if (firstID.getGridName().contains(gridPaneObserver.getGridVoltPrefix()) && secondID.getGridName().contains(gridPaneObserver.getSwitchIdPrefix())) {
                deleteResistenciaFromGridPane.accept(resistenciaFound);
                return;
            } else if (secondID.getGridName().contains(gridPaneObserver.getGridVoltPrefix()) && firstID.getGridName().contains(gridPaneObserver.getSwitchIdPrefix())) {
                deleteResistenciaFromGridPane.accept(resistenciaFound);
                return;
            }

            //antes de entrar a los casos de gridPanes, se mira que uno de los cables esté conectado a un led.
            if (firstID.getGridName().contains(gridPaneObserver.getLedIdPrefix())) {
                System.out.println("first id is from a led");
                unPaintCircles(gridPaneObserver, secondCircle);
                deleteResistenciaFromGridPane.accept(resistenciaFound);
                return;
            } else if (secondID.getGridName().contains(gridPaneObserver.getLedIdPrefix())) {
                System.out.println("second id is from a led");
                unPaintCircles(gridPaneObserver, firstCircle);
                deleteResistenciaFromGridPane.accept(resistenciaFound);
                return;
            }
            //mira si la primera ID pertenece a un switch, si pertenece a uno, entonces
            if (firstID.getGridName().contains(gridPaneObserver.getSwitchIdPrefix())) {
                System.out.println("first id is from a switch");
                unPaintCircles(gridPaneObserver, secondCircle);
                deleteResistenciaFromGridPane.accept(resistenciaFound);
                return;
            } else if (secondID.getGridName().contains(gridPaneObserver.getSwitchIdPrefix())) {
                System.out.println("second id is from a switch");
                unPaintCircles(gridPaneObserver, firstCircle);
                deleteResistenciaFromGridPane.accept(resistenciaFound);
                return;
            }
            ArrayList<Cable> connectedCables = getConnectedCables(cables, pressedCable,gridPaneObserver, false);

            for (Cable cable : connectedCables) {
                unPaintCircles(gridPaneObserver, cable.getSecondCircle());
                unPaintCircles(gridPaneObserver, cable.getFirstCircle());
            }

            deleteResistenciaFromGridPane.accept(resistenciaFound);
        }
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


    //Este metodo lo que hace es conseguir el circulo mas cercano a una coordenada
    public static CustomCircle getClosestCircle(List<CustomCircle> circles, double x, double y) {
        CustomCircle closestCircle = circles.get(0);

        double minDistance = Math.sqrt(Math.pow(closestCircle.getX() - x, 2) + Math.pow(closestCircle.getY() - y, 2));

        for (int i = 1; i < circles.size(); i++) {
            double currentDistance = Math.sqrt(Math.pow(circles.get(i).getX() - x, 2) + Math.pow(circles.get(i).getY() - y, 2));

            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                closestCircle = circles.get(i);
            }
        }

        return closestCircle;
    }


    public static String createRandomID() {return UUID.randomUUID().toString();}
}