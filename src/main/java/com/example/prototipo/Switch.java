package com.example.prototipo;


import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


public class Switch extends Group {
    private ImageView Shape;
    private CustomCircle Leg1;
    private CustomCircle Leg2;
    private CustomCircle Leg3;
    private CustomCircle Leg4;

    private ArrayList<CustomCircle> Legs = new ArrayList<>(); // son las patas que tiene el Switch2
    private CustomCircle[] UpperLegs;
    private CustomCircle[] LowerLegs;

    private Boolean ChargePass;
    private String UniqueId;
    private GridPaneObserver gridPaneObserver;
    private AnchorPane root;
    private Basurero basurero;
    private Boolean isPlacedCorrectly = true;
    private Boolean isUndragableAlready;
    private Boolean isBurned;
    private Boolean hasmoved;

    public Switch(Boolean chargePass, GridPaneObserver gridPaneObserver, AnchorPane root, Basurero basurero) {
        //Le damos una ID unica
        this.UniqueId = Utils.createRandomID();
        this.ChargePass = true;
        this.gridPaneObserver = gridPaneObserver;
        this.root = root;
        this.basurero = basurero;
        this.isUndragableAlready = false;
        this.isBurned = false;
        this.hasmoved = false;

        //Se crea la figura
        this.init();


        Utils.makeDraggableNode(this, new AtomicReference<>((double) 0), new AtomicReference<>((double) 0));
        manageEvents();
    }


    //Metodos

    //Este metodo lo que hace es crear el elemento para que se vea graficamente
    public void init() {
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
        ID idOne = new ID(1, 1, "switchvolt1");
        ID idTwo = new ID(1, 2, "switchvolt1");
        ID idThree = new ID(1, 3, "switchvolt1");
        ID idFour = new ID(1, 4, "switchvolt1");

        //Le damos a las id de las patas que no son para gridPane
        idOne.setIsForGridpane(false);
        idTwo.setIsForGridpane(false);
        idThree.setIsForGridpane(false);
        idFour.setIsForGridpane(false);

        //Creamos las patas
        this.Leg1 = new CustomCircle(4, idOne, 0);
        this.Leg2 = new CustomCircle(4, idTwo, 0);
        this.Leg3 = new CustomCircle(4, idThree, 0);
        this.Leg4 = new CustomCircle(4, idFour, 0);

        //Lo que hacemos es dejarlos como tomados ya que no sera necesario el que tiren cables
        this.Leg1.setisTaken(true);
        this.Leg2.setisTaken(true);
        this.Leg3.setisTaken(true);
        this.Leg4.setisTaken(true);

        //Ahora movemos los circulos en X
        this.Leg1.setTranslateX(x - 17.5);
        this.Leg2.setTranslateX(x + 17.5);
        this.Leg3.setTranslateX(x - 17.5);
        this.Leg4.setTranslateX(x + 17.5);

        //Ahora movemos los circulos en Y
        this.Leg1.setTranslateY(y - 20);
        this.Leg2.setTranslateY(y - 20);
        this.Leg3.setTranslateY(y + 20);
        this.Leg4.setTranslateY(y + 20);

        //A los circulos se le asignaran sus coordenadas cuando el elemento ya no sea draggable por el UnDragglableNode del Utils

        //Agregamos las patas a la coleccion de Legs
        this.Legs.add(this.Leg1);
        this.Legs.add(this.Leg2);
        this.Legs.add(this.Leg3);
        this.Legs.add(this.Leg4);


        //Ahora lo Agregamos a u
        this.UpperLegs = new CustomCircle[]{
                Legs.get(0),
                Legs.get(1)
        };
        this.LowerLegs = new CustomCircle[]{
                Legs.get(2),
                Legs.get(3)
        };

        //Agregamos la Imagen y los CustomCircle al grupo del Switch
        this.getChildren().add(this.Shape);
        this.getChildren().add(this.Leg1);
        this.getChildren().add(this.Leg2);
        this.getChildren().add(this.Leg3);
        this.getChildren().add(this.Leg4);
    }

