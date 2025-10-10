package utnfc.isi.back.services.interfaces;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

public interface DbContextGeneratorService {

    void inicializarDatabase() throws SQLException, IOException, URISyntaxException;

}