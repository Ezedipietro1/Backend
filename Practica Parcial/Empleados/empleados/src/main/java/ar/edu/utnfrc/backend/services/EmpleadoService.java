package ar.edu.utnfrc.backend.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate; 
import java.util.List;
import java.util.stream.Stream;

import ar.edu.utnfrc.backend.entities.Empleado;
import ar.edu.utnfrc.backend.repositories.EmpleadoRepository;
import ar.edu.utnfrc.backend.services.interfaces.IService;

public class EmpleadoService implements IService<Empleado, Integer> {
	
    private final EmpleadoRepository repository;
    private final DepartamentoService departamentoService;
    private final PuestoService puestoService;

    public EmpleadoService() {
        this.repository = new EmpleadoRepository();
        this.departamentoService = new DepartamentoService();
        this.puestoService = new PuestoService();
    }

    @Override
    public Empleado getById(Integer id) {
        return repository.getById(id);
    }

    @Override
    public Empleado getOrCreateByName(String name) {
        Empleado Empleado = repository.getByName(name);
        if (Empleado == null) {
            Empleado = new Empleado();
            Empleado.setNombre(name);
            repository.create(Empleado);
        }
        return Empleado;
    }

    @Override
    public List<Empleado> getAll() {
        return repository.getAllList();
    }

    
    public void bulkInsert(File fileToImport) throws IOException {
        Files.lines(Paths.get(fileToImport.toURI()))
                .skip(1)
                .forEach(linea -> {
                    Empleado empleado = this.procesarLinea(linea);   
                    this.repository.create(empleado);
                });
    }

    private Empleado procesarLinea(String linea) {
        String[] valores = linea.split(","); //Podria ser otro caracter de division
        Empleado empleado = new Empleado();

        //No conozco en que posicion vendran los valores
        //No conozco si habra algun tipo de chequeo extra o logica asociada
        empleado.setNombre(valores[0]);
        empleado.setEdad(Integer.parseInt(valores[1]));
        empleado.setFechaIngreso(LocalDate.parse(valores[2]));
        empleado.setSalario(Double.parseDouble(valores[3]));
        empleado.setEmpleadoFijo(Boolean.parseBoolean(valores[4]));

        var departamento = departamentoService.getOrCreateByName(valores[5]);
        empleado.setDepartamento(departamento);

        var puesto = puestoService.getOrCreateByName(valores[6]);
        empleado.setPuesto(puesto);

        return empleado;
    }

    public Stream<Empleado> getAllStream() {
        return repository.getAllStream();
    }
}
