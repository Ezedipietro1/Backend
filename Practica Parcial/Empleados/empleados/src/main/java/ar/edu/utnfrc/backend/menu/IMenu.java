package ar.edu.utnfrc.backend.menu;

public interface IMenu<T> {
    void addOption(int opcion, ItemMenu<T> action);
    void runMenu(T context);
}