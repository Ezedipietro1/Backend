package utnfc.isi.back.services;

import java.util.List;
import java.util.stream.Stream;

import utnfc.isi.back.domain.Designer;
import utnfc.isi.back.repo.DesignerRepository;
import utnfc.isi.back.services.interfaces.IService;

public class DesignerService implements IService<Designer, Integer> {
	
    private final DesignerRepository repository;

    public DesignerService() {
        this.repository = new DesignerRepository();
    }

    @Override
    public Designer getById(Integer id) {
        return repository.getById(id);
    }

    @Override
    public Designer getOrCreateByName(String name) {
        Designer designer = repository.getByName(name);
        if (designer == null) {
            designer = new Designer();
            designer.setNombre(name);
            repository.create(designer);
        }
        return designer;
    }

    @Override
    public List<Designer> getAll() {
        return repository.getAllList();
    }

    public Stream<Designer> getAllStream() {
        return repository.getAllStream();
    }
}