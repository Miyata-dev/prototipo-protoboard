package com.example.prototipo;

public class GridPaneObserver {
    private GridPaneTrailController firstGridPane;
    private GridPaneTrailController secondGridPane;
    private GridPaneController firsGridPaneVolt;
    private GridPaneController secondGridPaneVolt;

    public GridPaneObserver(GridPaneTrailController firstGridPane, GridPaneTrailController secondGridPane, GridPaneController firsGridPaneVolt, GridPaneController secondGridPaneVolt) {
        this.firstGridPane = firstGridPane;
        this.secondGridPane = secondGridPane;
        this.firsGridPaneVolt = firsGridPaneVolt;
        this.secondGridPaneVolt = secondGridPaneVolt;
    }

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
}
