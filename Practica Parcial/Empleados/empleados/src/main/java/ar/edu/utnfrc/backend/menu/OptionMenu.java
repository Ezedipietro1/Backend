package ar.edu.utnfrc.backend.menu;

@FunctionalInterface
public interface OptionMenu<T> {
    void invocar(T context);
}