package com.frc.isi.museo.repositorio;
import java.util.stream.Stream;
import java.util.Set;
import com.frc.isi.museo.repositorio.contexto.MuseoDBContext;
import jakarta.persistence.EntityManager;

public abstract class Repositorio<K, T> {
    protected EntityManager manager;

    public Repositorio() {
        manager = MuseoDBContext.getInstance().getManager();
    }

    public void add(T entity) {
        var transaccion = manager.getTransaction();
        transaccion.begin();
        manager.persist(entity);
        transaccion.commit();
    }

    public void update(T entity) {
        var transaccion = manager.getTransaction();
        transaccion.begin();
        manager.merge(entity);
        transaccion.commit();
    }

    public T delete(K id) {
        var transaccion = manager.getTransaction();
        transaccion.begin();
        var entity = this.getById(id);
        manager.remove(entity);
        transaccion.commit();
        return entity;
    }

    public abstract T getById(K id);

    public abstract Set<T> getAll();

    public abstract Stream<T> getAllStream();
}
