package com.SolicitudTraslado.services;

import com.SolicitudTraslado.domain.Ruta;
import com.SolicitudTraslado.repo.RutaRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class RutaService {
    private final RutaRepo rutaDepo;
    public RutaService(RutaRepo rutaDepo) {
        this.rutaDepo = rutaDepo;
    }

    @Transactional
    public Ruta crearRuta(Ruta ruta) {
        return rutaDepo.save(ruta);
    }

    @Transactional
    public Ruta actualizarRuta(Ruta rutaActualizada) {
        return rutaDepo.save(rutaActualizada);
    }


}
