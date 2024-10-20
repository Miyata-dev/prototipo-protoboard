package com.example.prototipo;


import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


public class Switch2 extends Group {
    private ImageView Shape;
    private CustomCircle Leg1;
    private CustomCircle Leg2;
    private CustomCircle Leg3;
    private CustomCircle Leg4;

    private ArrayList<CustomCircle> Legs = new ArrayList<>(); // son las patas que tiene el Switch2

    private Boolean ChargePass;
    private String UniqueId;
    private GridPaneObserver gridPaneObserver;
    private AnchorPane root;
    private Basurero basurero;
    private Boolean isPlacedCorrectly = true;

    public Switch2(Boolean chargePass, GridPaneObserver gridPaneObserver, AnchorPane root, Basurero basurero){
        //Le damos una ID unica
        this.UniqueId = Utils.createRandomID();
        this.ChargePass = chargePass;
        this.gridPaneObserver = gridPaneObserver;
        this.root = root;
        this.basurero = basurero;

        //Se crea la figura
        this.init();


        Utils.makeDraggableNode(this, new AtomicReference<>((double) 0), new AtomicReference<>((double) 0));
        manageEvents();
    }


    //Metodos

    //Este metodo lo que hace es crear el elemento para que se vea graficamente
    public void init(){
        //Declaramos la imagen dentro de la clase ya que siempre va a ser la misma imagen
        this.Shape = new ImageView(getClass().getResource("Switch.png").toExternalForm());
        //Declaramos su tamaño y su ubicación para despues agregarlo al grupo
        this.Shape.setFitWidth(50);
        this.Shape.setFitHeight(50);
        double x = 720 + 25;
        double y = 554 + 25;
        Shape.setTranslateX(720);
        Shape.setTranslateY(554);



        //Ahora viene agregar las 4 patas que tiene el Switch

        //Creamos ID's para cada pata del Switch nuevo
        ID idOne = new ID (1,1, "switchvolt1");
        ID idTwo = new ID (1,2, "switchvolt1");
        ID idThree = new ID (1,3, "switchvolt1");
        ID idFour = new ID(1,4, "switchvolt1");

        //Le damos a las id de las patas que no son para gridPane
        idOne.setIsForGridpane(false);
        idTwo.setIsForGridpane(false);
        idThree.setIsForGridpane(false);
        idFour.setIsForGridpane(false);

        //Creamos las patas
        this.Leg1 = new CustomCircle(4, idOne, 0);
        this.Leg2 = new CustomCircle(4, idTwo, 0);
        this.Leg3 = new CustomCircle(4, idThree, 0 );
        this.Leg4 = new CustomCircle(4, idFour, 0);

        //Lo que hacemos es dejarlos como tomados ya que no sera necesario el que tiren cables
        this.Leg1.setisTaken(true);
        this.Leg2.setisTaken(true);
        this.Leg3.setisTaken(true);
        this.Leg4.setisTaken(true);

        //Ahora movemos los circulos en X
        this.Leg1.setTranslateX(x-17.5);
        this.Leg2.setTranslateX(x+17.5);
        this.Leg3.setTranslateX(x-17.5);
        this.Leg4.setTranslateX(x+17.5);

        //Ahora movemos los circulos en Y
        this.Leg1.setTranslateY(y+20);
        this.Leg2.setTranslateY(y+20);
        this.Leg3.setTranslateY(y-20);
        this.Leg4.setTranslateY(y-20);

        //A los circulos se le asignaran sus coordenadas cuando el elemento ya no sea draggable por el UnDragglableNode del Utils

        //Agregamos las patas a la coleccion de Legs
        this.Legs.add(this.Leg1);
        this.Legs.add(this.Leg2);
        this.Legs.add(this.Leg3);
        this.Legs.add(this.Leg4);

        //Agregamos la Imagen y los CustomCircle al grupo del Switch
        this.getChildren().add(this.Shape);
        this.getChildren().add(this.Leg1);
        this.getChildren().add(this.Leg2);
        this.getChildren().add(this.Leg3);
        this.getChildren().add(this.Leg4);
    }

