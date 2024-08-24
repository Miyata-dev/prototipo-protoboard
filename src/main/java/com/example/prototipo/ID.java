package com.example.prototipo;

public class ID { //TODO agregar nombre de gridpane para diferenciar entre circulos de distintos gridpanes.
    private int indexRow, indexColumn;
    private String gridName;
    private String generatedID; //para agregarlo como id a los circulos.

    //se busca crear una ID con los indices separados con un gion ej: "indiceRoe-indiceCol"
    public ID(int indexRow, int indexColumn, String gridName) {
        this.indexRow = indexRow;
        this.indexColumn = indexColumn;
        this.gridName = gridName;
        this.generatedID = indexRow + "-" + indexColumn + "-" + gridName;
    }

    //para que este constructor funcione se debe de separar los indices con - en el string computed.
    public ID(String computedID) {
        String[] indexes = computedID.split("-");
        String rowIndex = indexes[0];
        String columnIndex = indexes[1];
        String gridName = indexes[2];
        this.indexRow = Integer.parseInt(rowIndex);
        this.indexColumn = Integer.parseInt(columnIndex);
        this.generatedID = rowIndex + "-" + columnIndex + "-" + gridName;
    }

    public String getGeneratedID() {
        return generatedID;
    }

    public int getIndexRow() {
        return indexRow;
    }

    public int getIndexColumn() {
        return indexColumn;
    }

    public static boolean isSameID(ID idUno, ID idDos) {
        return idUno.generatedID.compareTo(idDos.generatedID) == 0;
    }

    public static boolean isSameColumn(ID idUno, ID idDos) {
        int firstIDcolumnIndex = idUno.getIndexColumn();
        int secondIDcolumnIndex = idDos.getIndexColumn();

        return firstIDcolumnIndex == secondIDcolumnIndex;
    }

    public static boolean isThisColumn(ID id, int desiredColumn) {
        return id.getIndexColumn() == desiredColumn;
    }

    @Override
    public String toString() {
        return "Row: " + this.getIndexRow() + " Column: " + this.getIndexColumn();
    }
}