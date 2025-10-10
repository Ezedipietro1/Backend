package utnfc.isi.back.services.config;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import utnfc.isi.back.services.interfaces.DbContextGeneratorService;

public class DbContextGeneratorServiceImpl implements DbContextGeneratorService {

    private final URL schemaPath = DbContextGeneratorServiceImpl.class.getResource("/sql/ddl_board_games.sql");

    public DbContextGeneratorServiceImpl() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("ContextGeneratorServiceImpl::new = " + e.getMessage());
        }
    }

    @Override
    public void inicializarDatabase() throws SQLException, IOException, URISyntaxException {
        if (schemaPath == null) {
            throw new IOException("No se encontró el recurso '/sql/ddl_board_games.sql' en el classpath");
        }

        String schema;
        try {
            // Intentar leer como Path (funciona en entorno de desarrollo)
            schema = Files.readString(Path.of(schemaPath.toURI()), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            // Fallback: leer desde el InputStream del recurso (funciona empaquetado en JAR)
            System.out.println("DbContextGeneratorServiceImpl: no se pudo leer por Path, usando stream fallback: " + ex.getMessage());
            try (var in = schemaPath.openStream()) {
                schema = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        }

        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:boardgames;DB_CLOSE_DELAY=-1", "sa", "")) {
            try (Statement st = connection.createStatement()) {
                st.execute(schema);
            }
            System.out.println("Inicializando base: " + connection.getMetaData().getURL());
        }
    }

}