    //Este metodo lo que hara
    public void manageEvents(){
        ArrayList<CustomCircle> closestCircles = new ArrayList<>();
        ArrayList<CustomCircle> circlesCollecition = gridPaneObserver.getCirclesCollection();


        //Cuando se da click al Switch
        this.setOnMouseClicked(e-> {
            //preguntamos si es que el basurero esta activo
            if(basurero.getIsActive()){
                //Removemos el Grupo del root
                gridPaneObserver.getRoot().getChildren().remove(this);
                //y despues por cada cada pata seteamos a los circulos que no son del Switch2
                for (CustomCircle leg : Legs) {
                    leg.getCable().getSecondCircle().setisTaken(false);
                    //Eliminamos el cable del ciruclo que no es del switch
                    leg.getCable().getSecondCircle().setCable(null);
                    //Eliminamos el cable de la coleccion del gridPaneObserver
                    gridPaneObserver.removeCable(leg.getCable());
                    //y despues lo eliminamos visualmente
                    root.getChildren().remove(leg.getCable());
                }
            }
        });
        this.setOnMouseReleased(e->{
            closestCircles.clear();
            //isPlacedCorrectly = false;

            //Obtenemos la ubicacion de la imagen del Switch para asi asignarle las coordenadas al cada pata
            double x = Shape.localToScreen(Shape.getX(), Shape.getY()).getX() + 25;
            double y = Shape.localToScreen(Shape.getX(), Shape.getY()).getY() + 25;

            //Les setteamos las coordenadas correspondientes
            this.Leg1.setCoords(x-17.5, y+20);
            this.Leg2.setCoords(x+17.5, y+20);
            this.Leg3.setCoords(x-17.5, y-20);
            this.Leg4.setCoords(x+17.5, y-20);

            double maxRange = (circlesCollecition.get(0).getRadius() * 2) - (Leg1.getRadius() * 2) +  4; //ese es el rango maximo que puede tener.

            for (CustomCircle leg : Legs) {
                CustomCircle circleFound = Utils.getClosestCircle(circlesCollecition,leg.getX(), leg.getY());

                if(circleFound.hasCable()) return;

                double distanceY = Math.abs(leg.getY() - circleFound.getY());
                if( distanceY >= maxRange){
                    //todo revisar esto
                    //isPlacedCorrectly = false;
                } else {// NO esta entrando
                    if(!closestCircles.contains(circleFound)){
                        closestCircles.add(circleFound);
                        System.out.println("id: " + circleFound.getID());
                    }
                };
            }

            if (isPlacedCorrectly){
                //se toman los gridNames del primer círculo de arriba y el último círculo de abajo.,

                String firstCircleGridName = closestCircles.get(0).getID().getGridName();
                String secondCircleGridName = closestCircles.get(closestCircles.size() - 1).getID().getGridName();

                //TODO REVISAR ESTO
//                if(firstCircleGridName.equals(secondCircleGridName)){
//                    //Utils.makeUndraggableNode(this);
//                    return;
//                }
                //preguntamos si la cantidad de la coleccion es la misma que la de las patas
                if (closestCircles.size() == 4){
                    //Ahora por cada circulo encontrado hay que crearle un cable
                    AtomicInteger i = new AtomicInteger();
                    i.set(0);
                    System.out.println("-----------------------------------------");
                    closestCircles.forEach( circle ->{
                        circle.setisTaken(true);
                        Cable cable = new Cable (Legs.get(i.get()), circle);
                        circle.setCable(cable);
                        Legs.get(i.get()).setCable(cable);
                        gridPaneObserver.getRoot().getChildren().add(cable);
                        System.out.println("cable: " + cable.getFirstCircle().getID() +" || "+   cable.getSecondCircle().getID());
                        i.getAndIncrement();
                    });
                    System.out.println("-------------------------------------------");
                    Utils.makeUndraggableNode(this);
                }

            }
        });
    }





    public void clearcoords(ArrayList<CustomCircle> circles ){
        for (CustomCircle circle : circles) {
            circle.setCoords(0,0);
        }
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
