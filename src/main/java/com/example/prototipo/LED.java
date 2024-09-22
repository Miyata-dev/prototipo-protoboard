package com.example.prototipo;

import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.Set;

public class LED extends Group {
    public static boolean state;  //false -> apagado      true-> encendido
    private Basurero basurero;
    private AnchorPane root;
    private CustomCircle[] legs;

    public LED(boolean state, CustomShape customShape, Basurero basurero, AnchorPane root) {
        super(customShape);
        LED.state = state;
        this.legs = new CustomCircle[] {
            customShape.getLeg1(),
            customShape.getLeg2()
        };

        this.basurero = basurero;
        this.root = root;
        LedFunction(customShape);

        Utils.makeDraggableNode(this, customShape.getStartX(), customShape.getStartY());
        init(customShape);

        this.setOnMouseClicked(e -> {
            //Al momento de soltar el LED se pueden crear Cables
            customShape.getLeg1().setisTaken(false);
            customShape.getLeg2().setisTaken(false);
            Utils.makeUndraggableNode(this);

            //fuciona las dos ids de las patas y crea una ID a partir de las 2.

            System.out.println("pata 1: " + customShape.getLeg1().getID().getGeneratedID());
            System.out.println("pata 2: " + customShape.getLeg2().getID().getGeneratedID());

            if (basurero.getIsActive()) {
                //Llamamos al metodo del Basurero para borrar los cables que pueden tener el LED y despues borrar este mismo
                basurero.EliminateElements(customShape, e, root);
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
    public static void LedFunction(CustomShape customShape) {
        //false -> apagado      true-> encendido
        if (GetState()) {
            //Cuando el LED esta encendido el relleno se mostrara Verde y si esta apagado sera Rojo
            customShape.setFill(Color.GREEN);
        } else {
            customShape.setFill(Color.RED);
        }
    }

    //Metodo para mostrar si el LED esta encendido o Apagado
    public static void ONorOFF(CustomShape customShape) {
        //Preguntamos si las dos patas tienen energía y ademas la energia que tienen es distinta entre si
        if ((customShape.getLeg2().hasEnergy() && customShape.getLeg1().hasEnergy()) && (customShape.getLeg2().getState() != customShape.getLeg1().getState())) {
            //al ser distintas actualizamos el estado a true que es igual a encendido
            SetState(true);
            //Y lo cambiamos para que se vea graficamente
            LedFunction(customShape);
        } else {
            //en el caso que la condicion sea false se cambiara el state a false que es igual a apagado y se cambiara graficamente el LED a apagado
            SetState(false );
            LedFunction(customShape);
        }
    }

    public static void SetState(boolean states){
        state = states;
    }
    public static boolean GetState(){
        return state;
    }

    public CustomShape getCustomShape() {
        return (CustomShape) this.getChildren().get(0);
    }

    public CustomCircle[] getLegs() {
        return legs;
    }
}