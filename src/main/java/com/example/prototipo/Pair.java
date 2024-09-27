package com.example.prototipo;

//
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Pair<?,?>)) return false;

        Pair<T,G> other = (Pair<T,G>) obj;

        T firstValue = getFirstValue();
        G secondValue = getSecondValue();

        boolean isFirstValueEquals = firstValue.equals(other.getFirstValue());
        boolean isSecondValueEquals = secondValue.equals(other.getSecondValue());

        return isFirstValueEquals && isSecondValueEquals;
    }

    @Override
    public String toString() {
        return "firstValue: " + getFirstValue() + ", secondValue: " + getSecondValue();
    }
}