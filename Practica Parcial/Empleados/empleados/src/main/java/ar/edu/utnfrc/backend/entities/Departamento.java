package ar.edu.utnfrc.backend.entities;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "departamento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Departamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;

    @OneToMany(mappedBy = "departamento", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Empleado> empleados = new HashSet<>();
}
