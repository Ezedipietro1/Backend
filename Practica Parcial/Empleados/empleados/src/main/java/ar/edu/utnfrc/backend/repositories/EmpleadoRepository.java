package ar.edu.utnfrc.backend.repositories;

/* 
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
*/

import ar.edu.utnfrc.backend.entities.Empleado;

public class EmpleadoRepository extends Repository<Empleado, Integer> {
    public EmpleadoRepository() {
        super();
    }

    @Override
    protected Class<Empleado> getEntityClass() {
        return Empleado.class;
    }

    /* 
    @Override
    public Empleado getById(Integer id) {
        return this.manager.find(Empleado.class, id);
    }

    @Override
    public Set<Empleado> getAll() {
        return this.manager.createQuery("SELECT e FROM empleado e", Empleado.class)
                .getResultList()
                .stream().collect(Collectors.toSet());
    }

    @Override
    public Stream<Empleado> getAllStrem() {
        return this.manager.createQuery("SELECT e FROM empleado e", Empleado.class).getResultStream();
    }

    @Override
    public Empleado getByName(String name) {
        return this.manager.createQuery("SELECT e FROM empleado e WHERE e.nombre LIKE :name", Empleado.class)
                .setParameter("name", name)
                .getSingleResult();
    }
    */
}