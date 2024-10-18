package com.example.prototipo;


import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.concurrent.atomic.AtomicReference;

public class Switch2 extends Group {
    private CustomCircle Leg1;
    private CustomCircle Leg2;
    private CustomCircle Leg3;
    private CustomCircle Leg4;

    private ImageView Shape;
    private Boolean ChargePass;
    private String UniqueId;
    private GridPaneObserver gridPaneObserver;
    private AnchorPane root;
    private Basurero basurero;

    public Switch2(Boolean chargePass, GridPaneObserver gridPaneObserver, AnchorPane root, Basurero basurero){
        //Le damos una ID unica
        this.UniqueId = Utils.createRandomID();
        this.ChargePass = chargePass;
        this.gridPaneObserver = gridPaneObserver;
        this.root = root;
        this.basurero = basurero;
        this.init();


        Utils.makeDraggableNode(this, new AtomicReference<>((double) 0), new AtomicReference<>((double) 0));
        this.setOnMouseClicked(e->{

           if(basurero.getIsActive()){//TODO REVISAR EL METODO QUE ELIMINA UN ELEMENTO
               //Llamamos un metodo lo que hace es eliminar la figura,
           } else {
               this.ChargePass = !this.ChargePass;
           }
        });
    }


    //Metodos

    //Este metodo lo que hace es crear el elemento para que se vea graficamente
    public void init(){
        //Declaramos la imagen dentro de la clase ya que siempre va a ser la misma imagen
        this.Shape = new ImageView(getClass().getResource("Switch.png").toExternalForm());
        //Declaramos su tamaño y su ubicación para despues agregarlo al grupo
        this.Shape.setFitWidth(50);
        this.Shape.setFitHeight(50);
        Shape.setX(720);
        Shape.setY(554);




        this.getChildren().add(this.Shape);
    }




    //Setters...
    public void setRoot(AnchorPane root){
        this.root = root;
    }
    public void setLeg1(CustomCircle Leg1){
        this.Leg1 = Leg1;
    }
    public void setLeg2(CustomCircle Leg2){
        this.Leg2 = Leg2;
    }
    public void setLeg3(CustomCircle Leg3){
        this.Leg3 = Leg3;
    }
    public void setLeg4(CustomCircle Leg4){
        this.Leg4 = Leg4;
    }
    public void setGridPaneObserver(GridPaneObserver gridPaneObserver){
        this.gridPaneObserver = gridPaneObserver;
    }
    public void setChargePass(Boolean chargePass){
        this.ChargePass = chargePass;
    }

    //Getters...
    public String getUniqueId(){
        return this.UniqueId;
    }
    public GridPaneObserver getGridPaneObserver(){
        return this.gridPaneObserver;
    }
    public Boolean getChargePass(){
        return this.ChargePass;
    }
    public CustomCircle getLeg1(){
        return this.Leg1;
    }
    public CustomCircle getLeg2(){
        return this.Leg2;
    }
    public CustomCircle getLeg3(){
        return this.Leg3;
    }
    public CustomCircle getLeg4(){
        return this.Leg4;
    }

}
