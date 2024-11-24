package com.example.prototipo;

import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.w3c.dom.css.Rect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

//Tamaño Figura Width:88, Height: 95
public class Display extends Chip {
    private GridPaneObserver gridPaneObserver;
    private Basurero basurero;

    //la columnna del indice 2 no se concidera en estos hashmaps.
    private HashMap<String, ArrayList<CustomCircle>> displayColumns = new HashMap<>(); // Caracter asociado, columna asociada al caracter.
    private HashMap<String, Rectangle> displayElements = new HashMap<>();
    private HashMap<String, Boolean> displayisBurned = new HashMap<>();

    public Display(CustomShape customShape, Basurero basurero, GridPaneObserver gridPaneObserver) {

        super(customShape, basurero, gridPaneObserver, 5);
        this.gridPaneObserver = gridPaneObserver;
        this.basurero = basurero;
        createDisplayLines(customShape);

        Runnable removeAffectedCols = () -> {
            System.out.println("number of ghostCables " + getGhostCables().size());
        };
        Runnable removeDisplay = ()->{
            gridPaneObserver.removeDisplays(this);
        };

        this.setOnMouseClicked(e -> {
            if (super.getAffectedColumns() == null) return;

            super.mouseClicked(e, customShape, removeAffectedCols);
            super.mouseClicked(e, customShape, removeDisplay);
            System.out.println("size is : " +gridPaneObserver.getDisplays().size());
        });

        this.setOnMouseReleased(e -> {
            super.mouseReleased(e);
            super.getCols();
            asignCols(null);
            displayFunction();
        });

    }



    //Este metodo lo que hace es crear las lineas del display Lines
    public void createDisplayLines(CustomShape customShape){
        Bounds parentBounds = customShape.getBoundsInParent();
        double x = parentBounds.getMinX();
        double y = parentBounds.getMinY();
        //Tamaño Figura Width:88, Height: 95
        double width = 88;
        double height = 95;

        double rectanglelarge = width/3;
        double rectanglegrosor = 6;

        //Se hacen todos los rectangulos de manera individual ya que encontrar el algoritmo de los rectangulos no se pudo crear.

        //Creamos el primer rectangulo con la Clave a.
        Rectangle rectanglea = new Rectangle(x +25, (y + 10), rectanglelarge, rectanglegrosor);
        //Despues de setearle sus atributos le cambiamos el color a Gray
        rectanglea.setFill(Color.GRAY);
        //Y lo añadimos a la colecciones
        displayElements.put("a",rectanglea );

        //Y hacemos lo mismo con los demas rectangulos con sus ubicaciones correspondientes

        Rectangle rectangleb =  new Rectangle( rectanglea.getBoundsInParent().getMinX()
                + rectanglelarge , rectanglea.getBoundsInParent().getMinY() + rectanglegrosor, rectanglegrosor, rectanglelarge);
        rectangleb.setFill(Color.GRAY);

        displayElements.put("b", rectangleb);


        Rectangle rectanglec = new Rectangle(rectangleb.getBoundsInParent().getCenterX() - 3,
                rectangleb.getBoundsInParent().getMaxY() + rectanglegrosor * 1.5, rectanglegrosor, rectanglelarge);
        rectanglec.setFill(Color.GRAY);


        displayElements.put("c", rectanglec);


        Rectangle rectangled = new Rectangle (rectanglec.getBoundsInParent().getMinX() - rectanglelarge,
                rectanglec.getBoundsInParent().getMaxY(), rectanglelarge, rectanglegrosor);
        rectangled.setFill(Color.GRAY);
        displayElements.put("d", rectangled);

        Rectangle rectanglee = new Rectangle ( rectanglec.getBoundsInParent().getMinX() - rectanglelarge - rectanglegrosor,
                rectanglec.getBoundsInParent().getMinY(), rectanglegrosor, rectanglelarge);
        rectanglee.setFill(Color.GRAY);
        displayElements.put("e", rectanglee);

        Rectangle rectanglef = new Rectangle(rectanglea.getBoundsInParent().getMinX() - rectanglegrosor, rectanglea.getBoundsInParent().getMinY() + rectanglegrosor, rectanglegrosor, rectanglelarge);
        rectanglef.setFill(Color.GRAY);
        displayElements.put("f",  rectanglef);

        Rectangle rectangleg = new Rectangle(rectanglef.getBoundsInParent().getMaxX(), rectanglef.getBoundsInParent().getMaxY() + 1.5, rectanglelarge, rectanglegrosor);
        rectangleg.setFill(Color.GRAY);
        displayElements.put("g", rectangleg);


        Rectangle rectangledp = new Rectangle( rectangled.getBoundsInParent().getMaxX() + rectanglelarge/2,
                rectangled.getBoundsInParent().getMinY(), 5, 5);
        rectangledp.setFill(Color.GRAY);
        displayElements.put("dp", rectangledp);



        //Recorremos la coleccion para añadirlo a la scena y añadimos la coleccion de si esta quemado o no el segmento en falso
        displayElements.forEach((key, rectangle)->{
            this.getChildren().add(rectangle);
            this.displayisBurned.put(key, false);
        });
    }



