package ar.utn.frc;

public class Cliente {
    private String nombreEmpresa;
    private String cuit;

    public Cliente(String nombreEmpresa, String cuit) {
        this.nombreEmpresa = nombreEmpresa;
        this.cuit = cuit;
    }

    public String getNombreEmpresa() {
        return this.nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getCuit() {
        return this.cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "nombreEmpresa='" + nombreEmpresa + '\'' +
                ", cuit='" + cuit + '\'' +
                '}';
    }
}
