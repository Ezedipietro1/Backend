package com.frc.isi.museo.entidades;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ObraArtistica {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int codigo;

    private String nombre;
    
    private String anio;

    private double montoAsegurado;
    
    private boolean seguroTotal;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "museoId", referencedColumnName = "id")
    @ToString.Exclude
    private Museo museo;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "estiloArtisticoId", referencedColumnName = "id")
    @ToString.Exclude
    private EstiloArtistico estiloArtistico;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "autorId", referencedColumnName = "id")
    @ToString.Exclude
    private Autor autor;
}
