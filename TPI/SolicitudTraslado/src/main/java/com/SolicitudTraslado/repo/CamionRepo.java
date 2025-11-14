package com.SolicitudTraslado.repo;

import com.SolicitudTraslado.domain.Camion;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@Repository
public interface CamionRepo extends JpaRepository<Camion, Long> {
    List<Camion> findByState(boolean estado);
    List<Camion> findBySupportsKgAndVolumen(Double kg, Double volumen);
    List<Camion> findByTransportistaAndActivo(String dni);
}
