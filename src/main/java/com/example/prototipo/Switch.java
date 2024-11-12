package com.example.prototipo;


import javafx.scene.Group;
import javafx.scene.image.Image;
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
    private ArrayList<Cable> cables = new ArrayList<>();

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
    private Boolean isFirstTime = false;

    private ArrayList<Pair<Integer, ArrayList<CustomCircle>>> energizedColumns = new ArrayList<>();
    private Pair<Integer, CustomCircle> origin = null;//FirstValue == 1 -> arriba | == -1 -> abajo
    private ArrayList<CustomCircle> coOrigin = null;
    private CustomCircle coOriginCircle = null;

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
        ID idOne = new ID(100, 100, "switchvolt1");
        ID idTwo = new ID(200, 200, "switchvolt1");
        ID idThree = new ID(300, 300, "switchvolt1");
        ID idFour = new ID(400, 400, "switchvolt1");

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
                //se le va la energia alas columnas energizadas al eliminar el switch.
                energizedColumns.forEach(pair -> {
                    ArrayList<CustomCircle> col = pair.getSecondValue();
                    col.forEach(CustomCircle::removeEnergy);
                });
                energizedColumns.clear();
                gridPaneObserver.removeSwitches(this);
            } else {
                if (isUndragableAlready && !isBurned) {
                    this.hasmoved = true;
                    //Cuando el basurero no este activo y ya no se puede mover y se de un click se actualiza el estado
                    setChargePass(!this.ChargePass);
                    if (gridPaneObserver.getIsEnergyActivated()) {
                        Function();


                        //Ya que al momento de refrescar no se puede agregar cables a la colección, debemos mover el agregado de de cables fuera de la función del Switch

                        //Para eso preguntamos el estado de la carga del Switch y si el origen es distinto de nulo, si es asi, podremos
                        if (this.ChargePass && this.origin != null) {
                            //Preguntamos por el origen, y añadimos la pata de la coleccion
                            if (origin.getFirstValue() == 1) {//Arriba
                                for (CustomCircle circle : LowerLegs) {
                                    gridPaneObserver.addCable(circle.getCable());
                                }
                            } else if (origin.getFirstValue() == -1) {// abajo
                                for (CustomCircle circle : UpperLegs) {
                                    gridPaneObserver.addCable(circle.getCable());
                                }
                            }
                        } else if (!this.ChargePass && this.origin != null) {
                            Legs.forEach(leg -> {
                                if (!leg.equals(origin.getSecondValue()) && !leg.equals(this.coOriginCircle)) {
                                    leg.removeEnergy();
                                    gridPaneObserver.removeCable(leg.getCable());
                                }
                            });
                        }

                        for (Cable cable : new ArrayList<>(gridPaneObserver.getCables())) {
                            GridPaneObserver.refreshProtoboard(gridPaneObserver);
                        }

                    }
                }
            }
        });
        this.setOnMouseReleased(e -> {
            closestCircles.clear();
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
                } else {
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
                        this.cables.add(cable);
                        gridPaneObserver.addCable(cable);
                        System.out.println("cable: " + cable.getFirstCircle().getID() + " || " + cable.getSecondCircle().getID());
                        i.getAndIncrement();

                    });
                    System.out.println("-------------------------------------------");
                    Utils.makeUndraggableNode(this);
                    this.isUndragableAlready = true;
                    updateLegs();
                }

            }
        });
    }


    //Este metodo lo que hara es la funcionalidad del Switch
    public void Function() {

        System.out.println("ChargePass is: " + ChargePass);
        setEnergyfromClosestCircles(Legs);
        originHaveNoEnergy();
        if (!ChargePass) {
            unPaintLegs();
            updateLegs();
            originHaveNoEnergy();
            checkisBurned();
            return;
        }
        //Actualizamos la energia desde los circulos
        setEnergyfromClosestCircles(Legs);
        originHaveNoEnergy();
        checkisBurned();
        if (isBurned && this.coOriginCircle != null) {
            if(this.origin == null) return;
            unPaintLegs();
            coOrigin.forEach(CustomCircle::removeEnergy);
            Pair pair = new Pair<>(coOrigin.get(0).getState(), coOrigin);
            gridPaneObserver.removeColumn(coOrigin);
            energizedColumns.clear();
            this.coOriginCircle = null;
            this.coOrigin = null;
        } else {
            originHaveNoEnergy();
            paintLegs();
            originHaveNoEnergy();
        }
    }

    //Este metodo lo que hace es actualizar el estado de las patas que tiene el Switch e identificar el origen
    public void updateLegs(){
        if(isBurned) return;
        //En el caso que la primera pata tenga energia y la otra no se le da energia a la que no tiene
        if (UpperLegs[0].hasEnergy() && !UpperLegs[1].hasEnergy()) {
            UpperLegs[1].setState(UpperLegs[0].getState());

            setOrigin(1, UpperLegs[0]);
            setCoOriginLeg(UpperLegs[1]);
            ArrayList<CustomCircle> circles = GridPaneObserver.getCircles(gridPaneObserver, UpperLegs[1].getCable().getSecondCircle().getID());
            circles.forEach(cir -> cir.setState(UpperLegs[0].getState()));

            //Añadimos la columna a la coleccion de columnas energizadas
            addEnergizedColumn(circles);
            setCoOrigin(circles);

            //Es lo mismo que el anterior pero en el caso contrario
        } else if(!UpperLegs[0].hasEnergy() && UpperLegs[1].hasEnergy()){
            UpperLegs[0].setState(UpperLegs[1].getState());
            setOrigin(1, UpperLegs[1]);
            setCoOriginLeg(UpperLegs[0]);
            ArrayList<CustomCircle> circles = GridPaneObserver.getCircles(gridPaneObserver, UpperLegs[0].getCable().getSecondCircle().getID());
            circles.forEach(cir -> cir.setState(UpperLegs[1].getState()));


            //Añadimos la columna a la coleccion de columnas energizadas
            addEnergizedColumn(circles);
            setCoOrigin(circles);
        }

        //hacemos lo mismo pero con las patas inferiores
        if (LowerLegs[0].hasEnergy() && !LowerLegs[1].hasEnergy()) {
            LowerLegs[1].setState(LowerLegs[0].getState());
            setOrigin(-1, LowerLegs[0]);
            setCoOriginLeg(LowerLegs[1]);

            ArrayList<CustomCircle> circles = GridPaneObserver.getCircles(gridPaneObserver, LowerLegs[1].getCable().getSecondCircle().getID());
            circles.forEach(cir -> cir.setState(LowerLegs[0].getState()));


            //Añadimos la columna a la coleccion de columnas energizadas
            addEnergizedColumn(circles);
            setCoOrigin(circles);
        } else if (!LowerLegs[0].hasEnergy() && LowerLegs[1].hasEnergy()) {
            LowerLegs[0].setState(LowerLegs[1].getState());
            setOrigin(-1, LowerLegs[1]);
            setCoOriginLeg(LowerLegs[0]);
            ArrayList<CustomCircle> circles = GridPaneObserver.getCircles(gridPaneObserver, LowerLegs[0].getCable().getSecondCircle().getID());
            circles.forEach(cir -> cir.setState(LowerLegs[1].getState()));

            //Añadimos la columna a la coleccion de columnas energizadas
            addEnergizedColumn(circles);
            setCoOrigin(circles);
        }

    }



    //Este metodo lo que hara sera ver si el Switch se deberia quemar o no y ademas actualizamos el estado de los circulos
    public void checkisBurned( ){
        if(this.isBurned) return;
        //Preguntamos si los circulos de la parte superior del switch tienen energia
        if(UpperLegs[0].hasEnergy() && UpperLegs[1].hasEnergy()) {
            //si el estado que tienen las patas son distintos entonces se quema el componente
            if (UpperLegs[0].getState() != UpperLegs[1].getState()) {
                this.isBurned = true;
            }
        }

        //hacemos lo mismo pero con las patas inferiores
        if(LowerLegs[0].hasEnergy() && LowerLegs[1].hasEnergy()){
            if(LowerLegs[0].getState() != LowerLegs[1].getState()){
                this.isBurned = true;
            }
        }

        //En este caso lo que preguntamos es que si el paso de carga esta activado y las patas superiores tienen energia junto a las inferiores y ademas tambien tienen los mismos tipos de carga y tambien preguntamos si el estado de la pata superior es distinto al de las inferiiores
        if (!this.isBurned && ChargePass && (UpperLegs[0].hasEnergy() && UpperLegs[1].hasEnergy()) && LowerLegs[0].hasEnergy() && LowerLegs[1].hasEnergy() && UpperLegs[1].getState() == UpperLegs[0].getState() && LowerLegs[1].getState() == LowerLegs[0].getState() && LowerLegs[0].getState() != UpperLegs[0].getState()) {
            this.isBurned = true;

        }
        if(isBurned){
            unPaintLegs();
            coOrigin.forEach(circle->{
                circle.removeEnergy();
            });
            Pair pair = new Pair<>(coOrigin.get(0).getState(), coOrigin);
            gridPaneObserver.removeColumn(coOrigin);
            energizedColumns.clear();
        }
    }

    //Este Metodo lo que hace es pintar las patas del Switch
    public void paintLegs(){
        //se asegura de que tenga un origen.
        if (origin == null) return;

        //Se les setean el estado de la energia a la que estan conectada
        setEnergyfromClosestCircles(Legs);

        //si el origen del Switch es 1, se ocupara UpperLegs, lo que lleva a pintar la parte inferior
        if (origin.getFirstValue() == 1) {//Arriba
            for (CustomCircle circle : LowerLegs) {
                ArrayList<CustomCircle> col = GridPaneObserver.getCircles(gridPaneObserver, circle.getCable().getSecondCircle().getID());
                addEnergizedColumn(col);
            }
        } else if (origin.getFirstValue() == -1) {// abajo
            for (CustomCircle circle : UpperLegs) {
                ArrayList<CustomCircle> col = GridPaneObserver.getCircles(gridPaneObserver, circle.getCable().getSecondCircle().getID());
                addEnergizedColumn(col);
            }
        }
        //después de obtener las columnas energizadas se pintan.
        energizedColumns.forEach(pair -> {
            Integer energy = pair.getFirstValue();
            ArrayList<CustomCircle> col = pair.getSecondValue();
            col.forEach(cir -> cir.setState(energy));
        });
    }


    //Este metodo lo que hace es despintar las patas del Switch que no estan conectada a un cable energizado
    public void unPaintLegs(){
        if(this.origin == null) return;

        ArrayList<ArrayList<CustomCircle>> colsCopies = new ArrayList<>();
        energizedColumns.forEach(pair -> {
            if(!pair.getSecondValue().equals(this.coOrigin) && !pair.getSecondValue().equals(GridPaneObserver.getCircles(gridPaneObserver, origin.getSecondValue().getCable().getSecondCircle().getID()))){
                ArrayList<CustomCircle> col = pair.getSecondValue();
                col.forEach(CustomCircle::removeEnergy);
                colsCopies.add(col);
            }
        });
        //Recorremos cada pata del Switch que no sean del origen y del co origen

        if(this.origin.getFirstValue() == 1){ //-> arriba
            removeEnergyzedConnected(LowerLegs[0].getCable());
            removeEnergyzedConnected(LowerLegs[1].getCable());
        } else if(this.origin.getFirstValue() == -1) { // ->abajo
            removeEnergyzedConnected(UpperLegs[0].getCable());
            removeEnergyzedConnected(UpperLegs[1].getCable());
        }

        removeEnergizedColumn(colsCopies);

    }

    //Este metodo lo que es remover todas las columnas energizadas que estan conectadas a la pata del switch correspondiente.
    public void removeEnergyzedConnected(Cable cable){
        //Conseguimos los cables conectados a remover
        ArrayList<Cable> cabletoRemove = Utils.getConnectedCables(gridPaneObserver.getCables(), cable, gridPaneObserver, true);
        if(cabletoRemove.isEmpty()){//Si la coleccion de cables esta vacia entonces le quitamos energia a la columan del cable dado
            ArrayList<CustomCircle> circleCable = GridPaneObserver.getCircles(gridPaneObserver, cable.getSecondCircle().getID());
            circleCable.forEach(CustomCircle::removeEnergy);
            gridPaneObserver.removeColumn(circleCable);
            return;
        }
        cabletoRemove.add(cable);
        System.out.println("-------------------------- cable");
        System.out.println("tipo de carga: " + cable.getTipodecarga() + " first id: " + cable.getFirstCircle().getID() + " second id: " + cable.getSecondCircle().getID());
        System.out.println("-------------------------- cabletoremove");
        for (Cable cable1 : cabletoRemove) {//Vamos por cada cable de la coleccion

            if(cable1.getFirstCircle().getState() == cable1.getSecondCircle().getState() && cable1.getFirstCircle().getState() == origin.getSecondValue().getState() && cable1.getSecondCircle().getState() == origin.getSecondValue().getState()) {
                System.out.println("tipo de carga: " + cable1.getTipodecarga() + " first id: " + cable1.getFirstCircle().getID() + " second id: " + cable1.getSecondCircle().getID());
                //Obtenemos las columnas correspondientes y les quitamos la energia
                ArrayList<CustomCircle> firstcol = GridPaneObserver.getCircles(gridPaneObserver, cable1.getFirstCircle().getID());
                ArrayList<CustomCircle> secondcol = GridPaneObserver.getCircles(gridPaneObserver, cable1.getSecondCircle().getID());
                firstcol.forEach(CustomCircle::removeEnergy);
                secondcol.forEach(CustomCircle::removeEnergy);
                //para asi quitarlas de la coleccion de columnas energizadas del gridpaneObserver
                gridPaneObserver.removeColumn(firstcol);
                gridPaneObserver.removeColumn(secondcol);
            }
        }
        System.out.println("--------------------------");
    }

    //Este metodo lo que es settear el estado del CustomCircle segun el otro circulo del cable que tiene asignado
    public void setEnergyfromClosestCircles(ArrayList<CustomCircle> legs){
        for (CustomCircle leg : legs) {
            if(!leg.hasCable()) return;
            leg.setState(leg.getCable().getSecondCircle().getState());
        }
    }

    //Este metodo lo que hace es devolver los cables que deberian estar conectados dependiendo del tipo de carga y el origen
    public ArrayList<Cable> getConnectedCablesSwitch(){
        //En el caso de que el origen es null entonces se retorna nulo
        if(this.origin == null || isBurned || !isUndragableAlready) return null;

        if(this.ChargePass){
            return this.getCables();
        } else{
            if(this.origin.getFirstValue() == 1){//Arriba, entonces tenemos que añadir las de abajo
                ArrayList<Cable> LowerCable = new ArrayList<>();
                LowerCable.add(LowerLegs[0].getCable());
                LowerCable.add(LowerLegs[1].getCable());
                //Tenemos que agregar a los cables conectados el cable del co-origen
                if(UpperLegs[1].equals(this.origin.getSecondValue())){
                    LowerCable.add(UpperLegs[0].getCable());
                } else {
                    LowerCable.add(UpperLegs[1].getCable());
                }
                //Ahora añadimos los cables a la coleccion que esta en gridPaneObserver
                return LowerCable;
            } else if(this.origin.getFirstValue() == -1){//Abajo
                ArrayList<Cable> UpperCable = new ArrayList<>();
                UpperCable.add(UpperLegs[0].getCable());
                UpperCable.add(UpperLegs[1].getCable());
                if(LowerLegs[1].equals(origin.getSecondValue())){
                    UpperCable.add(LowerLegs[0].getCable());
                } else {
                    UpperCable.add(LowerLegs[1].getCable());
                }

                return UpperCable;
            } else {
                return null;
            }
        }
    }

    //Este metodo lo que hace es agregar la columna a las diferentes colecciones del gridPaneObserver
    public void addEnergizedColumn(ArrayList<CustomCircle> col) {
        Pair pair = new Pair<>(origin.getSecondValue().getState(), col);

        //En el caso de que la columna dada ya estaba en la coleccion de columnas energizadas se sale del metodo y no se agrega
        if(energizedColumns.contains(pair)) return;
        energizedColumns.add(pair);
        gridPaneObserver.addColumn(col, origin.getSecondValue().getState());
    }


    //Este metodo lo que hace es remover las columnas energizadas que estan dentro de una coleccion
    public void removeEnergizedColumn(ArrayList<ArrayList<CustomCircle>> cols) {
        cols.forEach(col->{
            Pair pair = new Pair<>(origin.getSecondValue().getState(), col);
            energizedColumns.remove(pair);
            gridPaneObserver.removeColumn(col);
        });
    }

    public void clearcoords(ArrayList<CustomCircle> circles ){
        for (CustomCircle circle : circles) {
            circle.setCoords(0,0);
        }
    }

    public void originHaveNoEnergy(){
        if(this.origin == null) return;

        ArrayList<CustomCircle> originCol = GridPaneObserver.getCircles(gridPaneObserver, origin.getSecondValue().getCable().getSecondCircle().getID());

        if(!originCol.get(0).hasEnergy()){
            for (CustomCircle leg : this.Legs) {
                removeEnergyzedConnected(leg.getCable());
                leg.removeEnergy();
            }
            //Reiniciamos el estado de todos los
            this.origin = null;
            this.coOrigin = null;
            this.coOriginCircle = null;
        }
    }

    //Setters...


    private void setCoOriginLeg(CustomCircle leg) {
        if(coOriginCircle != null) return;
        this.coOriginCircle = leg;
    }

    public void setCoOrigin(ArrayList<CustomCircle> circles){
        if(this.coOrigin != null ) return;
        this.coOrigin = circles;
    }

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
        if(this.ChargePass){
            Image image = new Image(getClass().getResource("Switch.png").toExternalForm());
            this.Shape.setImage(image);
        } else{
            Image image = new Image(getClass().getResource("Switch_OFF.png").toExternalForm());
            this.Shape.setImage(image);
        }
    }
    //solo se setea una vez.
    public void setOrigin(Integer origin, CustomCircle originCircle) {
        if (this.origin != null) return;
        this.origin = new Pair<>(origin, originCircle);
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

    public ArrayList<Cable> getCables(){return this.cables;}
}
