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
            Cliente cliente = null;

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

                    if (clientes.containsKey(cuit)) {
                        cliente = clientes.get(cuit);
                        
                    } else {
                        cliente = new Cliente(nombreEmpresa, cuit);
                        clientes.put(cuit, cliente);
                    }
                    

                    if (tipo == 2) {
                        Terrestre viaje = new Terrestre(codigo, nroReserva, precio, tipo, cliente, provinciasVisitadas, cantidadPasajeros);
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
        System.out.println("La cantidad de clientes es de: " + cantidadClientes + "\n");

        int cantidadAereos = 0;
        int cantidadMillas = 0;
        int cantidadTerrestres = 0;
        int cantidadPasajeros = 0;
        int cantidadMaritimos = 0;
        int cantidadContenedores = 0;
        double costoAcumuladoTotal = 0;
        
        for (Viaje viaje : listaViajes) {
            if (viaje instanceof Aereo) {
                cantidadAereos++;
                cantidadMillas += ((Aereo) viaje).getMillasAcumuladas();
            } else if (viaje instanceof Terrestre) {
                cantidadTerrestres++;
                cantidadPasajeros += ((Terrestre) viaje).getCantidadPasajeros();
            } else {
                cantidadMaritimos++;
                cantidadContenedores += ((Maritimo) viaje).getCantidadContenedores();

                if (((Maritimo) viaje).getCantidadContenedores() >= 5) {
                    costoAcumuladoTotal += ((Maritimo) viaje).calcularCosto(); 
                }
            }
        }

        System.out.println("Informacion de Viajes Aereos:");
        System.out.println("La cantidad de viajes aereos es de: " + cantidadAereos);
        System.out.println("La cantidad de millas acumuladas en viajes aereos es de: " + cantidadMillas + "\n");
        System.out.println("Informacion de Viajes Terrestres:");
        System.out.println("La cantidad de viajes terrestres es de: " + cantidadTerrestres);
        System.out.println("La cantidad de pasajeros en viajes terrestres es de: " + cantidadPasajeros + "\n");
        System.out.println("Informacion de Viajes Maritimos:");
        System.out.println("La cantidad de viajes maritimos es de: " + cantidadMaritimos);
        System.out.println("La cantidad de contenedores en viajes maritimos es de: " + cantidadContenedores);
        System.out.println("El costo acumulado de los viajes maritimos con mas de 5 contenedores es de: " + costoAcumuladoTotal);
    }
}
