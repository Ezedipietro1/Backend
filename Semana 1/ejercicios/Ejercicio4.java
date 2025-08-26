import java.util.Scanner;
import java.io.*;

public class Ejercicio4 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese su nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Ingrese las horas trabajadas: ");
        int horas = scanner.nextInt();
        System.out.print("Ingrese la cantidad de tareas realizadas: ");
        int tareas = scanner.nextInt();

        int productividad = 0;
        if (horas < 8) {
            productividad = tareas * 10 - (8 - horas) * 5;
        }
        else  if (horas == 8) {
            productividad = tareas * 10;
        }
        else {
            productividad = tareas * 10 + 5;
        }
        scanner.close();
        System.out.println("Nombre: " + nombre);
        System.out.println("Horas trabajadas: " + horas);
        System.out.println("Tareas realizadas: " + tareas);
        System.out.println("Productividad: " + productividad);
}  
}
