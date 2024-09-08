package com.example.prototipo;

public class ID {
    private int indexRow, indexColumn;
    private String gridName;
    private String generatedID;//La id generado se agregan como id a los CustomCircles
    private boolean isForGridpane; //como se usan en los leds, en ese caso este atributo es falso.

    //se busca crear una ID con los indices separados y el nombre del GridPane con un gion ej: "indexRow-indexCol-gridName"
    public ID(int indexRow, int indexColumn, String gridName) {
        this.indexRow = indexRow;
        this.indexColumn = indexColumn;
        this.gridName = gridName;
        this.generatedID = indexRow + "-" + indexColumn + "-" + gridName;
        this.isForGridpane = true;
    }

    //para que este constructor funcione se debe de separar los indices con - en el string computedID.
    public ID(String computedID) {
        String[] indexes = computedID.split("-");
        String rowIndex = indexes[0];
        String columnIndex = indexes[1];
        String gridName = indexes[2];
        this.indexRow = Integer.parseInt(rowIndex);
        this.indexColumn = Integer.parseInt(columnIndex);
        this.gridName = gridName;
        this.generatedID = rowIndex + "-" + columnIndex + "-" + gridName;
        this.isForGridpane = true;
    }
    //setters
    public void setIsForGridpane(boolean isForGridpane) {
        this.isForGridpane = isForGridpane;
    }
    //Getters
    public String getGeneratedID() {
        return generatedID;
    }

    public String getGridName() {
        return gridName;
    }

    public int getIndexRow() {
        return indexRow;
    }

    public int getIndexColumn() {
        return indexColumn;
    }
    public boolean getIsForGridpane(){ return this.isForGridpane; }


    //compara si dos Id son iguales o diferentes
    public static boolean isSameID(ID idUno, ID idDos) {
        return idUno.generatedID.compareTo(idDos.generatedID) == 0;
    }

    public static boolean isSameColumn(ID idUno, ID idDos) {
        int firstIDcolumnIndex = idUno.getIndexColumn();
        int secondIDcolumnIndex = idDos.getIndexColumn();

        return firstIDcolumnIndex == secondIDcolumnIndex;
    }

    public static boolean sameGridPane(ID idUno, ID idDos) {
        return idUno.getGridName().compareTo(idDos.getGridName()) == 0;
    }

    public static boolean isThisColumn(ID id, int desiredColumn) {
        return id.getIndexColumn() == desiredColumn;
    }

    @Override
    public String toString() {
        return "Row: " + this.getIndexRow() + " Column: " + this.getIndexColumn();
    }
}