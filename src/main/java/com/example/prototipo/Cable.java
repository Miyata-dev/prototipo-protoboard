package com.example.prototipo;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Arrays;

public class Cable extends Line {
    private int tipodecarga;
    private ID[] ids; //Contiene las id que tienen los CustomCircle a los que se conectan
    private String randomID; //esta id se da valor con un setter, no en el constructor.
    private CustomCircle[] circles;
    private String tipo; //null == Normal, resistencia == resistencia
    private boolean isBurned = false;

    public Cable() {
        this.tipodecarga = 0;
    }

    public Cable(double startX, double startY, double endX, double endY) {
        setStartX(startX);
        setStartY(startY);
        setEndX(endX);
        setEndY(endY);
    }

    public Cable(CustomCircle firstCircle, CustomCircle secondCircle){
        this.SetCircles(new CustomCircle[]{
                firstCircle,
                secondCircle
        });
        setStartX(firstCircle.getX());
        setEndX(secondCircle.getX());
        setStartY(firstCircle.getY());
        setEndY(secondCircle.getY());
        setRandomID();
        this.setIds(new ID[] {
                firstCircle.getID(),
                secondCircle.getID()
        });
    }

    //Metodos...

    //Este metodo lo que hace es conseguir el circulo que es diferente
    public static CustomCircle getCircleDiferentfromCable(CustomCircle circle, Cable cable){
        if(circle.getID().equals(cable.getFirstCircle().getID())){
            return cable.getSecondCircle();
        } else{
            return cable.getFirstCircle();
        }
    }

    public static boolean compareCables(Cable c1, Cable c2) {
        return ID.isSameID(c1.ids[0], c2.ids[0]) && ID.isSameID(c1.ids[1], c2.ids[1]);
    }


    //retorna null si no encuentra un cable.
    public static Cable getCableFromCollection(ArrayList<Cable> cables, Cable cableToFind) {
        for (Cable c : cables) {
            if (Cable.compareCables(c, cableToFind)) {
                return c;
            }
        }
        return null;
    }

    //Este metodo lo que hace es remover la carga de cable
    public void removeTipodecarga() {
        this.tipodecarga = 0;
    }

    public static boolean areConnected(Cable one, Cable two) {
        //en estás condiciones (la primera y la segunda) se mira que el indice de los cículos de los cables se conecten entre si.
        boolean firstAtt =
                one.getIds()[0].getIndexColumn() == two.getIds()[1].getIndexColumn() ||
                        one.getIds()[0].getIndexColumn() == two.getIds()[0].getIndexColumn();

        boolean secondAtt =
                one.getIds()[1].getIndexColumn() == two.getIds()[0].getIndexColumn() ||
                        one.getIds()[1].getIndexColumn() == two.getIds()[1].getIndexColumn();
        //
        boolean firstGridNameNameID =
                one.getIds()[0].getGridName().equals(two.getIds()[1].getGridName()) ||
                        one.getIds()[0].getGridName().equals(two.getIds()[0].getGridName());

        boolean secondGridNameID =
                one.getIds()[1].getGridName().equals(two.getIds()[0].getGridName()) ||
                        one.getIds()[1].getGridName().equals(two.getIds()[1].getGridName());

        return (firstAtt || secondAtt) && (firstGridNameNameID || secondGridNameID);
    }


    public boolean isConnectedToBatery() {
        String bateryID = "BateryVolt";
        //mira si una de las ids relacionadas a los circulos que conecta el cable pertenece a la bateria.
        boolean isFirstConnected = ids[0].getGridName().equals(bateryID);
        boolean isSecondConnected = ids[1].getGridName().equals(bateryID);

        return isFirstConnected || isSecondConnected;
    }

    public boolean isConnectedToVolts() {
        String[] voltNames = {
                "gridVolt1",
                "gridVolt2"
        };
        //ids[0].getGridName()
        boolean isFirstConnected = Arrays.asList(voltNames).contains(ids[0].getGridName());
        boolean isSecondConnected = Arrays.asList(voltNames).contains(ids[1].getGridName());

        return isFirstConnected || isSecondConnected;
    }

    //Setters
    public void setTipodecarga(int tipodecarga) {
        //si el tipo de energia es neutra (0), no se asigna al cable.
        if (tipodecarga == 0) return;
        this.tipodecarga = tipodecarga;
    }


    public void setIds(ID[] ids) {
        this.ids = ids;
    }

    public void setRandomID(String randomID) {
        this.randomID = randomID;
    }

    public void setRandomID() {
        this.randomID = Utils.createRandomID();
    }

    public void setBurned() {
        System.out.println("burning...");
        this.isBurned = true;
        this.setStroke(Color.BROWN);
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setLine(double startX, double startY, double endX, double endY) {
        setStartX(startX);
        setStartY(startY);
        setEndX(endX);
        setEndY(endY);
    }

    public void SetCircles(CustomCircle[] circles){
        this.circles = circles;
    }

    //Getters..

    public String getRandomID() {
        return randomID;
    }

    public ID[] getIds() {
        return ids;
    }

    public int getTipodecarga() {
        return tipodecarga;
    }

    public CustomCircle getFirstCircle(){
        return circles[0];
    }

    public CustomCircle getSecondCircle(){
        return circles[1];
    }

    public CustomCircle[] Getcircles(){
        return this.circles;
    }

    public String getTipo() {
        return tipo;
    }

    //calcula el largo de la linea
    public double getLineWidth() {
        double deltaX = this.getEndX() - this.getStartX();
        double deltaY = this.getEndY() - this.getStartY();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    public Boolean getisBurned(){
        return this.isBurned;
    }

    @Override
    public String toString() {
        return "id: " + getRandomID() + " carga: " + tipodecarga;
    }

    //TODO revisar.
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Cable)) return false;

        Cable cable = (Cable) obj;

        return this.randomID.equals(cable.getRandomID());
    }
}