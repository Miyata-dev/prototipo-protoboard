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

            if (basurero.getIsActive()) {
                //Llamamos al metodo del Basurero para borrar los cables que pueden tener el LED y despues borrar este mismo
                basurero.EliminateElements(customShape, e, root, gridPaneObserver, this);
                gridPaneObserver.removeLeds(this);
                root.getChildren().remove(this);
            }
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
    public void ONorOFF() {
        //Preguntamos si las dos patas tienen energía y ademas la energia que tienen es distinta entre si
        if ((this.shape.getLeg2().hasEnergy() && this.shape.getLeg1().hasEnergy()) && (this.shape.getLeg2().getState() != this.shape.getLeg1().getState())) {
            //al ser distintas actualizamos el estado a true que es igual a encendido
            SetState(true);
            //Y lo cambiamos para que se vea graficamente
            LedFunction();
        } else {
            //en el caso que la condicion sea false se cambiara el state a false que es igual a apagado y se cambiara graficamente el LED a apagado
            SetState(false );
            LedFunction();
        }
    }
    //actualiza el estado del led que se le pase por parametro, es estático puesto que este método sirve para
    //hacer el led reactivo a los cambios de energía del protoboard.
    public static void updateState(LED led, boolean state) {
        System.out.println("leg uno: " + led.getLeg1() + " leg dos: " + led.getLeg2());
        //mira que el led tenga conectado cables.
        if (!led.getLeg1().hasCable() || !led.getLeg2().hasCable()) return;
        //si uno de los cables no tiene energía no se cambia el estado.
        if (led.getLeg1().getCable().getTipodecarga() == 0 || led.getLeg2().getCable().getTipodecarga() == 0) return;

        led.SetState(state);
        led.LedFunction(); //este método reacciona al estado que se le pase.
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