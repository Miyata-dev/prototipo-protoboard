package com.example.prototipo;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

//TODO Prueba de posible error
public class Chip extends Group {
    private ArrayList<Rectangle> patitas = new ArrayList<>();
    private ArrayList<Double[]> coords = new ArrayList<>(); // coords[0]= x, coords[1] = y
    private ArrayList<CustomCircle> closeCircles = new ArrayList<>();
    private ArrayList<Cable> ghostCables = new ArrayList<>();
    private ArrayList<ArrayList<CustomCircle>> columns = new ArrayList<>();
    private List<ArrayList<CustomCircle>> lowerCols, upperCols, affectedColumns = new ArrayList<>();
    private Basurero basurero;
    private GridPaneObserver gridPaneObserver;
    private boolean isPlacedCorrectly = true, isUndraggable = false;
    private String type, randomID; //las opciones son: AND || OR || NOT.
    private CustomShape customShape;
    private int numeroPatitas = 7; //numeroPatitas se refiere al numero de patitas que tiene un lado del chip (arriba o abajo) por defecto es sero, pero se puede cambiar

    //para ejecutar código después de un tiempo.
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    //se debe especificar el tipo a travez del setter: setType.
    public Chip(CustomShape customShape, Basurero basurero, GridPaneObserver gridPaneObserver, int numeroPatitas) {
        super(customShape);
        this.customShape = customShape;
        this.basurero = basurero;
        this.gridPaneObserver = gridPaneObserver;
        this.numeroPatitas = numeroPatitas;
        addPatitas(customShape);
        this.setTranslateY(-100);
        this.randomID = Utils.createRandomID(); //genera una id random para encontrarlo
        Utils.makeDraggableNode(this, customShape.getStartX(), customShape.getStartY());
        manageEvents(customShape);
    }


    public void manageEvents(CustomShape customShape) {
        //Conseguimos la coleccion de circulos
        ArrayList<CustomCircle> circlesFromObserver = gridPaneObserver.getCirclesCollection();

        this.setOnMouseClicked(e -> {
            mouseClicked(e, customShape);
        });

        this.setOnMouseReleased(this::mouseReleased);
    }

    public void mouseClicked(MouseEvent e, CustomShape customShape, Runnable func) {
        //quita las columnas afectadas.
        Runnable removeAffectedCols = () -> {
            //se reinicia el estado "IsAffectedByChip" de los circulos de las columnas.
            affectedColumns.forEach(col -> {
                Utils.unPaintCircles(gridPaneObserver, col.get(0));
                gridPaneObserver.removeColumn(col);
                col.forEach(cir -> cir.setIsAffectedByChip(false));
            });

            lowerCols.forEach(col -> {
                Utils.unPaintCircles(gridPaneObserver, col.get(0));
                col.forEach(
                    cir -> cir.setIsAffectedByChip(false)
                );
            });

            upperCols.forEach(col -> {
                Utils.unPaintCircles(gridPaneObserver, col.get(0));
                col.forEach(
                    cir -> cir.setIsAffectedByChip(false)
                );
            });

            affectedColumns.clear();
            lowerCols.clear();
            upperCols.clear();
            gridPaneObserver.getCables().removeAll(ghostCables);
            ghostCables.clear();
        };

        Consumer<MouseEvent> doSomething = (ev) -> {
            if (basurero.getIsActive() && customShape.getHasMoved()) {
                gridPaneObserver.getRoot().getChildren().remove(this);
                closeCircles.forEach(circle -> {
                    circle.setisTaken(false);
                });

                removeAffectedCols.run();

                if (func != null) {
                    func.run();
                }

                new ArrayList<>(gridPaneObserver.getCables()).forEach(n ->
                        GridPaneObserver.refreshProtoboard(gridPaneObserver)
                );
            }

            if (basurero.getIsActive()) customShape.setHasMoved(true);
        };

        doSomething.accept(e);
    }

    public void mouseClicked(MouseEvent e, CustomShape customShape) {
        mouseClicked(e, customShape, null);
    }

