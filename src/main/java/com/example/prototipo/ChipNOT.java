package com.example.prototipo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ChipNOT extends Chip {
    private GridPaneObserver gridPaneObserver;
    private Basurero basurero;

    public ChipNOT(CustomShape customShape, Basurero basurero, GridPaneObserver gridPaneObserver) {
        super(customShape, basurero, gridPaneObserver);

        this.gridPaneObserver = gridPaneObserver;
        this.basurero = basurero;
        String tipo = "NOT";

        this.setType(tipo);
        addText(); //el texto que se despliega depende del texto dado por

        this.setOnMouseReleased(e -> {
            System.out.println("im here....");
            super.mouseReleased(e);
            getCols();
            checkColumns();
            //quita la energía de las columnas afectadas.
        });

        this.setOnMouseClicked(e -> {
            if (super.getAffectedColumns() == null) return;

            super.mouseClicked(e, customShape);
        });
    }

    public void checkColumn(List<ArrayList<CustomCircle>> arr, int index) {
        if (arr.isEmpty()) return;

        Function<ArrayList<CustomCircle>, CustomCircle> getFirstCircle = (a) -> a.get(0);
        Predicate<ArrayList<CustomCircle>> hasEnergy = (a) -> getFirstCircle.apply(a).hasEnergy();

        ArrayList<CustomCircle> firstColToCheck = arr.get(index - 1); //un indice atras de la columna que estamos revisando
        ArrayList<CustomCircle> columnToPower = arr.get(index);

        int energy = getFirstCircle.apply(firstColToCheck).getState();

        //debe se asi ya que se revisan las 2 columnas de atras del indice.
        if (index - 1 < 0) return;
        super.getAffectedColumns().add(arr.get(index)); //se agrega a las columnas afectadas.

        if (hasEnergy.test(firstColToCheck)) {
            columnToPower.forEach(cir -> {
                cir.setIsAffectedByChip(true);
                cir.setState(-energy);
            });

            connectWithGhostCable(arr, index, index -1, -energy);
        }

        if (!hasEnergy.test(firstColToCheck)) {
            Utils.unPaintCircles(gridPaneObserver, columnToPower.get(0)); //se pasa el primer circulo de la columna inspeccionada.
            gridPaneObserver.removeColumn(columnToPower);
        }
    }

    @Override
    public void checkColumns() {
        List<ArrayList<CustomCircle>> lowerCols = super.getLowerCols();
        List<ArrayList<CustomCircle>> upperCols = super.getUpperCols();

        //si no tiene columns abajo o arriba entonces no está colocado bien y por lo tanto se sale de la función.
        if (lowerCols == null || upperCols == null) return;

        int[] indexesToCheck = { 1, 3, 5 }; //estos son los indices a mirar en el caso del chip NOT.

        for (int i = 0; i < indexesToCheck.length; i++) {
            checkColumn(lowerCols, indexesToCheck[i]);
        }

        for (int i = 0; i < indexesToCheck.length; i++) {
            checkColumn(upperCols, indexesToCheck[i] + 1);
        }
    }

}