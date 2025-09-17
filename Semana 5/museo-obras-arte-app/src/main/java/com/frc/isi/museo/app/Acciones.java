package com.frc.isi.museo.app;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import com.frc.isi.museo.menu.ApplicationContext;
import com.frc.isi.museo.servicios.*;

public class Acciones {
    // public void nombreMetodo(ApplicationContext context) cada metodo de esta
    // clase la firma debe ser esta

    public void importarObras(ApplicationContext context) {
        var pathToImport = (URL) context.get("path");

        try (var paths = Files.walk(Paths.get(pathToImport.toURI()))) {
            var csvFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".csv"))
                    .map(Path::toFile)
                    .toList();

            csvFiles.stream()
                    .filter(f -> f.getName().contains("obras"))
                    .findFirst()
                    .ifPresentOrElse(f -> {
                        var service = context.getService(ObraArtisticaServicio.class);
                        try {
                            service.bulkInsert(f);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    },
                    () -> new IllegalArgumentException("Archivo inexistente"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
