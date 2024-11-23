package com.example.prototipo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ChipAND extends Chip { //implementar un equyals en la clase CHIP para poder hacer comparaciones.
    private GridPaneObserver gridPaneObserver;
    private Basurero basurero;

    public ChipAND(CustomShape customShape, Basurero basurero, GridPaneObserver gridPaneObserver) {
        super(customShape, basurero, gridPaneObserver, 7);
        this.gridPaneObserver = gridPaneObserver;
        this.basurero = basurero;
        String tipo = "AND";

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

            System.out.println("number of ghost cables: " + getGhostCables().size());
            super.mouseClicked(e, customShape);
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

        //esta es la columna que se energiza dependiendo de las columnas anteriores a esta.
        ArrayList<CustomCircle> columnToCheck = arr.get(index);

        //si la primera y segunda columna tiene energía, y tienen la misma energía entonces se agrega la tercera.
        if (hasEnergy.test(arr.get(index - 2)) && hasEnergy.test(arr.get(index - 1)) && !haveDifferenteEnergy.test(index)) { //n -2 y n -1
            //si ya tiene energía, se sale de la función
            if (hasEnergy.test(arr.get(index))) return;

            System.out.println("adding column same energy AND...");
            //la columna a inspeccionar se le setea como afectadaporchip y se pasa el estado.
            arr.get(index).forEach(cir -> {
                cir.setIsAffectedByChip(true);
                cir.setState(getFirstCircle.apply(arr.get(index - 2)).getState());
            });

            connectWithGhostCable(arr, index, index -  2);
        }
        //si la 1ra y segunda tienen energía pero no tiene el mismo tipo de energía, se pasa negativo.
        if (hasEnergy.test(arr.get(index - 2)) && hasEnergy.test(arr.get(index - 1)) && haveDifferenteEnergy.test(index)) {
            //si ya tiene energía, se sale de la función
            if (hasEnergy.test(arr.get(index))) return;

            System.out.println("adding column differente energy AND...");
            //la columna a inspeccionar se le setea como afectadaporchip y se pasa el estado.
            arr.get(index).forEach(cir -> {
                cir.setIsAffectedByChip(true);
                cir.setState(-1);
            });

            connectWithGhostCable(arr, index, index - 2, -1);
            gridPaneObserver.addColumn(arr.get(index), -1);
        }

        //en el casod de que una de las columnas no tenga energia, se le quita la energía.
        if (
            !hasEnergy.test(arr.get(index - 2)) && hasEnergy.test(arr.get(index - 1)) ||
            hasEnergy.test(arr.get(index - 2)) && !hasEnergy.test(arr.get(index - 1))
        ) {
            //todo obtener los cables acociados paa quitarles la energía.
            Utils.unPaintCircles(gridPaneObserver, arr.get(index).get(0)); //se pasa el primer circulo de la columna inspeccionada.
            gridPaneObserver.removeColumn(arr.get(index));
            //cuando se va la energia de la columna es necesario quitar el cable fantasma.
            disconnectGhostCable(columnToCheck.get(0).getID());
        }
        //si nunguno de los dos tiene energía se el quita la energía se le quita la energía a la columna seleccionada.
        if (!hasEnergy.test(arr.get(index - 2)) && !hasEnergy.test(arr.get(index - 1))) {
            //todo obtener los cables acociados paa quitarles la energía.
            Utils.unPaintCircles(gridPaneObserver, arr.get(index).get(0));
            gridPaneObserver.removeColumn(arr.get(index));
        }

        if (!hasEnergy.test(arr.get(index - 2))) {
            CustomCircle circle = getFirstCircle.apply(arr.get(index - 2));
            disconnectGhostCable(circle.getID());
        }

        if (!hasEnergy.test(arr.get(index - 1))) {
            CustomCircle circle = getFirstCircle.apply(arr.get(index - 1));
            disconnectGhostCable(circle.getID());
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