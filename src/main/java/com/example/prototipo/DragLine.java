package com.example.prototipo;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

public class DragLine {
    private Cable Wire1;
    private Cable Wire2;
    private CustomCircle StartPoint;
    private CustomCircle EndPoint;
    private ID[] ids;
    private AnchorPane root;
    private GridPaneTrailController firstGridPane;
    private GridPaneTrailController secondGridPane;

    public DragLine(AnchorPane root, GridPaneTrailController firstGridPane, GridPaneTrailController secondGridPane){
        this.root=root;
        this.firstGridPane=firstGridPane;
        this.secondGridPane=secondGridPane;
        this.ids=new ID[2];
        this.Wire1= new Cable(new Line());
        this.Wire2= new Cable(new Line());
    }

    public void DragginLine(){
        root.setOnMousePressed(event->{
            Node clicknode= event.getPickResult().getIntersectedNode();
            if(clicknode instanceof CustomCircle){
                //hola
            }
        });
    }

    //Setters
    public void setStartPoint(CustomCircle circle){this.StartPoint=circle;}
    public void setEndPoint(CustomCircle circle){this.EndPoint=circle;}
    public void setWire1(Cable cable){this.Wire1=cable;}
    public void setWire2(Cable cable){this.Wire2=cable;}

    //getters
    public CustomCircle getStartPoint(){return this.StartPoint;}
    public CustomCircle getEndPoint(){return this.EndPoint;}
    public Cable getWire1(){return this.Wire1;}
    public Cable getWire2(){return this.Wire2;}

}
