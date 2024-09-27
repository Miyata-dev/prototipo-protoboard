package com.example.prototipo;

import java.util.ArrayList;

public class GridPaneObserver {
    private GridPaneTrailController firstGridPane;
    private GridPaneTrailController secondGridPane;
    private GridPaneController firsGridPaneVolt;
    private GridPaneController secondGridPaneVolt;
    private ArrayList<Cable> cables = new ArrayList<>();
    //guarda las columnas con energía y la energía que tienen.
    private ArrayList<Pair<Integer, ArrayList<CustomCircle>>> energizedColumns = new ArrayList<>();

    public GridPaneObserver(GridPaneTrailController firstGridPane, GridPaneTrailController secondGridPane, GridPaneController firsGridPaneVolt, GridPaneController secondGridPaneVolt) {
        this.firstGridPane = firstGridPane;
        this.secondGridPane = secondGridPane;
        this.firsGridPaneVolt = firsGridPaneVolt;
        this.secondGridPaneVolt = secondGridPaneVolt;
    }
    //setters
    public void setCables(ArrayList<Cable> cables) {
        this.cables = cables;
    }

    public void addCable(Cable cable) {
        cables.add(cable);
    }

    public void removeCable(Cable cable) {
        cables.remove(cable);
    }
    //TODO ver donde ejecutar este método. este método agrega una columna con su respectiva energía.
    public void addColumn(ArrayList<CustomCircle> column, Integer energy) {
        energizedColumns.add(new Pair<>(energy, column));
    }

    //quita temporalmente la energía (desactiva la energía) //TODO REVISAR.
    public void deactivateGridObserver() {
        //remueve la energía que tiene el gridPane
        energizedColumns.forEach(pair -> {
            ArrayList<CustomCircle> col = pair.getSecondValue();

            col.forEach(CustomCircle::removeEnergy);
        });
    }
    //activa la energía
    public void activateGridObserver() {
        energizedColumns.forEach(pair -> {
            Integer energy = pair.getFirstValue();
            ArrayList<CustomCircle> col = pair.getSecondValue();

            col.forEach(cir -> cir.setState(energy));
        });
    }

    //getters
    public GridPaneController[] getGridPaneTrails() {
        return new GridPaneController[] {
                firsGridPaneVolt,
                secondGridPaneVolt
        };
    }

    public GridPaneTrailController[] getFirstGridPaneTrails() {
        return new GridPaneTrailController[] {
                firstGridPane,
                secondGridPane
        };
    }

    public GridPaneTrailController getFirstGridPaneTrail() {
        return firstGridPane;
    }

    public GridPaneTrailController getSecondGridPaneTrail() {
        return secondGridPane;
    }

    public GridPaneController getFirsGridPaneVolt() {
        return firsGridPaneVolt;
    }

    public GridPaneController getSecondGridPaneVolt() {
        return secondGridPaneVolt;
    }

    public ArrayList<Cable> getCables() {
        return cables;
    }
}