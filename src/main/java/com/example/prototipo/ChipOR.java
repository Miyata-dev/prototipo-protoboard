package com.example.prototipo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ChipOR extends Chip{
    private GridPaneObserver gridPaneObserver;
    private Basurero basurero;
    private List<ArrayList<CustomCircle>> lowerCols, upperCols, affectedColumns = new ArrayList<>();

    public ChipOR(CustomShape customShape, Basurero basurero, GridPaneObserver gridPaneObserver) {
        super(customShape, basurero, gridPaneObserver);
        this.gridPaneObserver = gridPaneObserver;
        this.basurero = basurero;
        String tipo = "OR";

        this.setType(tipo);
        addText(); //el texto que se despliega depende del texto dado por

        this.setOnMouseReleased(e -> {
            super.mouseReleased(e);
            super.getCols(); //se o
            checkColumns();
        });

        Runnable removeAffectedCols = () -> {
            System.out.println("Removing affected columns");
        };

        this.setOnMouseClicked(e -> {
            if (affectedColumns == null) return;

            super.mouseClicked(e, customShape, removeAffectedCols);
        });
    }

    //revisa la columna indicada, si la anterior a esa y la antepenultima a esta tienen energía
    //la columna indicada a esta se le dará enegía automáticamente.
    private void checkColumn(List<ArrayList<CustomCircle>> arr, int index) {
        if (arr.isEmpty()) return;

        Function<ArrayList<CustomCircle>, CustomCircle> getFirstCircle = (a) -> a.get(0);
        Predicate<ArrayList<CustomCircle>> hasEnergy = (a) -> getFirstCircle.apply(a).hasEnergy();

        //a partir del indice, mira si las dos columas anteriores tienen energía distinta.
        Predicate<Integer> haveDifferenteEnergy = (a) -> {
            CustomCircle firstCircle = getFirstCircle.apply(arr.get(index - 1));
            CustomCircle secondCircle = getFirstCircle.apply(arr.get(index - 2));
            return firstCircle.getState() != secondCircle.getState();
        };

        //debe se asi ya que se revisan las 2 columnas de atras del indice.
        if (index - 2 < 0) return;
        super.getAffectedColumns().add(arr.get(index)); //se agrega a las columnas afectadas.

        //TODO mejorar la lógica del if de abajo.
        //si la 1ra y segunda tienen energía pero no tiene el mismo tipo de energía, se pasa negativo.
        if (hasEnergy.test(arr.get(index - 2)) && hasEnergy.test(arr.get(index - 1)) && haveDifferenteEnergy.test(index)) {
            //si ya tiene energía, se sale de la función
            if (hasEnergy.test(arr.get(index))) return;

            //la columna a inspeccionar se le setea como afectadaporchip y se pasa el estado.
            arr.get(index).forEach(cir -> {
                cir.setIsAffectedByChip(true);
                cir.setState(1);
            });
            connectWithGhostCable(arr, index, 1);
            gridPaneObserver.addColumn(arr.get(index), 1);
        }

        //si la primera o segunda columna tiene energía, se agrega la tercera.
        if (hasEnergy.test(arr.get(index - 2)) || hasEnergy.test(arr.get(index - 1))) { //n -2 y n -1
            //si ya tiene energía, se sale de la función
            if (hasEnergy.test(arr.get(index))) return;

            //se obtiene el circulo que tiene energía/
            CustomCircle energizedCircle = getFirstCircle.apply(arr.get(index - 2)).hasEnergy()
                ? getFirstCircle.apply(arr.get(index - 2))
                : getFirstCircle.apply(arr.get(index - 1));

            arr.get(index).forEach(cir -> {
                cir.setIsAffectedByChip(true);
                cir.setState(energizedCircle.getState());
            });
            connectWithGhostCable(arr, index, energizedCircle.getState());
            gridPaneObserver.addColumn(arr.get(index), energizedCircle.getState());
        }

        //en el caso de que ninguna de las columnas no tenga energia, se le quita la energía.
        if (!hasEnergy.test(arr.get(index - 2)) && !hasEnergy.test(arr.get(index - 1))) {
            //todo obtener los cables acociados paa quitarles la energía.
            Utils.unPaintCircles(gridPaneObserver, arr.get(index).get(0)); //se pasa el primer circulo de la columna inspeccionada.
            gridPaneObserver.removeColumn(arr.get(index));
        }
    }

    // si el 0, 1 tienen el 2 tiene automaticamente y asi sucesivamente.
    @Override
    public void checkColumns() {
        List<ArrayList<CustomCircle>> lowerCols = super.getLowerCols();
        List<ArrayList<CustomCircle>> upperCols = super.getUpperCols();

        //si no tiene columns abajo o arriba entonces no está colocado bien y por lo tanto se sale de la función.
        if (lowerCols == null || upperCols == null) return;

        int[] indexesToCheck = { 2, 5 }; //estos son los indices a mirar en el caso del chip AND.

        for (int i = 0; i < indexesToCheck.length; i++) {
            checkColumn(lowerCols, indexesToCheck[i]);
        }

        for (int i = 0; i < indexesToCheck.length; i++) {
            checkColumn(upperCols, indexesToCheck[i] + 1);
        }
    }

}