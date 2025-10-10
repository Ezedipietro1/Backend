package utnfc.isi.back.menu;

@FunctionalInterface
public interface OptionMenu<T> {
    void invocar(T context);
}
