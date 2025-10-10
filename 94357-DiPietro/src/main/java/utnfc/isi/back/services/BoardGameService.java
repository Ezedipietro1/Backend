package utnfc.isi.back.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import utnfc.isi.back.domain.BoardGame;
import utnfc.isi.back.repo.BoardGameRepository;
import utnfc.isi.back.services.interfaces.IService;

public class BoardGameService implements IService<BoardGame, Integer> {
	
    private final BoardGameRepository repository;
    private final CategoryService categoryService;
    private final PublisherService publisherService;
    private final DesignerService designerService;

    public BoardGameService() {
        this.repository = new BoardGameRepository();
        this.categoryService = new CategoryService();
        this.publisherService = new PublisherService();
        this.designerService = new DesignerService();
    }

    @Override
    public BoardGame getById(Integer id) {
        return repository.getById(id);
    }

    @Override
    public BoardGame getOrCreateByName(String name) {
        BoardGame boardgame = repository.getByName(name);
        if (boardgame == null) {
            boardgame = new BoardGame();
            boardgame.setName(name);
            repository.create(boardgame);
        }
        return boardgame;
    }

    @Override
    public List<BoardGame> getAll() {
        return repository.getAllList();
    }

    
    public void bulkInsert(File fileToImport) throws IOException {
        Files.lines(Paths.get(fileToImport.toURI()))
                .skip(1)
                .forEach(linea -> {
                    BoardGame BoardGame = this.procesarLinea(linea);   
                    this.repository.create(BoardGame);
                });
    }

    private BoardGame procesarLinea(String linea) {
        String[] valores = linea.split(","); //Podria ser otro caracter de division
        BoardGame BoardGame = new BoardGame();

        //No conozco en que posicion vendran los valores
        //No conozco si habra algun tipo de chequeo extra o logica asociada
        BoardGame.setName(valores[0]);
        BoardGame.setYearPublished(Integer.parseInt(valores[1]));
        BoardGame.setMinAge(Integer.parseInt(valores[2]));
        BoardGame.setAverageRating(Double.parseDouble(valores[3]));
        BoardGame.setUsersRating(Integer.parseInt(valores[4]));
        BoardGame.setMinPlayers(Integer.parseInt(valores[5]));
        BoardGame.setMaxPlayers(Integer.parseInt(valores[6]));

        var deisgner = designerService.getOrCreateByName(valores[9]);
        BoardGame.setDesigner(deisgner);

        var publisher = publisherService.getOrCreateByName(valores[7]);
        BoardGame.setPublisher(publisher);

        var category = categoryService.getOrCreateByName(valores[8]);
        BoardGame.setCategory(category);

        return BoardGame;
    }

    public Stream<BoardGame> getAllStream() {
        return repository.getAllStream();
    }
}