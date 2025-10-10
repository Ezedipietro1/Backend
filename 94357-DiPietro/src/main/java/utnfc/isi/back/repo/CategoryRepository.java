package utnfc.isi.back.repo;

/* 
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
*/

import utnfc.isi.back.domain.Category; 

public class CategoryRepository extends Repository<Category, Integer> {
    public CategoryRepository() {
        super();
    }

    @Override
    protected Class<Category> getEntityClass() {
        return Category.class;
    }

    /* 
    @Override
    public Category getById(Integer id) {
        return this.manager.find(Category.class, id);
    }

    @Override
    public Set<Category> getAll() {
        return this.manager.createQuery("SELECT e FROM Category e", Category.class)
                .getResultList()
                .stream().collect(Collectors.toSet());
    }

    @Override
    public Stream<Category> getAllStrem() {
        return this.manager.createQuery("SELECT e FROM Category e", Category.class).getResultStream();
    }
    */
}