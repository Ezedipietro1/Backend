package ar.edu.utnfrc.backend.repositories;

/* 
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
*/

import ar.edu.utnfrc.backend.entities.Departamento;

public class DepartamentoRepository extends Repository<Departamento, Integer> {
    public DepartamentoRepository() {
        super();
    }

    @Override
    protected Class<Departamento> getEntityClass() {
        return Departamento.class;
    }

    /* 
    @Override
    public Departamento getById(Integer id) {
        return this.manager.find(Departamento.class, id);
    }

    @Override
    public Set<Departamento> getAll() {
        return this.manager.createQuery("SELECT e FROM Departamento e", Departamento.class)
                .getResultList()
                .stream().collect(Collectors.toSet());
    }

    @Override
    public Stream<Departamento> getAllStrem() {
        return this.manager.createQuery("SELECT e FROM Departamento e", Departamento.class).getResultStream();
    }
    */
}
