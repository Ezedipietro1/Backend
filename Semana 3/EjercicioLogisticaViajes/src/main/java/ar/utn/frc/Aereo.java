package ar.utn.frc;

public class Aereo extends Viaje {
    private int millasAcumuladas;
    private String codAerolinea;
    
    public Aereo(String codigo, int nroReserva, double precio, int tipo, Cliente cliente, int millasAcumuladas, String codAerolinea) {
        super(codigo, nroReserva, precio, tipo, cliente);
        this.millasAcumuladas = millasAcumuladas;
        this.codAerolinea = codAerolinea;
    }
    
    //Getters y Setters
    public int getMillasAcumuladas() {
        return millasAcumuladas;
    }

    public void setMillasAcumuladas(int millasAcumuladas) {
        this.millasAcumuladas = millasAcumuladas;
    }

    // Getter y Setter para codAerolinea
    public String getCodAerolinea() {
        return codAerolinea;
    }

    public void setCodAerolinea(String codAerolinea) {
        this.codAerolinea = codAerolinea;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", millasAcumuladas=" + millasAcumuladas +
                ", codAerolinea='" + codAerolinea + '\'' +
                '}';
    }
    
}