    //Este metodo lo que hara
    public void manageEvents() {
        ArrayList<CustomCircle> closestCircles = new ArrayList<>();
        ArrayList<CustomCircle> circlesCollecition = gridPaneObserver.getCirclesCollection();


        //Cuando se da click al Switch
        this.setOnMouseClicked(e -> {
            //preguntamos si es que el basurero esta activo
            if (basurero.getIsActive() && hasmoved) {
                //Removemos el Grupo del root
                gridPaneObserver.getRoot().getChildren().remove(this);
                //y despues por cada cada pata seteamos a los circulos que no son del Switch2
                for (CustomCircle leg : Legs) {
                    ArrayList<CustomCircle> circles = gridPaneObserver.getCircles(gridPaneObserver, leg.getCable().getSecondCircle().getID());
                    circles.forEach(circle -> {
                        circle.removeEnergy();
                    });
                    leg.getCable().getSecondCircle().setisTaken(false);
                    //Eliminamos el cable del ciruclo que no es del switch
                    leg.getCable().getSecondCircle().setCable(null);
                    //Eliminamos el cable de la coleccion del gridPaneObserver
                    gridPaneObserver.removeCable(leg.getCable());
                    gridPaneObserver.removeColumn(circles);
                    GridPaneObserver.refreshProtoboard(gridPaneObserver);
                }

            } else {
                if (isUndragableAlready && !isBurned) {
                    System.out.println("HERE I AM");
                    this.hasmoved = true;
                    //Cuando el basurero no este activo y ya no se puede mover y se de un click se actualiza el estado
                    this.ChargePass = !this.ChargePass;
                    Function();
                    GridPaneObserver.refreshProtoboard(gridPaneObserver);
                }
            }
        });
        this.setOnMouseReleased(e -> {
            closestCircles.clear();
            //isPlacedCorrectly = false;

            //Obtenemos la ubicacion de la imagen del Switch para asi asignarle las coordenadas al cada pata
            double x = Shape.localToScreen(Shape.getX(), Shape.getY()).getX() + 25;
            double y = Shape.localToScreen(Shape.getX(), Shape.getY()).getY() + 25;

            //Les setteamos las coordenadas correspondientes
            this.Leg1.setCoords(x - 17.5, y - 20);
            this.Leg2.setCoords(x + 17.5, y - 20);
            this.Leg3.setCoords(x - 17.5, y + 20);
            this.Leg4.setCoords(x + 17.5, y + 20);

            double maxRange = (circlesCollecition.get(0).getRadius() * 2) - (Leg1.getRadius() * 2) + 4; //ese es el rango maximo que puede tener.

            for (CustomCircle leg : Legs) {
                CustomCircle circleFound = Utils.getClosestCircle(circlesCollecition, leg.getX(), leg.getY());

                if (circleFound.hasCable() || circleFound.getIsBurned()) return;

                double distanceY = Math.abs(leg.getY() - circleFound.getY());
                if (distanceY >= maxRange) {
                    //todo revisar esto
                    //isPlacedCorrectly = false;
                } else {// NO esta entrando
                    if (!closestCircles.contains(circleFound)) {
                        closestCircles.add(circleFound);
                        System.out.println("id: " + circleFound.getID());
                    }
                }

            }

            if (isPlacedCorrectly) {

                //preguntamos si la cantidad de la coleccion es la misma que la de las patas
                if (closestCircles.size() == 4) {

                    //Creamos un AtomicInteger para asi aumentarlo cada vez y settear las patas de manera mas tranquila
                    AtomicInteger i = new AtomicInteger();
                    i.set(0);
                    System.out.println("-----------------------------------------");

                    //Ahora por cada circulo encontrado hay que crearle un cable
                    closestCircles.forEach(circle -> {
                        circle.setisTaken(true);
                        Cable cable = new Cable(Legs.get(i.get()), circle);
                        circle.setCable(cable);
                        Legs.get(i.get()).setCable(cable);
                        cable.setTipodecarga(circle.getState());
                        gridPaneObserver.addCable(cable);
                        System.out.println("cable: " + cable.getFirstCircle().getID() + " || " + cable.getSecondCircle().getID());
                        i.getAndIncrement();

                    });
                    System.out.println("-------------------------------------------");
                    Utils.makeUndraggableNode(this);
                    this.isUndragableAlready = true;

                }

            }
        });
    }


