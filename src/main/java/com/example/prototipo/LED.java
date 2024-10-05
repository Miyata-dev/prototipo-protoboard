package com.example.prototipo;

import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class LED extends Group {
    public  boolean state;  //false -> apagado      true-> encendido
    private Basurero basurero;
    private AnchorPane root;
    private CustomCircle[] legs;
    private String UniqueId;
    private CustomShape shape;

    public LED(boolean state, CustomShape customShape, Basurero basurero, AnchorPane root, GridPaneObserver gridPaneObserver) {
        super(customShape);
        this.shape = customShape;
        this.state = state;
        this.legs = new CustomCircle[] {
            customShape.getLeg1(),
            customShape.getLeg2()
        };
        this.UniqueId = customShape.getUniqueID();
        this.basurero = basurero;
        this.root = root;
        LedFunction();

        Utils.makeDraggableNode(this, customShape.getStartX(), customShape.getStartY());
        init(customShape);
        gridPaneObserver.addLeds(this);


        //Este AtomicBoolean lo que hace es que cada vez que se haga click en el LED el setisTaken sea siempre false
        AtomicBoolean LegTaken = new AtomicBoolean(true);

        this.setOnMouseClicked(e -> {
            //Al momento de soltar el LED se pueden crear Cables
            if(LegTaken.get()){
                Utils.makeUndraggableNode(this);
                customShape.getLeg1().setisTaken(false);
                customShape.getLeg2().setisTaken(false);
                LegTaken.set(false);
            }
            //fuciona las dos ids de las patas y crea una ID a partir de las 2.

            System.out.println("pata 1: " + customShape.getLeg1().getID().getGeneratedID());
            System.out.println("pata 2: " + customShape.getLeg2().getID().getGeneratedID());

            if (basurero.getIsActive() && customShape.getHasMoved()) {
                //Llamamos al metodo del Basurero para borrar los cables que pueden tener el LED y despues borrar este mismo
                basurero.EliminateElements(customShape, e, root, gridPaneObserver, this);
                gridPaneObserver.removeLeds(this);
                root.getChildren().remove(this);
            }

            if (basurero.getIsActive()) customShape.setHasMoved(true);
        });
    }

    //Este metodo crea las patas del LED de manera ordenada
    public void init(CustomShape customShape) {
        //Las ID del LED son unicas para futuras verificaciones
        ID idUno = new ID(1, 1, "LedVolt1");
        idUno.setIsForGridpane(false);

        ID idDos = new ID(2, 1, "LedVolt1");
        idDos.setIsForGridpane(false);

        double x = customShape.getX();
        double y = customShape.getY();

        customShape.setLeg1(new CustomCircle(5, idUno, 0));
        customShape.setLeg2(new CustomCircle(5, idDos, 0));
        //Creamos la primera pata del LED
        customShape.getLeg1().setisTaken(true);
        customShape.getLeg1().setFill(Color.ROYALBLUE);
        customShape.getLeg1().setTranslateX(x - 5);
        customShape.getLeg1().setTranslateY(y + 7.5);
        //Creamos la segunda pata del LED
        customShape.getLeg2().setisTaken(true);
        customShape.getLeg2().setFill(Color.ROYALBLUE);
        customShape.getLeg2().setTranslateX(x + 30);
        customShape.getLeg2().setTranslateY(y + 7.5);

        //Se añaden al Grupo del LED
        this.getChildren().add(customShape.getLeg1());
        this.getChildren().add(customShape.getLeg2());
    }


    //Este metodo lo que hace es cambiar el color del LED cuando Cambia su estado
    public void LedFunction() {
        //false -> apagado      true-> encendido
        if (GetState()) {
            //Cuando el LED esta encendido el relleno se mostrara Verde y si esta apagado sera Rojo
            this.shape.setFill(Color.GREEN);
        } else {
            this.shape.setFill(Color.RED);
        }
    }

    //Metodo para mostrar si el LED esta encendido o Apagado

    public void ONorOFF(){
        //Si las dos patas no tienen cables entonces se sale del metodo
        if(this.shape.getLeg1().hasCable() && this.shape.getLeg2().hasCable()){
            //Llamamos el metodo para actualizar el estado de las patas segun el cable
            GetStateofLegFromCable();
            //Preguntamos si es que las dos patas tienen energia
            if( this.shape.getLeg1().hasEnergy() && this.shape.getLeg2().hasEnergy()){
                //Vemos si el estado de las 2 patas son distintas entre si
                if(this.shape.getLeg2().getState() != this.shape.getLeg1().getState()){
                    //al ser distintas actualizamos el estado a true que es igual a encendido
                    SetState(true);
                    //Y lo cambiamos para que se vea graficamente
                    LedFunction();
                } else {
                    SetState(false );
                    LedFunction();
                }
            } else {
                SetState(false );
                LedFunction();
            }
        }
    }

    //Este metodo lo que es conseguir el estado de las Patas del LED por medio de los cables asignados
    public void GetStateofLegFromCable(){
        //Preguntamos si la primera pata es igual al primer circulo del cable
        if(ID.isSameID(this.shape.getLeg1().getID(), this.shape.getLeg1().getCable().getFirstCircle().getID())){
            //Al ver que los dos circulos son iguales entonces sabemos que el que es distinto es el segundo
            this.shape.getLeg1().setState(this.shape.getLeg1().getCable().getSecondCircle().getState());
        } else{
            //Sabemos que las id no son iguales entonces actualizamos el estado
            this.shape.getLeg1().setState(this.shape.getLeg1().getCable().getFirstCircle().getState());
        }
        //Ahora pregutamos por la segunda pata del LED
        if(ID.isSameID(shape.getLeg2().getID(), this.shape.getLeg2().getCable().getFirstCircle().getID())){
            this.shape.getLeg2().setState(this.shape.getLeg2().getCable().getSecondCircle().getState());
        } else {
            this.shape.getLeg2().setState(this.shape.getLeg2().getCable().getFirstCircle().getState());
        }
    }



    //actualiza el estado del led que se le pase por parametro, es estático puesto que este método sirve para hacer el led reactivo a los cambios de energía del protoboard.
    public static void updateState(LED led, boolean state) {
        System.out.println("leg uno: " + led.getLeg1() + " leg dos: " + led.getLeg2());
        //mira que el led tenga conectado cables.
        if (!led.getLeg1().hasCable() || !led.getLeg2().hasCable()) return;
        //si uno de los cables no tiene energía no se cambia el estado.
        if (led.getLeg1().getCable().getTipodecarga() == 0 || led.getLeg2().getCable().getTipodecarga() == 0) return;

        if(!state){
            led.shape.getLeg1().removeEnergy();
            led.shape.getLeg2().removeEnergy();
        } else{
            led.GetStateofLegFromCable();
        }
        led.SetState(state);
        led.LedFunction(); //este método reacciona al estado que se le pase.
    }

    //Este metodo lo que hace es actualizar al led segun si se quiere que este o no activado, Esta hecho especificamente para cuando se active y desactive el Protoboard
    public static void UpdatingState(LED led, boolean state){
        //si el state pasado es false entonces le quitamos la energia a las patas
        if(!state){
            led.shape.getLeg1().removeEnergy();
            led.shape.getLeg2().removeEnergy();
        } else{
            // cuando es true buscamos el estado de las patas a base del cable
            led.GetStateofLegFromCable();
        }
        // y llamamos al metodo para ver si se enciende o no se enciende el led graficamente
        led.ONorOFF();
    }

    public void SetState(boolean states){
        this.state = states;
    }
    public boolean GetState(){
        return this.state;
    }

    public CustomShape getCustomShape() {
        return (CustomShape) this.getChildren().get(0);
    }
    public CustomCircle[] getCircles() {
        return legs;
    }
    public CustomCircle getLeg1() {
        return this.getCustomShape().getLeg1();
    }
    public CustomCircle getLeg2() {
        return this.getCustomShape().getLeg2();
    }
    public String getUniqueId(){return this.UniqueId;}
}