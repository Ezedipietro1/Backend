import java.io.*;
import java.util.Scanner;

public class Ejercicio3 {
    public static void main(String[] args) throws IOException {
        
    Scanner scanner = new Scanner(System.in);
    int numero;
    
   
    System.out.print("Ingrese un número entero: ");
    numero = scanner.nextInt();    

    while (numero <= 0 ){
        System.out.print("El numero debe ser mayor a 0. Ingrese un número entero: ");
        numero = scanner.nextInt(); 
    }   
    
    for (int i = 1; i <= numero; i++) {
        if ((i % 3 == 0 && i % 5 != 0) || (i % 5 == 0 && i % 3 != 0)) {
            System.out.println(i);
        }
    }
    scanner.close();
    System.out.println("Fin del programa.");
}
}