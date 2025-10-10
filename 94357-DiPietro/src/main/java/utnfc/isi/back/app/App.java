package utnfc.isi.back.app;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import utnfc.isi.back.services.config.DbContextGeneratorServiceImpl;
import utnfc.isi.back.services.interfaces.DbContextGeneratorService;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        DbContextGeneratorService database = new DbContextGeneratorServiceImpl();
        try {
            database.inicializarDatabase();
        } catch (SQLException | IOException | URISyntaxException e) {
            System.out.printf("App::main = Inicializar Base de Datos - %s%n", e.getMessage());
        }

    }
}
