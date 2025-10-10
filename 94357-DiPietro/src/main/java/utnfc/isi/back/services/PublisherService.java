package utnfc.isi.back.services;

import java.util.List;
import java.util.stream.Stream;

import utnfc.isi.back.domain.Publisher;
import utnfc.isi.back.repo.PublisherRepository;
import utnfc.isi.back.services.interfaces.IService;

public class PublisherService implements IService<Publisher, Integer> {
	
    private final PublisherRepository repository;

    public PublisherService() {
        this.repository = new PublisherRepository();
    }

    @Override
    public Publisher getById(Integer id) {
        return repository.getById(id);
    }

    @Override
    public Publisher getOrCreateByName(String name) {
        Publisher publisher = repository.getByName(name);
        if (publisher == null) {
            publisher = new Publisher();
            publisher.setNombre(name);
            repository.create(publisher);
        }
        return publisher;
    }

    @Override
    public List<Publisher> getAll() {
        return repository.getAllList();
    }

    public Stream<Publisher> getAllStream() {
        return repository.getAllStream();
    }
}