    //Este metodo lo que hara es la funcionalidad del Switch
    public void Function() {
        if (!ChargePass) {
//            for (CustomCircle leg : Legs) {
//
//
//                leg.removeEnergy();
//                ArrayList<CustomCircle> circles = GridPaneObserver.getCircles(gridPaneObserver, leg.getCable().getSecondCircle().getID());
//                circles.forEach(circle -> {
//                    circle.removeEnergy();
//                });
//                if (Upper != null && leg.getID().equals(Upper.getID())) ;
//                else if (Lower != null && leg.getID().equals(Lower.getID())) {
//
//                } else {
//
//                    gridPaneObserver.removeColumn(circles);
//                }
//
//            }
//
//            return;
//        }
            //Si es que aun no esta Fijo entonces se sale del metodo
        }
        if (!getIsUndragableAlready()) {
            return;
        }

        //Actualizamos la energia desde los circulos
        setEnergyfromClosestCircles(Legs);
        checkisBurned();

        if (isBurned) {
            for (CustomCircle leg : Legs) {

                leg.removeEnergy();
                //if(Upper.
                ArrayList<CustomCircle> circles = GridPaneObserver.getCircles(gridPaneObserver, leg.getCable().getSecondCircle().getID());
                circles.forEach(circle -> {
                    circle.removeEnergy();
                });
                gridPaneObserver.removeColumn(circles);
            }
            return;
        }
        if ((Leg1.hasEnergy() || Leg2.hasEnergy()) && (!Leg3.hasEnergy() || !Leg4.hasEnergy()) && !isBurned) {
            Leg3.setState(Leg1.getState());
            Leg4.setState(Leg1.getState());
            ArrayList<CustomCircle> circles1 = GridPaneObserver.getCircles(gridPaneObserver, Leg3.getID());
            circles1.forEach(circle -> {
                circle.setState(Leg3.getState());
            });
            gridPaneObserver.addColumn(circles1, Leg3.getState());

            ArrayList<CustomCircle> circles2 = GridPaneObserver.getCircles(gridPaneObserver, Leg4.getID());
            circles2.forEach(circle -> {
                circle.setState(Leg4.getState());
            });
            gridPaneObserver.addColumn(circles2, Leg4.getState());
            GridPaneObserver.refreshProtoboard(gridPaneObserver);


        } else if ((Leg3.hasEnergy() || Leg4.hasEnergy()) && (!Leg1.hasEnergy() || !Leg2.hasEnergy()) && !isBurned) {
            Leg1.setState(Leg3.getState());
            Leg2.setState(Leg3.getState());
            ArrayList<CustomCircle> circles1 = GridPaneObserver.getCircles(gridPaneObserver, Leg1.getID());
            circles1.forEach(circle -> {
                circle.setState(Leg1.getState());
            });
            gridPaneObserver.addColumn(circles1, Leg1.getState());

            ArrayList<CustomCircle> circles2 = GridPaneObserver.getCircles(gridPaneObserver, Leg2.getID());
            circles2.forEach(circle -> {
                circle.setState(Leg2.getState());
            });
            gridPaneObserver.addColumn(circles2, Leg2.getState());
            GridPaneObserver.refreshProtoboard(gridPaneObserver);

        }

    }


    //Este metodo lo que hara es recorrer todos los caminos
    public Boolean isConnectedFromVolt(Cable LegCable){
        ArrayList<Cable> cables = Utils.getConnectedCables(gridPaneObserver.getCables(), LegCable, false);
        for (Cable cable : cables) {
            if(cable.getFirstCircle().getID().getGridName().equals(gridPaneObserver.getGridVoltPrefix()) || cable.getSecondCircle().getID().getGridName().equals(gridPaneObserver.getGridVoltPrefix())){
                return true;
            } else{
                return false;
            }
        }
        return false;
    }


