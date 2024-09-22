package com.example.prototipo;

import java.util.ArrayList;

public class GridPaneObserver {
    private GridPaneTrailController firstGridPane;
    private GridPaneTrailController secondGridPane;
    private GridPaneController firsGridPaneVolt;
    private GridPaneController secondGridPaneVolt;
    private ArrayList<Cable> cables = new ArrayList<>();

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