    //MOUSE RELEASED SUELTA EL CHIP SOLO SI TIENE EL MISMO NÚMERO DE CIRCULSO EN CLOSED CIRCLES, MIRAR CUANDO CAMBIES EL NUMERO DE CÍRCULOS
    //EN EL MÉTODO addPatitas.
    public void mouseReleased(MouseEvent e) {
        ArrayList<CustomCircle> circlesFromObserver = gridPaneObserver.getCirclesCollection();

        Consumer<MouseEvent> doSomething = (ev) -> {
            closeCircles.clear();
            coords.clear();  // Limpia coords antes de llenarlo de nuevo
            isPlacedCorrectly = true; //para reiniciar la variable.
            isUndraggable = false;
            //por cada pata, se obtienen las coordenadas y se guardan en una colección.
            patitas.forEach(pata -> {
                //se obtienen las coordenadas de la pata que se mira del chip para agregarlas a la colección de coordenadas.
                double coordX = pata.localToScreen(pata.getX(), pata.getY()).getX();
                double coordY = pata.localToScreen(pata.getX(), pata.getY()).getY();

                Double[] currentCoords = new Double[]{ coordX, coordY };

                coords.add(currentCoords);
            });

            double maxRange = (circlesFromObserver.get(0).getRadius() * 2) - patitas.get(0).getHeight() + 2; //ese es el rango maximo que puede tener.
            //TODO mejorar el algoritmo.
            for (Double[] coord : coords) {
                //obtiene los numeros mas cercanos
                CustomCircle closestCircle = Utils.getClosestCircle(gridPaneObserver.getCirclesCollection(), coord[0], coord[1]);

                double distanceY = Math.abs(coord[1] - closestCircle.getY());

                //Si al menos uno de los circulos encontrados tiene un cable se sale del metodo
                if (closestCircle.hasCable()) return;

                //se mira que la distancia entre el las coordenadas x e y sean válidas
                if (distanceY >= maxRange) {
                    isPlacedCorrectly = false;
                    return;
                } else {
                    if (!closeCircles.contains(closestCircle)) { //solo se agregan los circulos si no están repetidos en la colección.
                        closeCircles.add(closestCircle);
                    }
                };
            }

            if (isPlacedCorrectly) {
                //se toman los gridNames del primer círculo de arriba y el último círculo de abajo.,
                String firstCircleGridName = closeCircles.get(0).getID().getGridName();
                String secondCircleGridName = closeCircles.get(closeCircles.size() - 1).getID().getGridName();

                //si pertenecen al mismo gridPane no se suelta el Chip.
                if (firstCircleGridName.equals(secondCircleGridName)) return;

                if (closeCircles.size() == numeroPatitas * 2) { //numeroPatitas se refiere al numero de patitas que tiene un lado del chip (arriba o abajo)
                    //por cada circulo se le setea el estado de ocupado.
                    closeCircles.forEach(circle -> {
                        circle.setisTaken(true);
                    });

                    //Unicamente el Chip deberia ser Undraggable cuando la cantidad de circulos encontrados son 8
                    Utils.makeUndraggableNode(this);
                    isUndraggable = true;
                    //obtiene las columnas de cada círculo.
                    closeCircles.forEach(cir -> {
                        ID temporaryID = cir.getID();
                        //solo pinta una vez el color naranja solo si el círculo no tiene energía.
                        if (!scheduler.isShutdown() && !cir.hasEnergy()) {
                            cir.setFill(Color.ORANGE);
                        }
                        ArrayList<CustomCircle> column = GridPaneObserver.getCircles(gridPaneObserver, temporaryID);
                        columns.add(column);
                    });
                    //si es scheduler está apagado, entonces se sale de la función.
                    if (scheduler.isShutdown()) return;

                    scheduler.schedule(() -> {
                        closeCircles.forEach(cir -> {
                            if (cir.hasEnergy()) return;
                            cir.setFill(Color.GRAY);
                        });
                        scheduler.shutdown(); //cierra el scheduler después de despintarlos.
                    }, 300, TimeUnit.MILLISECONDS);
                }
            }
        };

        doSomething.accept(e);
    }

    //Este metodo lo que hace es crear patas y despues añadirla al grupo
    public void addPatitas(CustomShape customShape) {
        int initailX = 4;
        int initialY = 7;
        int incrementX = 18; //12 -> 18 6 * 4
        double heightDifference = customShape.getHeight() + 7; //para poder crear las patas de artriba y abajo se tiene la diferencia de altura.

        //este primer ciclo crea las patas de la parte superior del Chip
        for (int i = 0; i < numeroPatitas; i++) {
            crearPatita(customShape,initailX + incrementX * i, initialY);
        }
        //crea las patas de la parte inferior del Chip.
        for (int i = 0; i < numeroPatitas; i++) {
            crearPatita(customShape,initailX + incrementX * i, initialY - heightDifference);
        }
    }

