import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String nombre;
        int opcion = -1;

        System.out.print("Ingrese el nombre de su mascota: ");
        nombre = scanner.nextLine();

        Mascota mascota = new Mascota(nombre, 100, 5);
        System.out.println("¡Has adoptado a " + mascota.getNombre() + "!");

        while (opcion != 0) {
            System.out.println("\n--- Menú ---");
            System.out.println("1. Ver estado");
            System.out.println("2. Alimentar");
            System.out.println("3. Dar de beber");
            System.out.println("4. Hacer que salte");
            System.out.println("5. Hacer que corra");
            System.out.println("6. Hacer que duerma");
            System.out.println("7. Despertar");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    System.out.println(mascota);
                    break;
                case 2:
                    mascota.comer();
                    break;
                case 3:
                    mascota.beber();
                    break;
                case 4:
                    mascota.saltar();
                    break;
                case 5:
                    mascota.correr();
                    break;
                case 6:
                    mascota.dormir();
                    break;
                case 7: 
                    mascota.despertar();
                    break;
                case 0:
                    System.out.println("¡Gracias por jugar!");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
        scanner.close();
    }
    
}
