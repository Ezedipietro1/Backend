package ar.edu.utnfrc.backend.app;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.BufferedWriter;
import java.io.IOException;

import java.util.Scanner;


import java.util.Map;
import ar.edu.utnfrc.backend.services.EmpleadoService;
// import museo.arte.services.EstiloArtisticoService;


public class Actions {

    /* 
     * Método de ejemplo (del profesor) que permite importar empleados desde un archivo CSV.
     * Básicamente busca en un directorio archivos CSV que contengan la palabra "empleado" 
     * y los carga en el sistema usando el servicio EmpleadoService.
     */
    public void importarEmpleados(AppContext context) {
        // Obtiene del contexto (AppContext) la URL donde están los archivos a importar
        var pathToImport = (URL) context.get("path");

        // Bloque try-with-resources: recorre todos los archivos dentro del directorio indicado
        try (var paths = Files.walk(Paths.get(pathToImport.toURI()))) {
            
            // Se filtran los archivos encontrados:
            // 1. Solo se toman archivos regulares (no directorios)
            // 2. Que terminen en ".csv"
            // 3. Luego se convierten a objetos File y se guardan en una lista
            var csvFiles = paths
                    .filter(Files::isRegularFile)               // solo archivos, no carpetas
                    .filter(path -> path.toString().endsWith(".csv")) // que terminen en ".csv"
                    .map(path -> path.toFile())                 // convertir Path → File
                    .toList();                                  // recolectar en lista

            // Se procesa la lista de archivos CSV:
            // 1. Busca el primer archivo cuyo nombre contenga la palabra "empleado"
            // 2. Si lo encuentra → lo pasa al servicio para cargar empleados
            // 3. Si no lo encuentra → lanza una excepción
            csvFiles.stream()
                    .filter(f -> f.getName().contains("empleado"))  // buscar archivo con "empleado" en el nombre
                    .findFirst()                                   // quedarse con el primero
                    .ifPresentOrElse(f -> {                        // si existe:
                        // Obtener el servicio de empleados desde el contexto
                        var service = context.getService(EmpleadoService.class);
                        try {
                            // Insertar en bloque todos los empleados del archivo CSV
                            service.bulkInsert(f);
                        } catch (IOException e) {
                            e.printStackTrace(); // manejar error de lectura del archivo
                        }
                    },
                    () -> {
                        // Si no se encontró ningún archivo válido, lanzar excepción
                        throw new IllegalArgumentException("Archivo inexistente");
                    });

        } catch (IOException | URISyntaxException e) {
            // Manejo de errores: problemas de acceso al archivo o conversión de URI
            e.printStackTrace();
        }
    }

    public void listarEmpleados(AppContext context) {
        var service = context.getService(EmpleadoService.class);

        // Recuperar todas los empleados desde la BD
        var empleados = service.getAll();

        if (empleados.isEmpty()) {
            System.out.println("⚠ No hay empleados registradas en la base de datos.");
        } else {
            System.out.println("📋 Lista de empleados:");
            empleados.forEach(emp -> {
                System.out.printf(
                    "ID: %d | Nombre: %s | Año: %s | Puesto: %s | Departamento: %s | Salario: %.2f | Empleado Fijo: %s%n",
                    emp.getId(),
                    emp.getNombre(),
                    emp.getFechaIngreso().toString(),
                    emp.getPuesto() != null ? emp.getPuesto().getNombre() : "Desconocido",
                    emp.getDepartamento() != null ? emp.getDepartamento().getNombre() : "Desconocido",
                    emp.getSalario(),
                    emp.isEmpleadoFijo() ? "Sí" : "No"
                );
            });
        }
    }

    /* =================================================================================
     * Opcion 2
     ================================================================================= */
    /*
    public void contarEmpleadosFijoYNoFijos(AppContext context) {
        /*
        * Mostrar la cantidad de empleados fijos y no fijos.
        *
        EmpleadoService service = context.getService(EmpleadoService.class);
        
        // Recuperar todos los empleados desde la BD
        var empleados = service.getAll();

        long contFijos = service.contarEmpleadosFijos(empleados, true);
        long contNoFijos = service.contarEmpleadosFijos(empleados, false);
                           
        System.out.println("Empleados fijos: " + contFijos);
        System.out.println("Empleados no fijos: " + contNoFijos);
    } */

    public void contarEmpleadosFijoYNoFijos(AppContext context) {
        // 1️⃣ Obtener el servicio de empleados desde el contexto de la aplicación
        var service = context.getService(EmpleadoService.class);

        // 2️⃣ Recuperar la lista completa de empleados desde la base de datos
        var empleados = service.getAll();

        // 3️⃣ Mandar la lista de empleados al Empleado Service para que aplique la logica de negocio
        Map<Boolean, Long> resultado = service.contarFijosYNoFijos(empleados);

        // 4️⃣ Mostrar el resultado
        System.out.println("Empleados fijos: " + resultado.get(true));
        System.out.println("Empleados no fijos: " + resultado.get(false));
    }

