package utnfc.isi.back.app;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import utnfc.isi.back.services.BoardGameService;

public class Actions {
    
    /* 
     * Método de ejemplo (del profesor) que permite importar registros desde un archivo CSV.
     * Básicamente busca en un directorio archivos CSV que contengan la palabra "boardgame" 
     * y los carga en el sistema usando el servicio BoardGameService.
     */
    public void importarBoardGame(AppContext context) {
        // Obtiene del contexto (AppContext) la URL donde están los archivos a importar
        var pathToImport = (URL) context.get("path");

        // Bloque try-with-resources: recorre todos los archivos dentro del directorio indicado
        try (var paths = Files.walk(Paths.get(pathToImport.toURI()))) {
            
            // Se filtran los archivos encontrados:
            // 1. Solo se toman archivos regulares (no directorios)
            // 2. Que terminen en ".csv"
            // 3. Luego se convierten a objetos File y se guardan en una lista
            var csvFiles = paths
                    .filter(Files::isRegularFile)               // solo archivos, no carpetas
                    .filter(path -> path.toString().endsWith(".csv")) // que terminen en ".csv"
                    .map(path -> path.toFile())                 // convertir Path → File
                    .toList();                                  // recolectar en lista

            // Se procesa la lista de archivos CSV:
            // 1. Busca el primer archivo cuyo nombre contenga la palabra "boardgame"
            // 2. Si lo encuentra → lo pasa al servicio para cargar boardgames
            // 3. Si no lo encuentra → lanza una excepción
            csvFiles.stream()
                    .filter(f -> f.getName().contains("boardgames"))  // buscar archivo con "boardgame" en el nombre
                    .findFirst()                                   // quedarse con el primero
                    .ifPresentOrElse(f -> {                        // si existe:
                        // Obtener el servicio de boardgames desde el contexto
                        var service = context.getService(BoardGameService.class);
                        try {
                            // Insertar en bloque todos los boardgames del archivo CSV
                            service.bulkInsert(f);
                        } catch (IOException e) {
                            e.printStackTrace(); // manejar error de lectura del archivo
                        }
                    },
                    () -> {
                        // Si no se encontró ningún archivo válido, lanzar excepción
                        throw new IllegalArgumentException("Archivo inexistente");
                    });

        } catch (IOException | URISyntaxException e) {
            // Manejo de errores: problemas de acceso al archivo o conversión de URI
            e.printStackTrace();
        }
    }

    public void listarBoardGame(AppContext context) {
        var service = context.getService(BoardGameService.class);

        // Recuperar todas los boardgames desde la BD
        var boardgames = service.getAll();

        if (boardgames.isEmpty()) {
            System.out.println("⚠ No hay BoardGames registradas en la base de datos.");
        } else {
            System.out.println("📋 Lista de BoardGames:");
            boardgames.forEach(board -> {
                System.out.printf(
                    "ID: %d | Nombre: %s | Año Publicacion: %s | Edad Minima: %s | Rating Promedio: %s | Rating Usuarios: %s | Min Jugadores: %s | Max Jugadores: %s | Diseñador: %s | Publicador: %s | Categoria: %s%n",
                    board.getId(),
                    board.getName(),
                    board.getYearPublished()!= null ? board.getYearPublished().toString() : " - ", 
                    board.getMinAge() != null ? board.getMinAge().toString() : " - ",
                    board.getAverageRating() != null ? board.getAverageRating().toString() : "-",
                    board.getUsersRating() != null ? board.getUsersRating().toString() : "-",
                    board.getMinPlayers() != null ? board.getMinPlayers().toString() : "-",
                    board.getMaxPlayers() != null ? board.getMaxPlayers().toString() : "-",
                    board.getDesigner().getNombre(),
                    board.getPublisher().getNombre(),
                    board.getCategory().getNombre()
                );
            });
        }
    }

}
