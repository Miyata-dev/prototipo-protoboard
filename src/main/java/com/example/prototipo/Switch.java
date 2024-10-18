package com.example.prototipo;


import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;



public class Switch extends Group {//Se utiliza un rectangulo para hacer un cuadrado
    private boolean PasoDeCarga;// true -> deja la carga pasar           false -> la carga no pasa
    private GridPaneObserver gridPaneObserver;
    private Basurero basurero;
    private AnchorPane root;
    private CustomCircle EndLeg;
    private ArrayList<Cable> cables;
    private String UniqueId;
    private CustomCircle[] customCircles;
    private CustomShape shape;//TODO QUITAR EL CUSTOMSHAPE
    private ImageView shaping;


    public Switch(boolean PasoDeCarga, CustomShape customShape, GridPaneObserver gridPaneObserver, Basurero basurero, AnchorPane root, ArrayList<Cable> cables, ImageView image) {
        super(customShape, image);
        this.PasoDeCarga = true;
        this.basurero = basurero;
        this.gridPaneObserver = gridPaneObserver;
        this.root = root;
        this.cables = cables;
        this.UniqueId = customShape.getUniqueID();
        this.customCircles = new CustomCircle[]{customShape.getLeg1(), customShape.getLeg2()};
        this.EndLeg = null;
        this.shape = customShape;
        this.shaping = image;

        Utils.makeDraggableNode(this, customShape.getStartX(), customShape.getStartY());//Llamamos a la clase Util para poder convertir el Switch en un nodo movible.
        Init(customShape);
        gridPaneObserver.addSwitches(this);

        //Este AtomicBoolean lo que hace es que cada vez que se haga click en el Switch el setisTaken sea siempre false
        AtomicBoolean LegTaken = new AtomicBoolean(true);

        this.setOnMouseClicked(e -> {
            if(LegTaken.get()){
                Utils.makeUndraggableNode(this);
                customShape.getLeg1().setisTaken(false);
                customShape.getLeg2().setisTaken(false);
                LegTaken.set(false);
            }
            //Al darle Click al Switch se cambia el paso de carga y se llama a la funcion correspondiente
            setPasoDeCarga(!getPasoDeCarga());
            this.ChargePass(cables);

            System.out.println("El Paso de Carga es: " + getPasoDeCarga() + " de " + this.getUniqueId());

            if (basurero.getIsActive() && customShape.getHasMoved()) {
                if(this.PasoDeCarga){
                    this.PasoDeCarga = false;
                    this.ChargePass(this.gridPaneObserver.getCables());
                }
                //Llamamos al metodo para eliminar los cables que pueden pertenecer al Switch y despues borrar este mismo
                basurero.EliminateElements(customShape, e, root, this.gridPaneObserver);
                this.gridPaneObserver.removeSwitches(this);
                root.getChildren().remove(this);
            }

            if (basurero.getIsActive()) customShape.setHasMoved(true);
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

    public  void ChargePass(ArrayList<Cable> cables){
        //Llamamos al inicio del metodo si es que tiene una EndLeg

        if(this.EndLeg == null){
            DecideEndLeg(this.shape);
        } else {
            System.out.println("End Leg is" + getEndLeg().getID());
        }
        OnlyEndLegHaveEnergy();
        //Preguntamos si el paso de carga es true o false para asi saber que hacer
        if(this.PasoDeCarga && this.shape.getLeg1().hasCable() && this.shape.getLeg2().hasCable()) {
            //Preguntamos si es que el EndLeg tiene la misma id que la pata 2, para asi saber que hay que pintar Y ademas preguntamos
            if (this.EndLeg != null && ID.isSameID(this.EndLeg.getID(), this.shape.getLeg2().getID())) {
                //cambiamos el estado de la pata 2, ya que sabemos que es la pata final y tiene que tener la carga de la otra pata
                this.shape.getLeg2().setState(this.shape.getLeg1().getState());

                //Esta condición compara la ID del CustomCircle es la misma con una de las ID que almacena el Cable.
                if (ID.isSameID(this.shape.getLeg2().getID(), this.shape.getLeg2().getCable().getIds()[0])) {

                    //En el caso que son la misma id, Se guarda la contraria ya que no es el mismo
                    ID id = this.shape.getLeg2().getCable().getIds()[1];
                    PaintSwitch(id, this.shape, this.shape.getLeg2(), cables);
                } else {
                    //En el caso que no sean iguales se hara lo mismo pero llamando a la id Contraria
                    ID id = this.shape.getLeg2().getCable().getIds()[0];
                    PaintSwitch(id, this.shape, this.shape.getLeg2(), cables);
                }
                //Aca preguntamos en esta condición es que si la ID del EndLeg es igual a la de la pata 1
            } else if (this.EndLeg != null && ID.isSameID(this.EndLeg.getID(), this.shape.getLeg1().getID())) {
                //Y se realiza lo mismo que se hizo anterior mente pero con la otra pata
                this.shape.getLeg1().setState(this.shape.getLeg2().getState());
                if (ID.isSameID(this.shape.getLeg1().getID(), this.shape.getLeg1().getCable().getIds()[0])) {
                    ID id = this.shape.getLeg1().getCable().getIds()[1];
                    PaintSwitch(id, this.shape, this.shape.getLeg1(), cables);
                } else {
                    ID id = this.shape.getLeg1().getCable().getIds()[0];
                    PaintSwitch(id, this.shape, this.shape.getLeg1(), cables);
                }
            }
        } else{
            //Cuando el paso de energia es False y preguntamos si es que existe el EndLeg
            if (this.EndLeg != null && this.shape.getLeg1().hasCable() && this.shape.getLeg2().hasCable()){
                //Queremos que cuando sea falso despintar entonces preguntamos si las dos patas tienen energia y su cable correspondiente
                if(this.shape.getLeg1().hasEnergy() && this.shape.getLeg2().hasEnergy() && this.shape.getLeg1().hasCable() && this.shape.getLeg2().hasCable()){
                    //Pregutamos si el estado que tienen son los mismos
                    if(this.shape.getLeg1().getState() == this.shape.getLeg2().getState()){
                        //Despues preguntamos por el EndLeg
                        if(ID.isSameID(this.EndLeg.getCable().getIds()[0], this.EndLeg.getID())){
                            //Encontramos la id que no es perteneciente al Switch
                            UnPaintSwitch(this.EndLeg.getCable().getSecondCircle(), this.shape, this.EndLeg );
                        } else{
                            UnPaintSwitch(this.EndLeg.getCable().getFirstCircle(), this.shape, this.EndLeg );
                        }
                    } //NO sucede nada cuando los estados de las patas son distintas
                }
            }
        }
    }



    //Este metodo lo que hace es pintar El gridpane segun el GridPane que corresponda
    public  void PaintSwitch(ID id, CustomShape customShape, CustomCircle Leg, ArrayList<Cable> cables) {

        Utils.paintCircles(this.gridPaneObserver, id, Leg.getState());
//        if (this.gridPaneObserver.getFirstGridPaneTrail().getName().equals(id.getGridName())) {
//            //Llamamos a la funcion de Pintar
//            Utils.paintCircles(this.gridPaneObserver, id, Leg.getState());
//        } else if (this.gridPaneObserver.getSecondGridPaneTrail().getName().equals(id.getGridName())) {
//            Utils.paintCircles(this.gridPaneObserver, id, Leg.getState());
/*        } else*/
        if ((id.getGridName().equals("LedVolt1")) || (id.getGridName().equals("switchvolt1"))) {
            //En el caso que el nombre del Grid no es de ninguno de los Gridpane entonces debe ser de Automaticamente del una bateria, LED o Switch.
            if (ID.isSameID(Leg.getID(), Leg.getCable().Getcircles()[0].getID())) {
                Leg.getCable().Getcircles()[1].setState(Leg.getState());
            } else {
                Leg.getCable().Getcircles()[0].setState(Leg.getState());
            }
        }
    }


    //Este metodo lo que hace es Despintar la columna correspondiente que deberia despintarse
    public  void UnPaintSwitch(CustomCircle circle, CustomShape customShape, CustomCircle Leg) {
        ID id = circle.getID();

        Utils.unPaintCircles(this.gridPaneObserver, circle/*, true*/);
        Leg.removeEnergy();
        //si la id dada es del primer gridpane, entonces...

//        if ("gridTrail1".equals(id.getGridName())) {
//            // Llamamos a la función de Despintar
//            Utils.unPaintCircles(this.gridPaneObserver, circle/*, true*/);
//            Leg.removeEnergy();
//        } else if ("gridTrail2".equals(id.getGridName())) {
//            Utils.unPaintCircles(this.gridPaneObserver, circle /*, true*/);
//            Leg.removeEnergy();
/*         else*/
        if ((id.getGridName().equals("LedVolt1")) || (id.getGridName().equals("switchvolt1"))) {
            // En el caso que el nombre del Grid no es de ninguno de los Gridpane entonces debe ser de Automaticamente del una bateria, LED o Switch.
            if (ID.isSameID(Leg.getID(), Leg.getCable().Getcircles()[0].getID())) {
                Leg.getCable().Getcircles()[1].removeEnergy();
            } else {
                Leg.getCable().Getcircles()[0].removeEnergy();
            }

        }
    }

    //Este metodo lo que realiza es decidir la pata a la cual sabremos hacia donde se quitara la energía
    public void DecideEndLeg(CustomShape customShape){
        if(customShape.getLeg1().hasCable() && !(customShape.getLeg2().hasCable())){
            this.EndLeg = customShape.getLeg2();
        } else if( customShape.getLeg2().hasCable() && !(customShape.getLeg1().hasCable())){
            this.EndLeg = customShape.getLeg1();
        }
    }

    //Este metodo lo que hace es preguntar si unicamente el EndLeg tiene energia y la otra no tiene para asi quitarle la carga
    public void OnlyEndLegHaveEnergy(){
        if(this.EndLeg == null) return;
        //Preguntamos si EndLeg tiene energia
        if(this.EndLeg.hasEnergy()){
            //Preguntamos si la ID de EndLeg es igual la primera pata del CustomShape
            if(ID.isSameID(this.EndLeg.getID(), this.shape.getLeg1().getID())){
                //Si es asi preguntamos si EndLeg tiene Energia Y si la segunda pata NO tiene energia
                if(this.EndLeg.hasEnergy() && !(this.shape.getLeg2().hasEnergy())){
                    //Despues le quitamos la energia a la pata 1
                    this.shape.getLeg1().removeEnergy();
                    this.EndLeg.removeEnergy();
                }
            } else{
                if(this.EndLeg.hasEnergy() && !(this.shape.getLeg1().hasEnergy())){
                    this.shape.getLeg1().removeEnergy();
                    this.EndLeg.removeEnergy();
                }
            }
        }
    }


    public  void setPasoDeCarga(boolean state){
        this.PasoDeCarga = state;
    }
    public void SetEndLeg(CustomCircle c){ this.EndLeg=c;}

    public CustomCircle getEndLeg(){
        return this.EndLeg;
    }

    public String getUniqueId(){
        return this.UniqueId;
    }
    public AnchorPane getroot(){
        return this.root;
    }

    public CustomCircle[] getCircles(){
        return customCircles;
    }

    public boolean getPasoDeCarga(){
        return this.PasoDeCarga;
    }
}