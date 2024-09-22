package com.example.prototipo;


import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;


public class Switch extends Group {//Se utiliza un rectangulo para hacer un cuadrado
    private static boolean PasoDeCarga;// true -> deja la carga pasar           false -> la carga no pasa
    private static GridPaneObserver gridPaneObserver;
    private Basurero basurero;
    private static AnchorPane root;
    private static CustomCircle EndLeg;
    private static ArrayList<Cable> cables;


    public Switch(boolean PasoDeCarga, CustomShape customShape, GridPaneObserver gridPaneObserver, Basurero basurero, AnchorPane root, ArrayList<Cable> cables) {
        super(customShape);
        Switch.PasoDeCarga = true;
        this.basurero= basurero;
        Switch.gridPaneObserver = gridPaneObserver;
        Switch.root = root;
        Switch.cables= cables;

        Utils.makeDraggableNode(this, customShape.getStartX(), customShape.getStartY());//Llamamos a la clase Util para poder convertir el Switch en un nodo movible.
        Init(customShape);

        this.setOnMouseClicked(e -> {
            Utils.makeUndraggableNode(this);
            customShape.getLeg1().setisTaken(false);
            customShape.getLeg2().setisTaken(false);

            //Al darle Click al Switch se cambia el paso de carga y se llama a la funcion correspondiente
            Switch.PasoDeCarga = !Switch.PasoDeCarga;
            Switch.ChargePass(customShape, cables);


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
        customShape.getLeg1().setFill(Color.WHITE);
        customShape.getLeg1().setTranslateX(x-5);
        customShape.getLeg1().setTranslateY(y+15);

        //Se Crea la Segunda pata del Switch
        customShape.setLeg2(new CustomCircle(5, IdDos, 0));
        customShape.getLeg2().setisTaken(true);
        customShape.getLeg2().setFill(Color.YELLOW);
        customShape.getLeg2().setTranslateX(x+35);
        customShape.getLeg2().setTranslateY(y+15);

        customShape.getLeg1().removeEnergy();
        customShape.getLeg2().removeEnergy();
        customShape.getLeg1().setCable(null);
        customShape.getLeg2().setCable(null);

        //Se añaden al grupo de la Clase Switch
        this.getChildren().add(customShape.getLeg1());
        this.getChildren().add(customShape.getLeg2());
    }


    //Este Metodo lo que hace es realizar la funcionalidad del Switch
    public static void ChargePass(CustomShape customShape, ArrayList<Cable> cables1){
        //Llamamos al inicio del metodo si es que tiene una EndLeg
        DecideEndLeg(customShape);
        //Preguntamos si el paso de carga es true o false para asi saber que hacer
        if(Switch.PasoDeCarga) {
            //Preguntamos si es que el EndLeg tiene la misma id que la pata 2, para asi saber que hay que pintar Y ademas preguntamos
            if (ID.isSameID(Switch.EndLeg.getID(), customShape.getLeg2().getID()) && Switch.EndLeg != null) {
                //cambiamos el estado de la pata 2, ya que sabemos que es la pata final y tiene que tener la carga de la otra pata
                customShape.getLeg2().setState(customShape.getLeg1().getState());

                //Esta condición compara la ID del CustomCircle es la misma con una de las ID que almacena el Cable.
                if (ID.isSameID(customShape.getLeg2().getID(), customShape.getLeg2().getCable().getIds()[0])) {

                    //En el caso que son la misma id, Se guarda la contraria ya que no es el mismo
                    ID id = customShape.getLeg2().getCable().getIds()[1];
                    PaintSwitch(id, customShape, customShape.getLeg2(), cables);
                } else {
                    //En el caso que no sean iguales se hara lo mismo pero llamando a la id Contraria
                    ID id = customShape.getLeg2().getCable().getIds()[0];
                    PaintSwitch(id, customShape, customShape.getLeg2(), cables);
                }
            //Aca preguntamos en esta condición es que si la ID del EndLeg es igual a la de la pata 1
            } else if (ID.isSameID(Switch.EndLeg.getID(), customShape.getLeg1().getID()) && Switch.EndLeg != null) {
                //Y se realiza lo mismo que se hizo anterior mente pero con la otra pata
                customShape.getLeg1().setState(customShape.getLeg2().getState());
                if (ID.isSameID(customShape.getLeg1().getID(), customShape.getLeg1().getCable().getIds()[0])) {
                    ID id = customShape.getLeg1().getCable().getIds()[1];
                    PaintSwitch(id, customShape, customShape.getLeg1(), cables);
                } else {
                    ID id = customShape.getLeg1().getCable().getIds()[0];
                    PaintSwitch(id, customShape, customShape.getLeg1(), cables);
                }
            }
        } else{
            //Cuando el paso de energia es False y preguntamos si es que existe el EndLeg
            if (Switch.EndLeg != null){
                //Queremos que cuando sea falso despintar entonces preguntamos si las dos patas tienen energia y su cable correspondiente
                if(customShape.getLeg1().hasEnergy() && customShape.getLeg2().hasEnergy() && customShape.getLeg1().hasCable() && customShape.getLeg2().hasCable()){
                    //Pregutamos si el estado que tienen son los mismos
                    if(customShape.getLeg1().getState() == customShape.getLeg2().getState()){
                        //Despues preguntamos por el EndLeg
                        if(ID.isSameID(Switch.EndLeg.getCable().getIds()[0],Switch.EndLeg.getID())){
                            //Encontramos la id que no es perteneciente al Switch
                            UnPaintSwitch(EndLeg.getCable().getIds()[1], customShape, Switch.EndLeg );
                        } else{
                            UnPaintSwitch(EndLeg.getCable().getIds()[0], customShape, Switch.EndLeg );
                        }
                    } //NO sucede nada cuando los estados de las patas son distintas
                }
            }
        }
    }

    //Este metodo lo que hace es pintar El gridpane segun el GridPane que corresponda
    public static void PaintSwitch(ID id, CustomShape customShape, CustomCircle Leg, ArrayList<Cable> cables) {
        if ("gridTrail1".equals(id.getGridName())) {
            //Llamamos a la funcion de Pintar
            Utils.paintCircles(Switch.gridPaneObserver, id, Leg.getState(), cables);
        } else if ("gridTrail2".equals(id.getGridName())) {
            Utils.paintCircles(Switch.gridPaneObserver, id, Leg.getState(), cables);
        } else if ((id.getGridName().equals("LedVolt1")) || (id.getGridName().equals("switchvolt1"))) {
            //En el caso que el nombre del Grid no es de ninguno de los Gridpane entonces debe ser de Automaticamente del una bateria, LED o Switch.
            if (ID.isSameID(Leg.getID(), Leg.getCable().Getcircles()[0].getID())) {
                Leg.getCable().Getcircles()[1].setState(Leg.getState());
            } else {
                Leg.getCable().Getcircles()[0].setState(Leg.getState());
            }
        }
    }




    public static void UnPaintSwitch(ID id, CustomShape customShape, CustomCircle Leg) {
        if ("gridTrail1".equals(id.getGridName())) {
            // Llamamos a la función de Despintar
            Utils.unPaintCircles(Switch.gridPaneObserver, id, true);
        } else if ("gridTrail2".equals(id.getGridName())) {
            Utils.unPaintCircles(Switch.gridPaneObserver, id, true);
        } else if ((id.getGridName().equals("LedVolt1")) || (id.getGridName().equals("switchvolt1"))) {
                // En el caso que el nombre del Grid no es de ninguno de los Gridpane entonces debe ser de Automaticamente del una bateria, LED o Switch.
                if (ID.isSameID(Leg.getID(), Leg.getCable().Getcircles()[0].getID())) {
                    Leg.getCable().Getcircles()[1].removeEnergy();
                } else {
                    Leg.getCable().Getcircles()[0].removeEnergy();
                }

        }
    }



    //Este metodo lo que realiza es decidir la pata a la cual sabremos hacia donde se quitara la energía
    public static void DecideEndLeg(CustomShape customShape){
        if(customShape.getLeg1().hasCable() && !(customShape.getLeg2().hasCable())){
            SetEndLeg(customShape.getLeg2());
        } else if ( customShape.getLeg2().hasCable() && !(customShape.getLeg1().hasCable())){
            SetEndLeg(customShape.getLeg1());
        }
    }



    public static void SetPasoDeCarga(boolean state){
        Switch.PasoDeCarga = state;
    }
    public static AnchorPane getroot(){
        return Switch.root;
    }
    public static void SetEndLeg(CustomCircle c){ Switch.EndLeg=c;}
}
