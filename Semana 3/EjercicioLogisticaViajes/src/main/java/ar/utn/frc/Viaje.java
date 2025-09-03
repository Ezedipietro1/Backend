package ar.utn.frc;

public class Viaje {
    protected String codigo;
    protected int nroReserva;
    protected double precio;
    protected int tipo;
    protected Cliente cliente;
    
    public Viaje(String codigo, int nroReserva, double precio, int tipo, Cliente cliente){
        this.codigo = codigo;
        this.nroReserva = nroReserva;
        this.precio = precio;
        this.tipo = tipo;
        this.cliente = cliente;
    }

    // Getters
    public String getCodigo() {
        return codigo;
    }

    public int getNroReserva() {
        return nroReserva;
    }

    public double getPrecio() {
        return precio;
    }

    public int getTipo() {
        return tipo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    // Setters
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setNroReserva(int nroReserva) {
        this.nroReserva = nroReserva;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return "Viaje{" +
                "codigo='" + codigo + '\'' +
                ", nroReserva=" + nroReserva +
                ", precio=" + precio +
                ", tipo=" + tipo +
                ", " + cliente +
                '}';
    }
}