    /* =================================================================================
     * Opcion 3
     ================================================================================= */
    public void listarEmpleadosSalarioFinal(AppContext context) {
        // 1️⃣ Obtener el servicio de empleados desde el contexto de la aplicación
        var service = context.getService(EmpleadoService.class);

        // 2️⃣ Recuperar la lista completa de empleados desde la base de datos
        var empleados = service.getAll();

        if (empleados.isEmpty()) {
            System.out.println("⚠ No hay empelados registradas en la base de datos.");
        } else {
            System.out.println("📋 Lista de empleados:");
            empleados.forEach(emp -> {
                System.out.printf(
                    "Nombre: %s | Salario: %.2f | Salario Final: %.2f %n",
                    emp.getNombre(),
                    emp.getSalario(),
                    emp.calcularSalarioFinal()
                );
            });
        }
    }

    /* =================================================================================
     * Opcion 4
     ================================================================================= */
    public void contarEmpleadosPorDepartamento(AppContext context) {
        // Mostrar cantidad de empleados por departamento.
        var service = context.getService(EmpleadoService.class);
        var empleados = service.getAll();

        var mapaDepartamentos = service.mapaEmpleadosPorDepartamento(empleados);
        
        // Mostrar los resultados
        mapaDepartamentos.forEach((nombre, cantidad) ->
            System.out.println(nombre + ": " + cantidad)
        );
    }

    /* =================================================================================
     * Opcion 5
     ================================================================================= */
    public void promedioSalariosPorPuesto(AppContext context) {
        // 1️⃣ Obtener el servicio de empleados
        var service = context.getService(EmpleadoService.class);

        // 2️⃣ Recuperar todos los empleados desde la base de datos
        var empleados = service.getAll();

        var promedioPorPuesto = service.promedioSalariosPorPuesto(empleados);

        // 4️⃣ Mostrar los resultados
        promedioPorPuesto.forEach((puesto, promedio) ->
            System.out.printf("%s: salario promedio = %.2f%n", puesto, promedio)
        );
    }

    /* =================================================================================
     * Opcion 7
     ================================================================================= */
    public void generarArchivoEmpleadosPorDepartamento(AppContext context) {
        // 1️⃣ Obtener el servicio de empleados desde el contexto
        var service = context.getService(EmpleadoService.class);

        // 2️⃣ Recuperar todos los empleados desde la base de datos
        var empleados = service.getAll();

        // 3️⃣ Obtener el mapa con la cantidad de empleados por departamento
        var mapaDepartamentos = service.mapaEmpleadosPorDepartamento(empleados);

        // 4️⃣ Mostrar los resultados en consola
        mapaDepartamentos.forEach((nombre, cantidad) ->
            System.out.println(nombre + ": " + cantidad)
        );

        // 5️⃣ Crear carpeta y archivo, escribir el contenido
        try {
            // Crear carpeta "reportes" si no existe
            Files.createDirectories(Paths.get("reportes"));

            // Definir ruta del archivo
            Path path = Paths.get("reportes/reporte_departamentos.txt");

            // Abrir el archivo para escritura
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                // Escribir encabezado CSV
                writer.write("Departamento,Cantidad de Empleados");
                writer.newLine();

                // Escribir cada departamento y su cantidad
                for (var entry : mapaDepartamentos.entrySet()) {
                    writer.write(entry.getKey() + "," + entry.getValue());
                    writer.newLine();
                }

                System.out.println("\n✅ Archivo generado correctamente: " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("❌ Error al escribir el archivo: " + e.getMessage());
        }
    }

    /* =================================================================================
    * Opcion 8
    ================================================================================= */
    public void buscarEmpleadoPorEdad(AppContext context) {
        // 1️⃣ Obtener el servicio de empleados desde el contexto
        var service = context.getService(EmpleadoService.class);

        // 2️⃣ Obtener el scanner desde el contexto
        var scanner = (Scanner) context.get("scanner");

        // 3️⃣ Pedir el ID del empleado
        System.out.print("Ingrese edad de empleados a filtrar: ");
        int edad = scanner.nextInt();
        scanner.nextLine(); // limpia el salto de línea pendiente

        // 4️⃣ Buscar el empleado por ID
        // var empleados = service.getEmpleadosByAge(edad);
        var listaEmpleados = service.getAll();
        var empleados = service.getEmpleadosByAge(listaEmpleados, edad);

        // 5️⃣ Mostrar el resultado
        empleados.forEach(emp -> System.out.println(emp));

    }


}