    //Este metodo lo que hace es asignar las columnas correspondientes del Display, junto a sus correspondientes llaves
    public void asignCols(ArrayList<ArrayList<CustomCircle>> cols){
        //inicializamos una variable caracter en a
        String chars = "a";

        //Creamos un AtomicInteger para aumentar en el for
        AtomicInteger i = new AtomicInteger( 0);

        //Recorremos la coleccion de Columnas
        for (ArrayList<CustomCircle> column : super.getColumns()) {
            //Preguntamos si el indice de la columnas no son la tercera y la octava, porque son las que necesitan ser energizadas
            if(i.get() != 2 && i.get() != 7){
                if(i.get() == 0){
                    this.displayColumns.put("g", column);
                } else if(i.get() == 1){
                    this.displayColumns.put("f", column);
                } else if(i.get() == 5){
                    this.displayColumns.put("e", column);
                } else if(i.get() == 6){
                    this.displayColumns.put("d", column);
                } else{
                    //preguntamos si en el caso de que el caracter es igual a h, ya que cuando es de caracter h, su llave debe ser dp(decimal point)
                    if(chars.equals("d")){
                        this.displayColumns.put("dp", column);

                        break;
                    }
                    this.displayColumns.put(chars, column);
                    chars = Display. avanzarCharacter(chars);
                }
            } else {
                this.displayColumns.put("energy" + i, column);
            }
            i.getAndIncrement();
        }
    }

    //Este metodo lo que va a hacer es ir avanzando de caracter para cuando se haga un forEach;
    public static String avanzarCharacter(String letra){
        //Obtenemos el primer caracter del String
        char character = letra.charAt(0);
        //Despues Avanzamos al siguiente caracter
        char sigCharacter = (char) (character + 1);

        //Y lo transformamos en String para retornalo
        return String.valueOf(sigCharacter);
    }


    //Este metodo lo que hace es la funcion correspondiente de la funcion del Display
    public void displayFunction(){
        //En el caso de que las columnas sean nulas se retorna
        if(super.getColumns() == null || this.displayisBurned == null) return;

        //Preguntamos si las columnas que son conexion a tierra tienen energia positiva
        if (this.displayColumns.get("energy2").get(0).getState() == 1 || this.displayColumns.get("energy7").get(0).getState() == 1) {
            //si uno de los dos tienen energia positiva, recorremos las columnas
            this.displayColumns.forEach((charc, cols) -> {
                //Preguntamos si la llave no es de las columnas de conexion a tierra
                if (isnoEnergy(charc)) {
                    //en el caso de que la columna tenga un estado positivo, se enciende en rojo
                    if (cols.get(0).getState() == 1) {
                        setColorSegment(charc, Color.RED);
                        //En el caso de que el estado sea neutro, se coloca de colo gris
                    } else if (cols.get(0).getState() == 0) {
                        setColorSegment(charc, Color.GRAY);
                    } else if (cols.get(0).getState() == -1) {
                        //y si llega a ser negativo se quema.
                        setSegmentBurned(charc);
                    }
                }
            });
        } else {
            this.displayElements.forEach((charc, rec) ->{
                setColorSegment(charc, Color.GRAY);
            });
        }
    }

    public Boolean isnoEnergy(String charc){
        return !charc.equals("energy2") && !charc.equals("energy7");
    }

    public void setSegmentBurned(String charc){
        this.displayElements.get(charc).setFill(Color.DARKSLATEGRAY);
        this.displayisBurned.put(charc, true);
    }

    public void setColorSegment(String charc,Color color){
        if(!this.displayisBurned.get(charc)){
            this.displayElements.get(charc).setFill(color);
        }
    }

}