    public void updateLegs(){
        //En el caso que la primera pata tenga energia y la otra no se le da energia a la que no tiene
        if (UpperLegs[0].hasEnergy() && !UpperLegs[1].hasEnergy()) {
            UpperLegs[1].setState(UpperLegs[0].getState());
            //GridPaneObserver.refreshProtoboard(gridPaneObserver);
            //Es lo mismo que el anterior pero en el caso contrario
        } else if(!UpperLegs[0].hasEnergy() && UpperLegs[1].hasEnergy()){
            UpperLegs[0].setState(UpperLegs[1].getState());
            //GridPaneObserver.refreshProtoboard(gridPaneObserver);
        }

        //hacemos lo mismo pero con las patas inferiores
        if(!LowerLegs[0].hasEnergy() && !LowerLegs[1].hasEnergy());
        else if (LowerLegs[0].hasEnergy() && !LowerLegs[1].hasEnergy()) {
            LowerLegs[1].setState(LowerLegs[0].getState());
            //GridPaneObserver.refreshProtoboard(gridPaneObserver);
        } else if (!LowerLegs[0].hasEnergy() && LowerLegs[1].hasEnergy()) {
            LowerLegs[0].setState(LowerLegs[1].getState());
            //GridPaneObserver.refreshProtoboard(gridPaneObserver);
        }

    }


    //Este metodo lo que hara sera ver si el Switch se deberia quemar o no y ademas actualizamos el estado de los circulos
    public void checkisBurned( ){
        int Upper = 0;
        int Lower = 0;
        //Preguntamos si los circulos de la parte superior del switch tienen energia
        if(UpperLegs[0].hasEnergy() && UpperLegs[1].hasEnergy()) {
            //si el estado que tienen las patas son distintos entonces se quema el componente
            if (UpperLegs[0].getState() != UpperLegs[1].getState()) {
                this.isBurned = true;
                return;
            } else {
                Upper = UpperLegs[0].getState();
            }
        }

        //hacemos lo mismo pero con las patas inferiores
        if(LowerLegs[0].hasEnergy() && LowerLegs[1].hasEnergy()){
            if(LowerLegs[0].getState() != LowerLegs[1].getState()){
                this.isBurned = true;
            } else{
                Lower = LowerLegs[0].getState();
            }
        }

        //En el caso que el paso de carga este activo y el estado de las patas inferiores y superiores son distintas se quema
        if(ChargePass && Lower != Upper){
            this.isBurned = true;
        }
    }

    public void paintLegs(){
        for(CustomCircle leg : Legs){
            ArrayList<CustomCircle> circles = GridPaneObserver.getCircles(gridPaneObserver, Cable.getCircleDiferentfromCable(leg, leg.getCable()).getID());
            circles.forEach(circle -> {
                circle.setState(leg.getState());
            });
        }
    }

    //Este metodo lo que hace es despintar las patas del Switch que no estan conectada a un cable energizado
    public void unPaintLegs(){
        for (CustomCircle leg : Legs) {
            //preguntamos si la pata tiene un cable conectado al volt
            if(!isConnectedFromVolt(leg.getCable())){
                //le quitamos la energia
                leg.removeEnergy();
                ArrayList<CustomCircle> circles = GridPaneObserver.getCircles(gridPaneObserver, leg.getCable().getSecondCircle().getID());
                circles.forEach(circle -> {circle.removeEnergy();});
            }
        }
        GridPaneObserver.refreshProtoboard(gridPaneObserver);
    }

    //Este metodo lo que es settear el estado del CustomCircle segun el otro circulo del cable que tiene asignado
    public void setEnergyfromClosestCircles(ArrayList<CustomCircle> legs){
        for (CustomCircle leg : legs) {
            leg.setState(leg.getCable().getSecondCircle().getState());
        }
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
    public ArrayList<CustomCircle> getLegs(){
        return this.Legs;
    }
    public Boolean getIsUndragableAlready(){
        return this.isUndragableAlready;
    }
}
