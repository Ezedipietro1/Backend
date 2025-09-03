package ar.utn.frc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArraySimple<E> implements Iterable<E> {

    private ArrayList<E> items;

    public ArraySimple() {
        this(10);
    }

    public ArraySimple(int size) {
        this.items = new ArrayList<>();
    }

    public int size() {
        return this.items.size();
    }

    public Object get(int pos) throws NoSuchElementException {
        if (pos >= this.items.size())
            throw new NoSuchElementException("get: No se puede retornar un elemento que no existe");
        return this.items.get(pos);
    }

    public void set(E item, int posicion) throws IllegalAccessException {
        if (posicion >= this.items.size())
            throw new IllegalAccessException(
                    "set: No puedo insertar una valor en una poscion inexistente del conjunto");

        this.items.set(posicion, item);
    }

    public void append(E item) {
        this.items.add(item);
    }

    public E remove(int pos) throws NoSuchElementException {
        return this.items.remove(pos);
    }

    @Override
    public Iterator<E> iterator() {
        return this.items.iterator();
    }

    // Clase que me da la herramienta para recorrer items
    // private class IteradorArray<E> implements Iterator<E> {
    // private int size;
    // private int current;

    // public IteradorArray() {
    // size = items.length;
    // current = 0;
    // }

    // @Override
    // public boolean hasNext() {
    // return current < size;
    // }

    // @Override
    // public E next() {
    // E proximo = (E) items[current];
    // current++;
    // return proximo;
    // }
}