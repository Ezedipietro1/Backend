package com.SolicitudTraslado.repo;

import com.SolicitudTraslado.domain.Deposito;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@Repository
public interface DepositoRepo extends JpaRepository<Deposito, Long> {
    List<Deposito> findByUbicacionId(Long ubicacionId);
    List<Deposito> findByCiudadId(Long ciudadId);
}
