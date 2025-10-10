package ar.edu.utnfrc.backend.services;

import java.util.List;
import java.util.stream.Stream;

import ar.edu.utnfrc.backend.entities.Departamento;
import ar.edu.utnfrc.backend.repositories.DepartamentoRepository;
import ar.edu.utnfrc.backend.services.interfaces.IService;

public class DepartamentoService implements IService<Departamento, Integer> {
	
    private final DepartamentoRepository repository;

    public DepartamentoService() {
        this.repository = new DepartamentoRepository();
    }

    @Override
    public Departamento getById(Integer id) {
        return repository.getById(id);
    }

    @Override
    public Departamento getOrCreateByName(String name) {
        Departamento departamento = repository.getByName(name);
        if (departamento == null) {
            departamento = new Departamento();
            departamento.setNombre(name);
            repository.create(departamento);
        }
        return departamento;
    }

    @Override
    public List<Departamento> getAll() {
        return repository.getAllList();
    }

    public Stream<Departamento> getAllStream() {
        return repository.getAllStream();
    }
}
