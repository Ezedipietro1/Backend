import java.io.*;
import java.lang.reflect.Array;
import java.util.Scanner;

public class Puerto {
    public static void main(String[] args) {
        ArraySimple arrayBarcos = new ArraySimple();
        boolean primeraLinea = true;

        try {
            File archivo = new File("barcos.csv");
            Scanner scanner = new Scanner(archivo);

            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                String[] datos = linea.split(",");

                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }

                if (datos.length == 8 && primeraLinea == false) {
                    String matricula = datos[0];
                    System.out.println(matricula);
                    int nroMuelle = Integer.parseInt(datos[1]);
                    int capacidad = Integer.parseInt(datos[2]);
                    float costoPorHora = Float.parseFloat(datos[3]);
                    int idCapitan = Integer.parseInt(datos[4]);
                    String nombreCapitan = datos[5];
                    String apellidoCapitan = datos[6];
                    int antiguedadCapitan = Integer.parseInt(datos[7]);

                    Capitan capitan = new Capitan(idCapitan, nombreCapitan, apellidoCapitan, antiguedadCapitan);
                    Barco barco = new Barco(matricula, nroMuelle, capacidad, costoPorHora, capitan);
                    System.out.println(barco);
                    arrayBarcos.append(barco);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("El archivo no existe");
        }

        float totalCarga = 0;
        for (int i = 0; i < arrayBarcos.size(); i++) {

            totalCarga += arrayBarcos.get(i).calcularCarga();

            

        }
    }
}
