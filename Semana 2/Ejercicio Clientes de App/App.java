import java.util.Scanner;
import java.io.*;

public class App {
    public static void main(String[] args) {
        Cliente[] arrayClientes = new Cliente[200];
        float[] arrayPuntos = new float[200];
        int contadorClientes = -1;

        try {
            File archivo = new File("clientes.csv");
            Scanner scanner = new Scanner(archivo);

            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();

                String[] datos = linea.split(",");

                if (contadorClientes == -1) {
                    contadorClientes ++;
                    continue;
                }

                if (datos.length == 7) {
                    String nombre = datos[0];
                    int dni = Integer.parseInt(datos[1]);
                    short edad = Short.parseShort(datos[2]);
                    String ocupacion = datos[3];
                    int cantidadPosteos = Integer.parseInt(datos[4]);
                    float horasEnPlataforma = Float.parseFloat(datos[5]);
                    boolean verificado = Boolean.parseBoolean(datos[6]); 

                    Cliente cliente = new Cliente(nombre, dni, edad, ocupacion, cantidadPosteos, horasEnPlataforma, verificado);
                    arrayClientes[contadorClientes] = cliente;
                    contadorClientes ++;
                }
            }
            Calculos calculos = new Calculos(arrayClientes);
            int mayores = calculos.calcularMayoresDe(25);
            System.out.println("La cantidad de clientes mayores de 25, es de: " + mayores);

            int totalPosteos = calculos.totalPosteos();
            System.out.println("La cantidad total de posteos es de: " + totalPosteos);

            arrayPuntos = calculos.calcularPuntuacionTodos();

            System.out.println("Puntuacion del cliente 79: " + calculos.calcularPuntuacion(arrayClientes[78]));

        } catch (FileNotFoundException e) {
            System.out.println("El archivo no existe");
        }

        

        System.out.println("\n Lista de Clientes:");
        for (int i = 0; i < 10; i++) {
            System.out.println(arrayClientes[i] + "\n");
            System.out.println("Puntuacion del cliente: " + arrayPuntos[i] + "\n");
        }
    }
}
