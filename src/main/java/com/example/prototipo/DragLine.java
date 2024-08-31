package com.example.prototipo;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

import java.util.ArrayList;

public class DragLine {
    private Cable Wire1;
    private CustomCircle StartPoint;
    private CustomCircle EndPoint;
    private ID[] ids;
    private AnchorPane root;
    private GridPaneTrailController firstGridPane;
    private GridPaneTrailController secondGridPane;
    private ArrayList<Cable> Wires= new ArrayList<>();

    public DragLine(AnchorPane root, GridPaneTrailController firstGridPane, GridPaneTrailController secondGridPane){
        this.root=root;
        this.firstGridPane=firstGridPane;
        this.secondGridPane=secondGridPane;
        this.ids=new ID[2];
        this.Wire1= new Cable(new Line());
    }

    public void DragginLine(){
        root.addEventHandler(MouseEvent.MOUSE_PRESSED, event-> {


        //root.setOnMousePressed(event->{
            Node clicknode= event.getPickResult().getIntersectedNode();
            if(clicknode instanceof CustomCircle){
                CustomCircle custom = (CustomCircle) clicknode;
                setStartPoint(custom);
                Wire1.setTipodecarga(custom.getState());
                ids[0]= new ID(custom.getId());

            }
        });
        root.setOnMouseDragged(event->{
            if(event.getTarget() instanceof CustomCircle){
                DrageLine(event.getX(), event.getY(),event);
            }
        });
        root.setOnMouseReleased(event->{
            finish(event);
        });
    }

    private void finish(MouseEvent event){
        if((!(event.getTarget() instanceof CustomCircle)) || (getEndPoint()==null)) {
            return;
        }
        Wire1.setIds(ids);
        Cable current= new Cable (Wire1.getLine());
        current.setTipodecarga(Wire1.getTipodecarga());
        Wires.add(current);

    }

    private void DrageLine(double x, double y, MouseEvent event){
        if(Wire1 != null){
            if(event.getPickResult().getIntersectedNode() instanceof CustomCircle){
                CustomCircle custom = (CustomCircle) event.getPickResult().getIntersectedNode();
                ids[1]= new ID(custom.getId());
                setEndPoint(custom);
                if(!(ID.isSameID(ids[0], ids[1]))){
                    Wire1.getLine().setEndX(x);
                    Wire1.getLine().setEndY(y);
                }
            }
        }
    }

    //Cuando la linea esta siendo presionada.
    private void LinePressed(AnchorPane root, MouseEvent event){
        if(event.getPickResult().getIntersectedNode() instanceof CustomCircle){
            Wire1.setLine(new Line(event.getSceneX(), event.getSceneY(), event.getX(), event.getY()));//Posiblemente aca este el error
            Wire1.getLine().setStrokeWidth(5);
            root.getChildren().add(Wire1.getLine());
        }

    }


    //Setters
    public void setStartPoint(CustomCircle circle){this.StartPoint=circle;}
    public void setEndPoint(CustomCircle circle){this.EndPoint=circle;}
    public void setWire1(Cable cable){this.Wire1=cable;}

    //getters
    public CustomCircle getStartPoint(){return this.StartPoint;}
    public CustomCircle getEndPoint(){return this.EndPoint;}
    public Cable getWire1(){return this.Wire1;}

}
