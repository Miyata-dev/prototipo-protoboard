package com.example.prototipo;


import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;


public class Switch extends Group {//Se utiliza un rectangulo para hacer un cuadrado
    private static boolean PasoDeCarga;// true -> deja la carga pasar           false -> la carga no pasa
    private static GridPaneTrailController gridPane1;
    private static GridPaneTrailController gridPane2;
    private Basurero basurero;
    private static AnchorPane root;
    private static CustomCircle EndLeg;


    public Switch(boolean PasoDeCarga, CustomShape customShape, GridPaneTrailController grid1, GridPaneTrailController grid2, Basurero basurero, AnchorPane root) {
        super(customShape);
        Switch.PasoDeCarga = PasoDeCarga;
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

            Switch.PasoDeCarga = !Switch.PasoDeCarga;
            ChargePass(customShape);


            System.out.println("el paso de carga" + this.PasoDeCarga);
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

        //Se añaden al grupo de la Clase Switch
        this.getChildren().add(customShape.getLeg1());
        this.getChildren().add(customShape.getLeg2());
    }

    //TODO REVISAR TODO FLUJO DE CONDICIONES ESTA ENREDADO

    //Este Metodo lo que hace es realizar la funcionalidad del Switch
    public static void ChargePass(CustomShape customShape){
        //Se puede preguntar si es que las dos patas tienen cable, si es asi y el paso de energia es falso se despinta si las energias son falsas
        //Para eso tenemos que modificar la condicion y el flujo de energia

        if(!Switch.PasoDeCarga && EndLeg != null && customShape.getLeg2().hasCable() && customShape.getLeg1().hasCable()){
            if (customShape.getLeg1().getCable().getTipodecarga() == customShape.getLeg2().getCable().getTipodecarga()){
                if( ID.isSameID(Switch.EndLeg.getCable().getIds()[0],Switch.EndLeg.getID() )) {
                }

            }

        }
        if(!Switch.PasoDeCarga && EndLeg != null && customShape.getLeg1().hasEnergy() && customShape.getLeg2().hasEnergy() && customShape.getLeg2().hasCable() && customShape.getLeg1().hasCable()){
            System.out.println("HIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII");
            if(customShape.getLeg1().getState() != customShape.getLeg2().getState()){
                if( ID.isSameID(Switch.EndLeg.getCable().getIds()[0],Switch.EndLeg.getID() )){
                    System.out.println("AAAA");
                    UnPaintSwitch(Switch.EndLeg.getCable().getIds()[1], customShape, EndLeg);
                    //UnPaintSwitch(Switch.EndLeg.getID(), customShape, Switch.EndLeg);
                } else{
                    System.out.println("AAAAAAAAAAAAAAAA");
                    UnPaintSwitch(Switch.EndLeg.getCable().getIds()[0], customShape, EndLeg );
                }
            } else{
                if(ID.isSameID(customShape.getLeg1().getID(), customShape.getLeg1().getCable().getIds()[0])){
                    UnPaintSwitch(customShape.getLeg1().getCable().getIds()[1], customShape, customShape.getLeg2());
                    if(ID.isSameID(customShape.getLeg2().getID(), customShape.getLeg2().getCable().getIds()[0])){
                        UnPaintSwitch(customShape.getLeg2().getCable().getIds()[1], customShape, customShape.getLeg2() );
                    } else{
                        UnPaintSwitch(customShape.getLeg2().getCable().getIds()[0], customShape, customShape.getLeg2() );
                    }
                } else {
                    UnPaintSwitch(customShape.getLeg1().getCable().getIds()[0], customShape, customShape.getLeg1() );
                    if(ID.isSameID(customShape.getLeg2().getID(), customShape.getLeg2().getCable().getIds()[0])){
                        UnPaintSwitch(customShape.getLeg2().getCable().getIds()[1], customShape, customShape.getLeg2() );
                    } else{
                        UnPaintSwitch(customShape.getLeg2().getCable().getIds()[0], customShape, customShape.getLeg2() );
                    }
                }
            }
        }

        //Si las dos patas tienen Energia no pasa nada, pero si llegan a ser distintas "el componente deberia explotar"
        if(customShape.getLeg1().hasEnergy() && customShape.getLeg2().hasEnergy() && customShape.getLeg1().hasCable() && customShape.getLeg2().hasCable() && Switch.EndLeg != null && Switch.PasoDeCarga) {
            if(customShape.getLeg1().getState() == customShape.getLeg2().getState()){
                if( ID.isSameID(Switch.EndLeg.getCable().getIds()[0],Switch.EndLeg.getID() )){
                    System.out.println("AAAA");
                    UnPaintSwitch(Switch.EndLeg.getCable().getIds()[1], customShape, EndLeg);
                    //UnPaintSwitch(Switch.EndLeg.getID(), customShape, Switch.EndLeg);
                } else{
                    System.out.println("AAAAAAAAAAAAAAAA");
                    UnPaintSwitch(Switch.EndLeg.getCable().getIds()[0], customShape, EndLeg );
                }

            }
            if (ID.isSameID(Switch.EndLeg.getID(), Switch.EndLeg.getCable().Getcircles()[0].getID())) {
                if (Switch.EndLeg.getState() != Switch.EndLeg.getCable().Getcircles()[1].getState()) {
                    PaintSwitch(Switch.EndLeg.getCable().Getcircles()[1].getID(), customShape, Switch.EndLeg);
                    System.out.println("B");
                }
            }else {
                if(Switch.EndLeg.getState() != Switch.EndLeg.getCable().Getcircles()[0].getState()){
                    PaintSwitch(Switch.EndLeg.getCable().Getcircles()[0].getID(), customShape, Switch.EndLeg);
                    System.out.println("A");
                }
            }
        }

        if(customShape.getLeg1().hasEnergy() && customShape.getLeg2().hasEnergy())
        //Preguntamos si se puede pasar energia y si una pata tiene energia y la otra pata tiene cable, ya que para pasarla necesitamos que al menos tenga un cable
        if(customShape.getLeg1().hasEnergy() && customShape.getLeg2().hasCable() && Switch.PasoDeCarga){
            System.out.println(customShape.getLeg1().getState());
            //Si la Pata1 tiene energia entonces tenemos que cambiar el Estado de la segunda al estado correspondiente
            customShape.getLeg2().setState(customShape.getLeg1().getState());
            customShape.getLeg2().getCable().setTipodecarga(customShape.getLeg1().getState());
            //Esta condición compara la ID del CustomCircle es la misma con una de las ID que almacena el Cable.
            if(ID.isSameID(customShape.getLeg2().getID(), customShape.getLeg2().getCable().getIds()[0])){
                //En el caso que son la misma id, Se guarda la contraria ya que no es el mismo
                ID id = customShape.getLeg2().getCable().getIds()[1];
                PaintSwitch(id, customShape, customShape.getLeg2());
            } else{
                ID id = customShape.getLeg2().getCable().getIds()[0];
                PaintSwitch(id, customShape, customShape.getLeg2());
            }
        } else if( customShape.getLeg2().hasEnergy() && Switch.PasoDeCarga && customShape.getLeg1().hasCable()){
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

        } else{
            //La primera vez cuando ninguno de los dos tiene un estado va a tirar error porque no se puede determinar el estado
            if(customShape.getLeg1().hasCable() && !customShape.getLeg2().hasCable()){
                customShape.getLeg1().setState(customShape.getLeg1().getCable().getTipodecarga());
            } else if (customShape.getLeg2().hasCable() && !(customShape.getLeg1().hasCable())){
                customShape.getLeg2().setState(customShape.getLeg2().getCable().getTipodecarga());
            }
        }
        DecideEndLeg(customShape);
        if(EndLeg != null){
            System.out.printf("End Leg is " + EndLeg.getID());
        }
    }

