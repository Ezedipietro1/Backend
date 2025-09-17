package com.frc.isi.museo.servicios;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.frc.isi.museo.entidades.ObraArtistica;
import com.frc.isi.museo.repositorio.ObraArtisticaRepositorio;

public class ObraArtisticaServicio {
    private final ObraArtisticaRepositorio obraArtisticaRepositorio;

    public ObraArtisticaServicio() {
        obraArtisticaRepositorio = new ObraArtisticaRepositorio();
    }

    public void bulkInsert(File fileToImport) {
        Files.lines(Paths.get(fileToImport.toURI()))
        .skip(1)
        .forEach(linea -> {
            ObraArtistica obra = procesarLinea(linea);
            obraArtisticaRepositorio.add(obra);
        });
    }

    public ObraArtistica procesarLinea(String linea) {
        String[] tokens = linea.split(",");

    }
}
