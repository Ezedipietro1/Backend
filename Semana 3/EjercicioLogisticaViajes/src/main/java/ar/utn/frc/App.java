package ar.utn.frc;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        HashMap<String, Cliente> clientes = new HashMap<>();
        ArrayList<Viaje> listaViajes = new ArrayList();

        try {
            File archivo = new File("viajes.csv");
            Scanner scanner = new Scanner(archivo);

            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                String[] datos = linea.split(";");

                if (datos.length == 13) {
                    // Datos de viajes
                    String codigo = datos[0];
                    int nroReserva = Integer.parseInt(datos[1]);
                    double precio = Double.parseDouble(datos[2]);
                    int tipo = Integer.parseInt(datos[3]);

                    // Datos aereos
                    int millasAcumuladas = Integer.parseInt(datos[4]);
                    String codAerolinea = datos[5];

                    // Datos terrestres
                    int provinciasVisitadas = Integer.parseInt(datos[6]);
                    int cantidadPasajeros = Integer.parseInt(datos[7]);

                    // Datos Maritimos
                    int cantidadContenedores = Integer.parseInt(datos[8]);
                    double costoPorKilo = Double.parseDouble(datos[9]);
                    double pesoTransportado = Double.parseDouble(datos[10]);

                    // Datos Clientes
                    String nombreEmpresa = datos[11];
                    String cuit = datos[12];

                    Cliente cliente = new Cliente(nombreEmpresa, cuit);
                    clientes.put(cuit, cliente);

                    if (tipo == 2) {
                        Terrestre viaje = new Terrestre(codigo, nroReserva, precio, tipo, cliente, provinciasVisitadas, cantidadPasajeros);
                        System.out.println(viaje);
                        listaViajes.add(viaje);
                    } else if (tipo == 1) {
                        Aereo viaje = new Aereo(codigo, nroReserva, precio, tipo, cliente, millasAcumuladas, codAerolinea);
                        listaViajes.add(viaje);
                    } else {
                        Maritimo viaje = new Maritimo(codigo, nroReserva, precio, tipo, cliente, cantidadContenedores, costoPorKilo, pesoTransportado);
                        listaViajes.add(viaje);
                    }
                }
            }
            
        } catch (FileNotFoundException e) {
            System.out.println("El archivo no existe");
        }

        int cantidadClientes = clientes.size();
        System.out.println("La cantidad de clientes es de: " + cantidadClientes);


    }
}
