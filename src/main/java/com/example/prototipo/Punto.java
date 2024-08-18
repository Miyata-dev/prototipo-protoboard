package com.example.prototipo;

public class Punto {
    private int x;
    private int y;


    public Punto(int x, int y){
        this.x = x;
        this.y = y;
    }
    //Setters
    public void SetX(int x){
        this.x = x;
    }
    public void SetY(int y){
        this.y = y;
    }
    //Getters
    public int GetX(){
        return x;
    }
    public int GetY(){
        return y;
    }

}
