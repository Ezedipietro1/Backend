package ar.utn.frc;

public class Maritimo extends Viaje {
    private int cantidadContenedores;
    private double costoPorKilo;
    private double pesoTransportado;

    public Maritimo(String codigo, int nroReserva, double precio, int tipo, Cliente cliente, int cantidadContenedores, double costoPorKilo, double pesoTransportado) { 
        super(codigo, nroReserva, precio, tipo, cliente);
        this.cantidadContenedores = cantidadContenedores;
        this.costoPorKilo = costoPorKilo;
        this.pesoTransportado = pesoTransportado;
    }

    // Getter y Setter para cantidadContenedores
    public int getCantidadContenedores() {
        return cantidadContenedores;
    }

    public void setCantidadContenedores(int cantidadContenedores) {
        this.cantidadContenedores = cantidadContenedores;
    }

    // Getter y Setter para costoPorKilo
    public double getCostoPorKilo() {
        return costoPorKilo;
    }

    public void setCostoPorKilo(double costoPorKilo) {
        this.costoPorKilo = costoPorKilo;
    }

    // Getter y Setter para pesoTransportado
    public double getPesoTransportado() {
        return pesoTransportado;
    }

    public void setPesoTransportado(double pesoTransportado) {
        this.pesoTransportado = pesoTransportado;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", cantidadContenedores=" + cantidadContenedores +
                ", costoPorKilo=" + costoPorKilo +
                ", pesoTransportado=" + pesoTransportado +
                '}';
    }
}