    //Este metodo lo que hace es pintar El gridpane segun el GridPane que corresponda
    public static void PaintSwitch(ID id, CustomShape customShape, CustomCircle Leg) {
        if ("gridTrail1".equals(id.getGridName())) {
            //Llamamos a la funcion de Pintar
            Utils.paintCircles(gridPane1.getGridPane(), id, Leg.getState());
        } else if ("gridTrail2".equals(id.getGridName())) {
            Utils.paintCircles(gridPane2.getGridPane(), id, Leg.getState());
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
            Utils.unPaintCircles(gridPane1.getGridPane(), id.getIndexColumn());
        } else if ("gridTrail2".equals(id.getGridName())) {
            Utils.unPaintCircles(gridPane2.getGridPane(), id.getIndexColumn());
        } else if ((id.getGridName().equals("LedVolt1")) || (id.getGridName().equals("switchvolt1"))) {
            // En el caso que el nombre del Grid no es de ninguno de los Gridpane entonces debe ser de Automaticamente del una bateria, LED o Switch.
            if (ID.isSameID(Leg.getID(), Leg.getCable().Getcircles()[0].getID())) {
                Leg.getCable().Getcircles()[1].setState(0);
            } else {
                Leg.getCable().Getcircles()[0].setState(0);
            }
        }
        Leg.setState(0);
    }

    public static void DecideEndLeg(CustomShape customShape){
        if(customShape.getLeg1().hasCable() && !(customShape.getLeg2().hasCable())){
            SetEndLeg(customShape.getLeg2());
        } else if ( customShape.getLeg2().hasCable() && !(customShape.getLeg1().hasCable())){
            SetEndLeg(customShape.getLeg1());
        } else {
            SetEndLeg(null);
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
