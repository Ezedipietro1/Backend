package utnfc.isi.back.services;

import java.util.List;
import java.util.stream.Stream;

import utnfc.isi.back.domain.Category;
import utnfc.isi.back.repo.CategoryRepository;
import utnfc.isi.back.services.interfaces.IService;

public class CategoryService implements IService<Category, Integer> {
	
    private final CategoryRepository repository;

    public CategoryService() {
        this.repository = new CategoryRepository();
    }

    @Override
    public Category getById(Integer id) {
        return repository.getById(id);
    }

    @Override
    public Category getOrCreateByName(String name) {
        Category category = repository.getByName(name);
        if (category == null) {
            category = new Category();
            category.setNombre(name);
            repository.create(category);
        }
        return category;
    }

    @Override
    public List<Category> getAll() {
        return repository.getAllList();
    }

    public Stream<Category> getAllStream() {
        return repository.getAllStream();
    }
}