    //TODO implementar el pintar.
    public void getCols() {
        //si no está colocado bien no hace nada.
        if (!isUndraggable) return;
        ArrayList<ArrayList<CustomCircle>> cols = columns;

        String upperColGridName = columns.get(0).get(0).getID().getGridName();
        String lowerColGridName = columns.get(cols.size() - 1).get(0).getID().getGridName();
        //obtiene las columnas de abajo.
        lowerCols = cols.stream()
                .filter(col -> col.get(0).getID().getGridName().equals(lowerColGridName)).collect(Collectors.toList());

        upperCols = cols.stream()
                .filter(col -> col.get(0).getID().getGridName().equals(upperColGridName)).collect(Collectors.toList());

        lowerCols.forEach(col -> col.forEach(
            cir -> cir.setIsAffectedByChip(true)
        ));

        upperCols.forEach(col -> col.forEach(
            cir -> cir.setIsAffectedByChip(true)
        ));
    }

    public void addGhostCable(Cable cable) {
        if (Cable.getCableFromCollection(ghostCables, cable) == null) {
            ghostCables.add(cable);
        }
    }
    //conecta artificialmente a los cables reales del protoboard con un cable fantasma.
    public void connectWithGhostCable(List<ArrayList<CustomCircle>> arr, int index, int indexToConnect, int state) {
        Function<ArrayList<CustomCircle>, CustomCircle> getFirstCircle = (a) -> a.get(0);

        Cable cable = new Cable(getFirstCircle.apply(arr.get(indexToConnect)), getFirstCircle.apply(arr.get(index)));
        cable.setRandomID();
        cable.setIsGhostCable(true);
        //si no se tiene un estado en especifico, se toma el primer circulo de la columna.
        if (state == 0) {
            cable.setTipodecarga(getFirstCircle.apply(arr.get(indexToConnect)).getState());
        } else {
            cable.setTipodecarga(state);
        }

        gridPaneObserver.addCable(cable);
        addGhostCable(cable);
        gridPaneObserver.addColumn(arr.get(index), state);
    }

    public void connectWithGhostCable(List<ArrayList<CustomCircle>> arr, int index, int indexToConnect) {
        connectWithGhostCable(arr, index, indexToConnect,0);
    }

    public void addText() {
        Bounds parentBounds = customShape.getBoundsInParent();
        double y = parentBounds.getCenterY();
        double x = parentBounds.getCenterX();

        Text text = new Text(x, y + 5, type);
        text.setFill(Color.WHITE);  // Color de texto blanco
        text.setFont(Font.font("System", FontWeight.BOLD, 14));

        this.getChildren().add(text);
    }

    //Este metodo crea una pata y la agrega al grupo de la clase Chip.
    private void crearPatita(CustomShape customShape, double xOffSet, double yOffSet) {

        //obtenemos la ubicacion del custom shape
        double widthX = customShape.getX();
        double heightY = customShape.getY();

        //Para asi poder darles una ubicacion a la figura que seria un rectangulo
        Rectangle rectangle = new Rectangle(7, 7);

        rectangle.setLayoutY(heightY - yOffSet);
        rectangle.setLayoutX(widthX + xOffSet);
        rectangle.setFill(Color.BLACK);

        patitas.add(rectangle);

        this.getChildren().add(rectangle);
    }
    //SETTERS
    public void setType(String type) {
        this.type = type;
    }

    public void setNumeroPatitas(int numeroPatitas) {
        this.numeroPatitas = numeroPatitas;
    }

    //GETTERS
    public String getType() { //este tipo va a definir el comportamiento del chip.
        return type;
    }

    public boolean isUndraggable() {
        return isUndraggable;
    }

    public ArrayList<ArrayList<CustomCircle>> getColumns() {
        return columns;
    }

    public List<ArrayList<CustomCircle>> getLowerCols() {
        return lowerCols;
    }

    public List<ArrayList<CustomCircle>> getUpperCols() {
        return upperCols;
    }

    public List<ArrayList<CustomCircle>> getAffectedColumns() {
        return affectedColumns;
    }

    public ArrayList<Cable> getGhostCables() {
        return ghostCables;
    }

    public ArrayList<CustomCircle> getCloseCircles() {
        return closeCircles;
    }

    public CustomShape getCustomShape() {
        return customShape;
    }

    public int getNumeroPatitas() {
        return numeroPatitas;
    }

    public void checkColumns() {
        System.out.println("checking columns...");
    }
}