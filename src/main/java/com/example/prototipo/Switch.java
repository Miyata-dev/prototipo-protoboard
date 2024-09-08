package com.example.prototipo;

import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class Switch extends Group {//Se utiliza un rectangulo para hacer un cuadrado
    private boolean PasoDeCarga;// true -> deja la carga pasar           false -> la carga no pasa
    private GridPaneController gridPane1;
    private GridPaneController gridPane2;
    private Basurero basurero;
    private AnchorPane root;


    public Switch(boolean PasoDeCarga, CustomShape customShape, GridPaneController grid1, GridPaneController grid2, Basurero basurero, AnchorPane root) {
        super(customShape);
        this.PasoDeCarga = PasoDeCarga;
        this.gridPane1 = grid1;
        this.gridPane2 = grid2;
        this.basurero= basurero;
        this.root = root;

        Utils.makeDraggableNode(this, customShape.getStartX(), customShape.getStartY());//Llamamos a la clase Util para poder convertir el Switch en un nodo movible.
        Init(customShape);

        this.setOnMouseClicked(e -> {
            Utils.makeUndraggableNode(this);
            customShape.getLeg1().setisTaken(false);
            customShape.getLeg2().setisTaken(false);

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


    //TODO refactorizar el codigo, ya que son muchos if else y ver manera de simplificarlo
    public void ChargePass(CustomShape customShape, GridPaneController grid1, GridPaneController grid2){
        //Preguntamos si es que el paso de carga del Switch esta activado o no
        if(this.PasoDeCarga == true){
            if(customShape.getLeg1().hasEnergy()){
                //preguntamos si una de las dos ID que tiene el cable es igual al id de una de las patas
                if(ID.isSameID(customShape.getLeg1().getID(), customShape.getLeg1().getCable().getIds()[0])){
                    //Sabremos que al CustomCircle al que hay que pasarle la carga es el contrario a la condición ya que no es igual al de la pata
                    ID id = customShape.getLeg1().getCable().getIds()[1];
                    if(id.getGridName().equals(grid1.getName())){
                        //Llama a la funcion de pintar
                        Utils.paintCircles(grid1.getGridPane(), id, customShape.getLeg1().getState());
                    } else if (id.getGridName().equals(grid2.getName())) {
                        Utils.paintCircles(grid2.getGridPane(), id , customShape.getLeg1().getState());
                    } else{return;}
                    //Llamamos a la funcion de pintar
                } else if(ID.isSameID(customShape.getLeg1().getID(), customShape.getLeg1().getCable().getIds()[1])){
                    //Sabremos que los id son distintos de los CustomCircle por lo que no entrara
                    ID id = customShape.getLeg1().getCable().getIds()[0];
                    //Preguntamos a cual de los dos gridPane tiene que pintar el switch.
                    if(id.getGridName().equals(grid1.getName())){
                        //Llama a la funcion de pintar
                        Utils.paintCircles(grid1.getGridPane(), id, customShape.getLeg1().getState());
                    } else if (id.getGridName().equals(grid2.getName())) {
                        Utils.paintCircles(grid2.getGridPane(), id , customShape.getLeg1().getState());
                    }

                }
            } else if(customShape.getLeg2().hasEnergy()){
                if(ID.isSameID(customShape.getLeg2().getID(), customShape.getLeg2().getCable().getIds()[0])){
                    //Sabremos que al CustomCircle al que hay que pasarle la carga es el contrario a la condición ya que no es igual al de la pata
                    ID id = customShape.getLeg2().getCable().getIds()[1];
                    //Preguntamos a cual de los dos gridPane tiene que pintar el switch.
                    if(id.getGridName().equals(grid1.getName())){
                        //Llama a la funcion de pintar
                        Utils.paintCircles(grid1.getGridPane(), id, customShape.getLeg2().getState());
                    } else if (id.getGridName().equals(grid2.getName())) {
                        Utils.paintCircles(grid2.getGridPane(), id , customShape.getLeg2().getState());
                    }
                    //Llamamos a la funcion de pintar


                } else if(ID.isSameID(customShape.getLeg2().getID(), customShape.getLeg2().getCable().getIds()[1])){
                    //Sabremos que los id son distintos de los CustomCircle por lo que no entrara
                    ID id = customShape.getLeg2().getCable().getIds()[0];
                    //Preguntamos a cual de los dos gridPane tiene que pintar el switch.
                    if(id.getGridName().equals(grid1.getName())){
                        //Llama a la funcion de pintar
                        Utils.paintCircles(grid1.getGridPane(), id, customShape.getLeg2().getState());
                    } else if (id.getGridName().equals(grid2.getName())) {
                        Utils.paintCircles(grid2.getGridPane(), id , customShape.getLeg2().getState());
                    }
                }

            } else{ return;}

        } else{return;}
    }

}
