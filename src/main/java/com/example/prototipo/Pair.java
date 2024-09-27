package com.example.prototipo;

public class Pair<T, G> { //guarda 2 elementos genéricos, permite duplicados.
    private T firsValue;
    private G secondValue;

    public Pair(T firstValue, G secondValue) {
        this.firsValue = firstValue;
        this.secondValue = secondValue;
    }

    public void setFirstValue(T firsValue) {
        this.firsValue = firsValue;
    }

    public void setSecondValue(G secondValue) {
        this.secondValue = secondValue;
    }

    public T getFirstValue() {
        return firsValue;
    }

    public G getSecondValue() {
        return secondValue;
    }
}