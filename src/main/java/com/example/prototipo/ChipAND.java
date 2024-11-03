package com.example.prototipo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ChipAND extends Chip { //implementar un equyals en la clase CHIP para poder hacer comparaciones.
    private GridPaneObserver gridPaneObserver;
    private Basurero basurero;
    private List<ArrayList<CustomCircle>> lowerCols, upperCols, affectedColumns = new ArrayList<>();

    public ChipAND(CustomShape customShape, Basurero basurero, GridPaneObserver gridPaneObserver) {
        super(customShape, basurero, gridPaneObserver);
        this.gridPaneObserver = gridPaneObserver;
        this.basurero = basurero;
        String tipo = "AND";

        this.setType(tipo);

        this.setOnMouseReleased(e -> {
            System.out.println("im here....");
            super.mouseReleased(e);
            getCols();
            checkColumns();
            gridPaneObserver.getCables().forEach(c -> GridPaneObserver.refreshProtoboard(gridPaneObserver));
            //quita la energía de las columnas afectadas.
        });

        Runnable removeAffectedCols = () -> {
            lowerCols.forEach(col -> {
                Utils.unPaintCircles(gridPaneObserver, col.get(0));
                gridPaneObserver.removeColumn(col);
                col.forEach(cir -> cir.setIsAffectedByChip(false));
            });

            upperCols.forEach(col -> {
                Utils.unPaintCircles(gridPaneObserver, col.get(0));
                gridPaneObserver.removeColumn(col);
                col.forEach(cir -> cir.setIsAffectedByChip(false));
            });

            affectedColumns.forEach(col -> {
                Utils.unPaintCircles(gridPaneObserver, col.get(0));
                gridPaneObserver.removeColumn(col);
                col.forEach(cir -> cir.setIsAffectedByChip(false));
            });

            affectedColumns.clear();
            upperCols.clear();
            lowerCols.clear();

            gridPaneObserver.removeChipAND(this);
            gridPaneObserver.getCables().forEach(c -> GridPaneObserver.refreshProtoboard(gridPaneObserver));
        };

        this.setOnMouseClicked(e -> {
            if (affectedColumns == null) return;

            super.mouseClicked(e, customShape, removeAffectedCols);
        });

    }
    //TODO implementar el pintar.
    private void getCols() {
        //si no está colocado bien no hace nada.
        if (!super.isUndraggable()) return;
        System.out.println("getting cols.....");
        ArrayList<ArrayList<CustomCircle>> cols = super.getColumns();

        String upperColGridName = cols.get(0).get(0).getID().getGridName();
        String lowerColGridName = cols.get(cols.size() - 1).get(0).getID().getGridName();

        System.out.println("lower: " + lowerColGridName + " upper: " + upperColGridName);
        //obtiene las columnas de abajo.
        lowerCols = cols.stream()
            .filter(col -> col.get(0).getID().getGridName().equals(lowerColGridName)).collect(Collectors.toList());

        upperCols = cols.stream()
            .filter(col -> col.get(0).getID().getGridName().equals(upperColGridName)).collect(Collectors.toList());
    }
    //revisa la columna indicada, si la anterior a esa y la antepenultima a esta tienen energía
    //la columna indicada a esta se le dará enegía automáticamente.
    private void checkColumn(List<ArrayList<CustomCircle>> arr, int index) {
        Function<ArrayList<CustomCircle>, CustomCircle> getFirstCircle = (a) -> a.get(0);
        Predicate<ArrayList<CustomCircle>> hasEnergy = (a) -> getFirstCircle.apply(a).hasEnergy();

        //debe se asi ya que se revisan las 2 columnas de atras del indice.
        if (index - 2 < 0) return;
        affectedColumns.add(arr.get(index)); //se agrega a las columnas afectadas.

        //si la primera y segunda columna tiene energía, se agrega la tercera.
        if (hasEnergy.test(arr.get(index - 2)) && hasEnergy.test(arr.get(index - 1))) { //n -2 y n -1
            //la columna a inspeccionar se le setea como afectadaporchip y se pasa el estado.
            arr.get(index).forEach(cir -> {
                cir.setIsAffectedByChip(true);
                cir.setState(getFirstCircle.apply(arr.get(index - 2)).getState());
            });
            gridPaneObserver.addColumn(arr.get(index), getFirstCircle.apply(arr.get(index - 2)).getState());
        }

        //en el casod de que una de las columnas no tenga energia, se le quita la energía.
        if (
            !hasEnergy.test(arr.get(index - 2)) && hasEnergy.test(arr.get(index - 1)) ||
            hasEnergy.test(arr.get(index - 2)) && !hasEnergy.test(arr.get(index - 1))
        ) {
            //todo obtener los cables acociados paa quitarles la energía.
            Utils.unPaintCircles(gridPaneObserver, arr.get(index).get(0)); //se pasa el primer circulo de la columna inspeccionada.
            gridPaneObserver.removeColumn(arr.get(index));
        }
        //si nunguno de los dos tiene energía se el quita la energía se le quita la energía a la columna seleccionada.
        if (!hasEnergy.test(arr.get(index - 2)) && !hasEnergy.test(arr.get(index - 1))) {
            //todo obtener los cables acociados paa quitarles la energía.
            Utils.unPaintCircles(gridPaneObserver, arr.get(index).get(0));
            gridPaneObserver.removeColumn(arr.get(index));
        }

    }
    //retorna las columnas que soin afectadas por las condiciones.
    public List<ArrayList<CustomCircle>> getAffectedColumns() {
        return affectedColumns;
    }

    // si el 0, 1 tienen el 2 tiene automaticamente y asi sucesivamente.
    @Override
    public void checkColumns() {
        //si no tiene columns abajo o arriba entonces no está colocado bien y por lo tanto se sale de la función.
        if (lowerCols == null || upperCols == null) return;

        int[] indexesToCheck = { 2, 5 }; //estos son los indices a mirar en el caso del chip AND.

        for (int i = 0; i < indexesToCheck.length; i++) {
            checkColumn(lowerCols, indexesToCheck[i]);
        }

        for (int i = 0; i < indexesToCheck.length; i++) {
            checkColumn(upperCols, indexesToCheck[i]);
        }
    }
}