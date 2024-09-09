package com.example.prototipo;


import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;


public class Switch extends Group {//Se utiliza un rectangulo para hacer un cuadrado
    private static boolean PasoDeCarga;// true -> deja la carga pasar           false -> la carga no pasa
    private static  GridPaneTrailController gridPane1;
    private static  GridPaneTrailController gridPane2;
    private Basurero basurero;
    private static AnchorPane root;


    public Switch(boolean PasoDeCarga, CustomShape customShape, GridPaneTrailController grid1, GridPaneTrailController grid2, Basurero basurero, AnchorPane root) {
        super(customShape);
        //Switch.PasoDeCarga = PasoDeCarga;
        Switch.gridPane1 = grid1;
        Switch.gridPane2 = grid2;
        this.basurero= basurero;
        Switch.root = root;

        Utils.makeDraggableNode(this, customShape.getStartX(), customShape.getStartY());//Llamamos a la clase Util para poder convertir el Switch en un nodo movible.
        Init(customShape);

        this.setOnMouseClicked(e -> {
            Utils.makeUndraggableNode(this);
            customShape.getLeg1().setisTaken(false);
            customShape.getLeg2().setisTaken(false);

            if(Switch.PasoDeCarga == false){
                Switch.PasoDeCarga= !Switch.PasoDeCarga;
                ChargePass(customShape);
            } else{
                Switch.PasoDeCarga= !Switch.PasoDeCarga;
            }


            System.out.println("el paso de carga" + Switch.PasoDeCarga);
            if (basurero.getIsActive()) {
                //Llamamos al metodo para eliminar los cables que pueden pertenecer al Switch y despues borrar este mismo
                basurero.EliminateElements(customShape, e, root);
                root.getChildren().remove(this);
            }
        });
    }

    //Este metodo crea las patas del LED de manera ordenada
    public void Init(CustomShape customShape){
        double x = customShape.getX();
        double y = customShape.getY();

        ID IdUno= new ID(1,1, "switchvolt1");
        IdUno.setIsForGridpane(false);
        ID IdDos = new ID (2,1,"switchvolt1");
        IdDos.setIsForGridpane(false);
        //Se Crea La primera pata del Switch
        customShape.setLeg1(new CustomCircle(5, IdUno, 0));
        customShape.getLeg1().setisTaken(true);
        customShape.getLeg1().setFill(Color.YELLOW);
        customShape.getLeg1().setTranslateX(x-5);
        customShape.getLeg1().setTranslateY(y+15);

        //Se Crea la Segunda pata del Switch
        customShape.setLeg2(new CustomCircle(5, IdDos, 0));
        customShape.getLeg2().setisTaken(true);
        customShape.getLeg2().setFill(Color.YELLOW);
        customShape.getLeg2().setTranslateX(x+35);
        customShape.getLeg2().setTranslateY(y+15);

        //Se añaden al grupo de la Clase Switch
        this.getChildren().add(customShape.getLeg1());
        this.getChildren().add(customShape.getLeg2());
    }

    public static void ChargePass(CustomShape customShape){
        //Preguntamos si es que el paso de energia es falso, se sale del metodo
        if(!PasoDeCarga){

            return;
        }
        //Si las dos patas tienen Energia no pasa nada, pero si llegan a ser distintas "el componente deberia explotar"
        if(customShape.getLeg1().hasEnergy() && customShape.getLeg2().hasEnergy()){
            return;
        }

        if(customShape.getLeg1().hasEnergy()){
            System.out.println(customShape.getLeg1().getState());
            //Si la Pata1 tiene energia entonces tenemos que cambiar el Estado de la segunda al estado correspondiente
            customShape.getLeg2().setState(customShape.getLeg1().getState());
            //Esta condición compara la ID del CustomCircle es la misma con una de las ID que almacena el Cable.
            if(ID.isSameID(customShape.getLeg2().getID(), customShape.getLeg2().getCable().getIds()[0])){

                //En el caso que son la misma id, Se guarda la contraria ya que no es el mismo
                ID id = customShape.getLeg2().getCable().getIds()[1];
                PaintSwitch(id, customShape, customShape.getLeg2());
            } else{

                ID id = customShape.getLeg2().getCable().getIds()[0];
                PaintSwitch(id, customShape, customShape.getLeg2());
            }
        } else if( customShape.getLeg2().hasEnergy()){
            customShape.getLeg1().setState(customShape.getLeg2().getState());
            if (ID.isSameID(customShape.getLeg1().getID(), customShape.getLeg1().getCable().getIds()[0])){
                customShape.getLeg2().setState(customShape.getLeg1().getState());
                ID id = customShape.getLeg1().getCable().getIds()[1];
                PaintSwitch(id, customShape, customShape.getLeg1());
            } else{
                customShape.getLeg2().setState(customShape.getLeg1().getState());
                ID id = customShape.getLeg1().getCable().getIds()[0];
                PaintSwitch(id, customShape, customShape.getLeg1());
            }
        }
    }

    //Este metodo lo que hace es pintar El gridpane segun el GridPane que corresponda
    public static void PaintSwitch(ID id, CustomShape customShape, CustomCircle Leg) {
        System.out.println(id.getGridName());
        if ("gridTrail1".equals(id.getGridName())) {
            //Llamamos a la funcion de Pintar
            Utils.paintCircles(gridPane1.getGridPane(), id, Leg.getState());
        } else if ("gridTrail2".equals(id.getGridName())) {
            Utils.paintCircles(gridPane2.getGridPane(), id, Leg.getState());
        } else if ((id.getGridName().equals("LedVolt1")) || (id.getGridName().equals("switchvolt1"))) {
            //En el caso que el nombre del Grid no es de ninguno de los Gridpane entonces debe ser de Automaticamente del una bateria, LED o Switch.
                if(ID.isSameID(Leg.getID(),Leg.getCable().Getcircles()[0].getID())){
                    Leg.getCable().Getcircles()[1].setState(Leg.getState());
                } else{
                    Leg.getCable().Getcircles()[0].setState(Leg.getState());
                }
            }
        }

    public static void UnPaintSwitch(ID id, CustomShape customShape, CustomCircle Leg) {
        System.out.println(id.getGridName());
        if ("gridTrail1".equals(id.getGridName())) {
            // Llamamos a la función de Despintar
            System.out.println("despintando");
            Utils.unPaintCircles(gridPane1.getGridPane(), id.getIndexColumn());
            System.out.println("despintando");
        } else if ("gridTrail2".equals(id.getGridName())) {
            System.out.println("Despintando2");
            Utils.unPaintCircles(gridPane2.getGridPane(), id.getIndexColumn());
            System.out.println("Despintando2");
        } else if ((id.getGridName().equals("LedVolt1")) || (id.getGridName().equals("switchvolt1"))) {
            // En el caso que el nombre del Grid no es de ninguno de los Gridpane entonces debe ser de Automaticamente del una bateria, LED o Switch.
            if (ID.isSameID(Leg.getID(), Leg.getCable().Getcircles()[0].getID())) {
                Leg.getCable().Getcircles()[1].setState(0);
            } else {
                Leg.getCable().Getcircles()[0].setState(0);
            }
        }
    }

    //TODO refactorizar el codigo, ya que son muchos if else y ver manera de simplificarlo

    public static void SetPasoDeCarga(boolean state){
        Switch.PasoDeCarga = state;
    }
    public static AnchorPane getroot(){
        return Switch.root;
    }
}
