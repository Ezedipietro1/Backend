package ar.edu.utnfrc.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "empleado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String nombre;
    private int edad;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    private double salario;

    @Column(name = "empleado_fijo", nullable = false)
    private boolean empleadoFijo;

    @ManyToOne
    @JoinColumn(name = "departamento_id")
    @ToString.Exclude
    private Departamento departamento;

    @ManyToOne
    @JoinColumn(name = "puesto_id")
    @ToString.Exclude
    private Puesto puesto;

    public double calcularSalarioFinal() {
        return empleadoFijo ? salario * 1.08 : salario;
    }
}
