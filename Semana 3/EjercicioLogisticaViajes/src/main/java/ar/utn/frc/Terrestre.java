package ar.utn.frc;

public class Terrestre extends Viaje{
    private int provinciasVisitadas;
    private int cantidadPasajeros;
    
    public Terrestre(String codigo, int nroReserva, double precio, int tipo, Cliente cliente, int provinciasVisitadas, int cantidadPasajeros) { 
        super(codigo, nroReserva, precio, tipo, cliente);
        this.provinciasVisitadas = provinciasVisitadas;
        this.cantidadPasajeros = cantidadPasajeros;
    }

    // Getter y Setter para provinciasVisitadas
    public int getProvinciasVisitadas() {
        return provinciasVisitadas;
    }

    public void setProvinciasVisitadas(int provinciasVisitadas) {
        this.provinciasVisitadas = provinciasVisitadas;
    }

    // Getter y Setter para cantidadPasajeros
    public int getCantidadPasajeros() {
        return cantidadPasajeros;
    }

    public void setCantidadPasajeros(int cantidadPasajeros) {
        this.cantidadPasajeros = cantidadPasajeros;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", provinciasVisitadas=" + provinciasVisitadas +
                ", cantidadPasajeros=" + cantidadPasajeros +
                '}';
    }
}
