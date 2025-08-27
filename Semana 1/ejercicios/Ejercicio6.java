import java.util.Scanner;
import java.io.*;

public class Ejercicio6 {
    public static void main(String[] args) {
        try {
        File archivo = new File("numeros.txt");
        Scanner scanner = new Scanner(archivo);
        int pares = 0;
        int impares = 0;
        int invalidas = 0;
        int cant = 0;
        int total = 0;
        float promedio = 0;
        int suma = 0;

        while (scanner.hasNextLine()){
            String linea = scanner.nextLine();
            
            try {
                int numero = Integer.parseInt(linea.trim());
                cant ++;
                if (numero % 2 == 0) {
                    pares++;
                } else {
                    impares++;
                }
                total ++;
                suma += numero;
            } catch (NumberFormatException e) {
                invalidas++;
            }
        }
        scanner.close();

        promedio = (float) suma / cant;
        System.out.println("Cantidad de números pares: " + pares);
        System.out.println("Cantidad de números impares: " + impares);
        System.out.println("Cantidad de líneas inválidas: " + invalidas);
        System.out.println("Cantidad total de números leídos: " + total);
        System.out.println("Promedio de los números leídos: " + promedio);
        
        } catch (FileNotFoundException e) {
            System.out.println("El archivo no se encontró.");
        }
    }
}