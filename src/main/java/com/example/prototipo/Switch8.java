package com.example.prototipo;

import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Switch8 extends Chip{

    private GridPaneObserver gridPaneObserver;
    private Basurero basurero;
    private ArrayList<Pair<Boolean,Rectangle>> activatedButtons = new ArrayList<>(); // True = pasando energia, false = no pasando energia
    private ArrayList<Pair<Boolean,Rectangle>> burnedButtons = new ArrayList<>(); // True = quemado, false = no quemado

    public Switch8(CustomShape customShape, Basurero basurero, GridPaneObserver gridPaneObserver) {
        super(customShape, basurero, gridPaneObserver, 8);
        this.basurero = basurero;
        this.gridPaneObserver = gridPaneObserver;
        createButtons(customShape);

        this.setOnMouseReleased(e -> {
            super.mouseReleased(e);
            super.getCols(); //se o
            checkColumns();
        });

        Runnable removeAffectedCols = () -> {
            System.out.println("number of ghostCables " + getGhostCables().size());
        };

        this.setOnMouseClicked(e -> {
            if (getAffectedColumns() == null) return;
            System.out.println("number of ghost cables: " + getGhostCables().size());
            System.out.println("number of pairs: " + activatedButtons.size());

            checkColumns();

            super.mouseClicked(e, customShape, removeAffectedCols);
        });

        activatedButtons.forEach(pair->{
            pair.getSecondValue().setOnMouseClicked(mouseEvent -> {
                System.out.println("CLICKEANDO");
                pair.setFirstValue(!pair.getFirstValue());
                System.out.println(pair.getFirstValue());

                if(pair.getFirstValue()){
                    System.out.println("painting...");
                    pair.getSecondValue().setFill(Color.GRAY);
                    pair.getSecondValue().setY(pair.getSecondValue().getY()-2);
                    checkColumns();
                }else{
                    System.out.println("unpainting...");
                    pair.getSecondValue().setFill(Color.WHITE);
                    pair.getSecondValue().setY(pair.getSecondValue().getY()+2);
                    checkColumns();
                }

                new ArrayList<>(gridPaneObserver.getCables()).forEach(n ->
                        GridPaneObserver.refreshProtoboard(gridPaneObserver)
                );
            });
        });

    }

    private void createButtons(CustomShape customShape) {
        Bounds parentBounds = customShape.getBoundsInParent();
        double x = parentBounds.getMinX() + 6;
        double y = parentBounds.getMinY();

        for (int i = 0 ; i < 8 ; i++) {
            Rectangle rectangle = new Rectangle(x , (y + 10), 8, 20);
            rectangle.setFill(Color.WHITE);
            x = x + 18;
            activatedButtons.add(new Pair<>(false, rectangle));
            this.getChildren().add(rectangle);
        }

    }

    //conecta artificialmente a los cables reales del protoboard con un cable fantasma.
    //false = viene de abajo, true = viene de arriba
    public void connectWithGhostCable(int index, int state, boolean isFromUpper) {
        List<ArrayList<CustomCircle>> lowerCols = super.getLowerCols();
        List<ArrayList<CustomCircle>> upperCols = super.getUpperCols();

        Function<ArrayList<CustomCircle>, CustomCircle> getFirstCircle = (a) -> a.get(0);

        Cable cable = new Cable(getFirstCircle.apply(lowerCols.get(index)), getFirstCircle.apply(upperCols.get(index)));
        cable.setRandomID();
        cable.setIsGhostCable(true);

        //se pasas como llave la ID del primer circulo para dsp poder conseguirlo a la hora de eliminarlo.
        CustomCircle circleToConnect = isFromUpper
            ? getFirstCircle.apply(upperCols.get(index))
            : getFirstCircle.apply(lowerCols.get(index));

        ArrayList<CustomCircle> circlesToPower = isFromUpper
            ? upperCols.get(index)
            : lowerCols.get(index);

        addGhostCableToMap(getFirstCircle.apply(upperCols.get(index)).getID(), cable);
        System.out.println("key of ghostCable: " + getFirstCircle.apply(upperCols.get(index)).getID());
        //si no se tiene un estado en especifico, se toma el primer circulo de la columna.
        if (state == 0) {
            cable.setTipodecarga(circleToConnect.getState());
        } else {
            cable.setTipodecarga(state);
        }

        gridPaneObserver.addCable(cable);
        addGhostCable(cable);
        gridPaneObserver.addColumn(circlesToPower, state);
    }

    //a partir de un indice se revisa la columna de arriba y abajo de dicho indice.
    public void checkColumn(int index, boolean isActivated) {
        List<ArrayList<CustomCircle>> lowerCols = super.getLowerCols();
        List<ArrayList<CustomCircle>> upperCols = super.getUpperCols();

        Function<ArrayList<CustomCircle>, CustomCircle> getFirstCircle = (a) -> a.get(0);
        Predicate<ArrayList<CustomCircle>> hasEnergy = (a) -> getFirstCircle.apply(a).hasEnergy();

        ArrayList<CustomCircle> lowerCol = lowerCols.get(index);
        ArrayList<CustomCircle> upperCol = upperCols.get(index);

        if (hasEnergy.test(lowerCol) && !hasEnergy.test(upperCol) && isActivated) {
            System.out.println("index: " + index + "painting upperCol");
            int state = getFirstCircle.apply(lowerCol).getState();
            connectWithGhostCable(index, state,false);

            upperCol.forEach(cir -> {
                cir.setIsAffectedByChip(true);
                cir.setState(state);
            });

            gridPaneObserver.addColumn(upperCol, state);
        }

        if (hasEnergy.test(upperCol) && !hasEnergy.test(lowerCol) && isActivated) {
            System.out.println("index: " + index + "painting lowercol");
            int state = getFirstCircle.apply(upperCol).getState();
            connectWithGhostCable(index, state,true);

            lowerCol.forEach(cir -> {
                cir.setIsAffectedByChip(true);
                cir.setState(state);
            });
            gridPaneObserver.addColumn(lowerCol, state);
        }

        if (!isActivated) {
            //System.out.println("unpainting from checkColumn...");
            CustomCircle circle = getFirstCircle.apply(upperCol);
            //System.out.println("key: " + circle.getID());
            disconnectGhostCable(circle.getID());
        }

    }

    public void checkColumns(){
        List<ArrayList<CustomCircle>> lowerCols = super.getLowerCols();
        List<ArrayList<CustomCircle>> upperCols = super.getUpperCols();

        for (Pair<Boolean, Rectangle> pair : activatedButtons) {
            int index = activatedButtons.indexOf(pair);
            boolean isActivated = pair.getFirstValue();

            checkColumn(index, isActivated);
        }
    }
}
