package ar.edu.utnfrc.backend.menu;

import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

import ar.edu.utnfrc.backend.app.AppContext;

public class Menu<T> implements IMenu<T> {
    private Map<Integer, ItemMenu<T>> options = new HashMap<>();

    @Override
    public void addOption(int opcion, ItemMenu<T> action) {
        this.options.put(opcion, action);
        System.out.println("Opción " + opcion + ": " + action + " registrada.");
    }

    @Override
    public void runMenu(T context) {
        while (true) {
            int choice = this.showMenu(context);

            if (choice == 0) {
                System.out.println("Saliendo...");
                break;
            }

            if (this.options.containsKey(choice)) {
                this.options.get(choice).ejecutar(context);
            } else {
                System.out.println("Opción no válida.");
            }
        }
    }

    private int showMenu(T context) {
        if (context instanceof AppContext appCtx) {
            Scanner sc = appCtx.get("scanner", Scanner.class);

            System.out.println("Opciones disponibles:");

            // Mostrar usando key + value
            for (Map.Entry<Integer, ItemMenu<T>> entry : this.options.entrySet()) {
                System.out.println(entry.getKey() + " - " + entry.getValue());
            }
            System.out.println("0 - Salir.");
            System.out.print("Seleccione opción: ");

            while (!sc.hasNextInt()) {
                System.out.println("Entrada inválida. Intente de nuevo:");
                sc.next(); // consumir la entrada no válida
            }

            return sc.nextInt();
        } else {
            throw new IllegalArgumentException("El contexto no es AppContext");
        }
    }
}