package utnfc.isi.back.repo;

/* 
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
*/

import utnfc.isi.back.domain.Publisher; 

public class PublisherRepository extends Repository<Publisher, Integer> {
    public PublisherRepository() {
        super();
    }

    @Override
    protected Class<Publisher> getEntityClass() {
        return Publisher.class;
    }

    /* 
    @Override
    public Publisher getById(Integer id) {
        return this.manager.find(Publisher.class, id);
    }

    @Override
    public Set<Publisher> getAll() {
        return this.manager.createQuery("SELECT e FROM Publisher e", Publisher.class)
                .getResultList()
                .stream().collect(Collectors.toSet());
    }

    @Override
    public Stream<Publisher> getAllStrem() {
        return this.manager.createQuery("SELECT e FROM Publisher e", Publisher.class).getResultStream();
    }
    */
}