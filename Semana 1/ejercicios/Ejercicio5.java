import java.util.Scanner;
import java.io.*;

public class Ejercicio5 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        int total = 0;
        int aprobados = 0;
        int reprobados = 0;
        float promedio = 0;
        int sum = 0;
        int num = 0;


        while (num != -1) {
            System.out.print("Ingrese un numero (-1 para salir): ");
            num = scanner.nextInt();
            if (num == -1) {
                break;
            }
            if (num < 0) {
                System.out.println("El numero no puede ser negativo. Intente de nuevo.");
                continue;
            } else if (num > 10) {
                System.out.println("El numero no puede ser mayor a 10. Intente de nuevo.");
                continue;
            }
            if (num > max) {
                max = num;
            }
            if (num < min) {
                min = num;
            }
            total += num;
            sum += 1;
            if (num >= 6) {
                aprobados++;
            }
            if (num < 6) {
                reprobados++;
            }
        }
        promedio = (float) total / sum;
        System.out.println("La mayor nota es: " + max);
        System.out.println("La menor nota es: " + min);
        System.out.println("La cantidad de aprobados es: " + aprobados);
        System.out.println("La cantidad de reprobados es: " + reprobados);
        System.out.println("El promedio es: " + promedio);
}
}