package com.SolicitudTraslado.domain;

import jakarta.persistence.*;
import lombok.*;
import com.SolicitudTraslado.domain.enums.EstadoSolicitud;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "solicitud_traslado")
public class SolicitudTraslado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numero;

    // Cliente es administrado por otro microservicio; almacenamos solo su id
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_id", nullable = false)
    private Ruta ruta;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contenedor_id", nullable = false)
    private Contenedor contenedor;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tarifa_id", nullable = false)
    private Tarifa tarifa;

    @Column(name = "costo_estimado", nullable = false)
    private Double costoEstimado;

    @Column(name = "tiempo_estimado", nullable = false)
    private Double tiempoEstimado;

    @Column(name = "costo_final")
    private Double costoFinal;

    @Column(name = "tiempo_real")
    private Double tiempoReal;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoSolicitud estado;
}
