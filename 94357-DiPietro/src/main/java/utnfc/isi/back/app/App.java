package utnfc.isi.back.app;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Scanner;
import java.net.URL;

//import utnfc.isi.back.domain.BoardGame;
import utnfc.isi.back.menu.ItemMenu;
import utnfc.isi.back.menu.Menu;
import utnfc.isi.back.services.BoardGameService;
import utnfc.isi.back.services.config.DbContextGeneratorServiceImpl;
import utnfc.isi.back.services.interfaces.DbContextGeneratorService;

public class App {
    public static void main(String[] args) {
        DbContextGeneratorService database = new DbContextGeneratorServiceImpl();
        try {
            database.inicializarDatabase();
        } catch (SQLException | IOException | URISyntaxException e) {
            System.out.printf("App::main = Inicializar Base de Datos - %s%n", e.getMessage());
        }

        // inicializar context global de la app como KEY VALUE, STRING: OBJECT
        AppContext context = AppContext.getInstance();

        // reemplaza T por AppContext como variable qeu recibe dinammicamente
        Menu<AppContext> menu = new Menu<>();
        
        // menu.setTitulo("Menu de Opciones para Museo"); // capaz agregar atributo
        URL folderPath = App.class.getResource("/files");
        context.put("path", folderPath);
        context.registerService(BoardGameService.class, new BoardGameService());
        // context.registerService(EstiloArtisticoService.class, new EstiloArtisticoService());

        Actions actions = new Actions();
        
        menu.addOption(1, new ItemMenu<>(
            "Cargar BoardGames desde CSV", 
            actions::importarBoardGame
        ));

         
        menu.addOption(2, new ItemMenu<>(
            "Listar BoardGames", 
            actions::listarBoardGame
        ));

        
        menu.addOption(3, new ItemMenu<>(
            "Listar Juegos por Desarrollador", 
            actions::listarJuegosPorDesarrollador
        ));

        
        menu.addOption(4, new ItemMenu<>(
            "Contar juegos con restricción de edad", 
            actions::contarCantidadJuegosConRestriccionEdad
        ));

        
        menu.addOption(5, new ItemMenu<>(
            "Listar Juegos por Rango de Edades", 
            actions::listarJuegosPorEdades
        ));

        /*
        // AGREGADASS
        menu.addOption(6, new ItemMenu<>(
            "Listar empleados desde DB", 
            actions::listarEmpleados
        ));

        menu.addOption(7, new ItemMenu<>(
            "Generar Archivo CSV", 
            actions::generarArchivoEmpleadosPorDepartamento
        ));

        menu.addOption(8, new ItemMenu<>(
            "Buscar un empleado por su edad", 
            actions::buscarEmpleadoPorEdad
        ));
        */
        
        // inicializamos un unico scanner en appContext
        Scanner sc = new Scanner(System.in);
        context.put("scanner", sc);    // preguntar sobre opciones del scanner

        menu.runMenu(context);

    }
}
