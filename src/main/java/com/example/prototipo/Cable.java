package com.example.prototipo;

public class Cable {
    int tipodecarga;
    Punto origenpunto;
    Punto destinopunto;

    public Cable(int tipodecarga, Punto origenpunto, Punto destinopunto){
        this.tipodecarga = tipodecarga;
        this.origenpunto = origenpunto;
        this.destinopunto = destinopunto;
    }
    //Setters
    public void SetTipodecarga(int tipodecarga){
        this.tipodecarga = tipodecarga;
    }
    public void SetOrigenpunto(Punto origenpunto){
        this.origenpunto = origenpunto;
    }
    public void SetDestinopunto(Punto destinopunto){
        this.destinopunto = destinopunto;
    }
    //Getters
    public int GetTipodecarga(){
        return tipodecarga;
    }
    public Punto GetOrigenpunto(){
        return origenpunto;
    }
    public Punto GetDestinopunto(){
        return destinopunto;
    }
}
