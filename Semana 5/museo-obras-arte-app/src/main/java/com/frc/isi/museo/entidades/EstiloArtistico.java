package com.frc.isi.museo.entidades;

import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class EstiloArtistico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    
    @OneToMany(mappedBy = "estiloArtistico", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<ObraArtistica> obras;
}
