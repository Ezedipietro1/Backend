package utnfc.isi.back.repo;

/* 
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
*/

import utnfc.isi.back.domain.BoardGame; 

public class BoardGameRepository extends Repository<BoardGame, Integer> {
    public BoardGameRepository() {
        super();
    }

    @Override
    protected Class<BoardGame> getEntityClass() {
        return BoardGame.class;
    }

    /* 
    @Override
    public BoardGame getById(Integer id) {
        return this.manager.find(BoardGame.class, id);
    }

    @Override
    public Set<BoardGame> getAll() {
        return this.manager.createQuery("SELECT e FROM BoardGame e", BoardGame.class)
                .getResultList()
                .stream().collect(Collectors.toSet());
    }

    @Override
    public Stream<BoardGame> getAllStrem() {
        return this.manager.createQuery("SELECT e FROM BoardGame e", BoardGame.class).getResultStream();
    }
    */
}