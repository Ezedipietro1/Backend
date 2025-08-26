import java.util.Scanner;
import java.io.*;

public class Ejercicio2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el codigo ISBN: ");
        String codigo = scanner.nextLine();
        int total = 0;
        int num = 10; 
        
        for (int i = 0; i < codigo.length(); i++) {
            if (codigo.charAt(i) != '-') { 
                total += (codigo.charAt(i) - 0) * num;
                num--;
            }
        }
        if (total % 11 == 0) {
            System.out.println("El codigo ISBN es valido.");
        } else {
            System.out.println("El codigo ISBN no es valido.");
        }
        scanner.close();
    }
}
