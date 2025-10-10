package ar.edu.utnfrc.backend.app;

import java.util.Scanner;
import java.net.URL;

// import java.util.ArrayList;
import ar.edu.utnfrc.backend.menu.Menu;
import ar.edu.utnfrc.backend.menu.ItemMenu;

// import ar.edu.utnfrc.backend.services.PuestoService;
import ar.edu.utnfrc.backend.services.EmpleadoService;

public class App {
    public static void main(String[] args) {
        
        // inicializar context global de la app como KEY VALUE, STRING: OBJECT
        AppContext context = AppContext.getInstance();

        // reemplaza T por AppContext como variable qeu recibe dinammicamente
        Menu<AppContext> menu = new Menu<>();
        
        // menu.setTitulo("Menu de Opciones para Museo"); // capaz agregar atributo
        URL folderPath = App.class.getResource("/files");
        context.put("path", folderPath);
        context.registerService(EmpleadoService.class, new EmpleadoService());
        // context.registerService(EstiloArtisticoService.class, new EstiloArtisticoService());

        Actions actions = new Actions();
        
        menu.addOption(1, new ItemMenu<>(
            "Cargar empleados desde CSV", 
            actions::importarEmpleados
        ));

        menu.addOption(2, new ItemMenu<>(
            "Contar Empleados Fijos y No fijos", 
            actions::contarEmpleadosFijoYNoFijos
        ));

        menu.addOption(3, new ItemMenu<>(
            "Listar Empleados con su Salario Final", 
            actions::listarEmpleadosSalarioFinal
        ));

        menu.addOption(4, new ItemMenu<>(
            "Contar empleados por departamento", 
            actions::contarEmpleadosPorDepartamento
        ));

        menu.addOption(5, new ItemMenu<>(
            "Promedio Salarios por puesto", 
            actions::promedioSalariosPorPuesto
        ));

        // AGREGADASS
        menu.addOption(6, new ItemMenu<>(
            "Listar empleados desde DB", 
            actions::listarEmpleados
        ));

        menu.addOption(7, new ItemMenu<>(
            "Generar Archivo CSV", 
            actions::generarArchivoEmpleadosPorDepartamento
        ));

        menu.addOption(8, new ItemMenu<>(
            "Buscar un empleado por su edad", 
            actions::buscarEmpleadoPorEdad
        ));
        
        // inicializamos un unico scanner en appContext
        Scanner sc = new Scanner(System.in);
        context.put("scanner", sc);    // preguntar sobre opciones del scanner

        menu.runMenu(context);
    }
}