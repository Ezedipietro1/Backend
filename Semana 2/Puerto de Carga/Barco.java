public class Barcos {
    private String matricula;
    private int nroMuelle;
    private int capacidad;
    private int costoPorHora;
    private Capitan capitan;

    // Constructor
    public Barcos(String matricula, int nroMuelle, int capacidad, int costoPorHora, Capitan capitan) {
        this.matricula = matricula;
        this.nroMuelle = nroMuelle;
        this.capacidad = capacidad;
        this.costoPorHora = costoPorHora;
        this.capitan = capitan;
    }

    // Getters
    public String getMatricula(){
        return this.matricula
    }

    public int getNroMuelle(){
        return this.nroMuelle
    }

    public int getCapacidad(){
        return this.capacidad
    }

    public int getcostoPorHora(){
        return this.costoPorHora
    }

    public Object getCapitan(){
        return this.capitan
    }

    //Setters
    public void setMatricula(String matricula){
        this.matricula = matricula
    }

    public void setNroMuelle(int nroMuelle){
        this.nroMuelle = nroMuelle
    }

    public void setCapacidad(int capacidad){
        this.capacidad = capacidad
    }

    public void setCostoPorHora(int costoPorHora){
        this.costoPorHora = costoPorHora
    }

    public void setCapitan(Capitan capitan){
        this.capitan = capitan
    }
    
}
