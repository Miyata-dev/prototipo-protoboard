package com.example.prototipo;

public class LED {
    boolean estado;
    Punto ubicacion1;
    Punto ubicacion2;

    public LED(boolean estado, Punto ubicacion1, Punto ubicacion2) {
        this.estado = estado;
        this.ubicacion1 = ubicacion1;
        this.ubicacion2 = ubicacion2;
    }
    //Setters
    public void setEstado(boolean estado) {
        this.estado = estado;
    }
    public void setUbicacion1(Punto ubicacion1) {
        this.ubicacion1 = ubicacion1;
    }
    public void setUbicacion2(Punto ubicacion2) {
        this.ubicacion2 = ubicacion2;
    }
    //Getters
    public boolean getEstado() {
        return estado;
    }
    public Punto getUbicacion1() {
        return ubicacion1;
    }
    public Punto getUbicacion2() {
        return ubicacion2;
    }

}
