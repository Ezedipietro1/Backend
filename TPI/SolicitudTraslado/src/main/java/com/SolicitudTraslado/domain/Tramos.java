package com.SolicitudTraslado.domain;

import java.sql.Date;

import jakarta.persistence.*;
import lombok.*;
import com.SolicitudTraslado.domain.enums.TipoTramo;
import com.SolicitudTraslado.domain.enums.EstadoTramo;;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "tramos")
public class Tramos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "dominio_camion", nullable = false)
    private Camion camion;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "origen_id", nullable = false)
    private Deposito origen;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "destino_id", nullable = false)
    private Deposito destino;

    @Column(nullable = false)
    private Date fechaInicio;

    @Column(nullable = true)
    private Date fechaFin;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_id", nullable = false)
    private Ruta ruta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_tramo", nullable = false, length = 20)
    private TipoTramo tipoTramo;  

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_tramo", nullable = false, length = 20)
    private EstadoTramo estadoTramo;
}
