package ar.edu.utnfrc.backend.services;

import java.util.List;
import java.util.stream.Stream;

import ar.edu.utnfrc.backend.entities.Puesto;
import ar.edu.utnfrc.backend.repositories.PuestoRepository;
import ar.edu.utnfrc.backend.services.interfaces.IService;

public class PuestoService implements IService<Puesto, Integer> {
	
    private final PuestoRepository repository;

    public PuestoService() {
        this.repository = new PuestoRepository();
    }

    @Override
    public Puesto getById(Integer id) {
        return repository.getById(id);
    }

    @Override
    public Puesto getOrCreateByName(String name) {
        Puesto Puesto = repository.getByName(name);
        if (Puesto == null) {
            Puesto = new Puesto();
            Puesto.setNombre(name);
            repository.create(Puesto);
        }
        return Puesto;
    }

    @Override
    public List<Puesto> getAll() {
        return repository.getAllList();
    }

    public Stream<Puesto> getAllStream() {
        return repository.getAllStream();
    }
}