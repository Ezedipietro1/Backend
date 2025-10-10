package utnfc.isi.back.repo;

/* 
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
*/

import utnfc.isi.back.domain.Designer; 

public class DesignerRepository extends Repository<Designer, Integer> {
    public DesignerRepository() {
        super();
    }

    @Override
    protected Class<Designer> getEntityClass() {
        return Designer.class;
    }

    /* 
    @Override
    public Designer getById(Integer id) {
        return this.manager.find(Designer.class, id);
    }

    @Override
    public Set<Designer> getAll() {
        return this.manager.createQuery("SELECT e FROM Designer e", Designer.class)
                .getResultList()
                .stream().collect(Collectors.toSet());
    }

    @Override
    public Stream<Designer> getAllStrem() {
        return this.manager.createQuery("SELECT e FROM Designer e", Designer.class).getResultStream();
    }
    */
}