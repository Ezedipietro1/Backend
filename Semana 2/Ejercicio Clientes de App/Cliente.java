public class Cliente {
    private String nombre;
    private int dni;
    private short edad;
    private String ocupacion;
    private int cantidadPosteos;
    private float horasEnPlataforma;
    private boolean verificado;
    
    //Constructores
    public Cliente(String nombre, int dni, short edad, String ocupacion, int cantidadPosteos, float horasEnPlataforma, boolean verificado){
        if (dni <= 0) {
        throw new IllegalArgumentException("El DNI no puede ser 0 ni negativo.");
        }
        if (edad <= 0) {
            throw new IllegalArgumentException("La edad no puede ser 0 ni negativa.");
        }
        if (cantidadPosteos < 0) {
            throw new IllegalArgumentException("La cantidad de posteos no puede ser negativa.");
        }
        if (horasEnPlataforma < 0) {
            throw new IllegalArgumentException("Las horas en plataforma no pueden ser negativas.");
        }
        this.nombre = nombre;
        this.dni = dni;
        this.edad = edad;
        this.ocupacion = ocupacion;
        this.cantidadPosteos = cantidadPosteos;
        this.horasEnPlataforma = horasEnPlataforma;
        this.verificado = verificado;
    }

    //Getters
    public String getNombre(){
        return this.nombre;
    }

    public int getDni(){
        return this.dni;
    }

    public short getEdad() {
    return this.edad;
    }

    public String getOcupacion() {
        return this.ocupacion;
    }

    public int getCantidadPosteos() {
        return this.cantidadPosteos;
    }

    public float getHorasEnPlataforma() {
        return this.horasEnPlataforma;
    }

    public boolean isVerificado() {
        return this.verificado;
    }

    //Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDni(int dni) {
        if (dni <= 0){
            throw new IllegalArgumentException("El DNI no puede ser 0 ni negativo");
        }
        this.dni = dni;
    }

    public void setEdad(short edad) {
        if (edad <= 0){
            throw new IllegalArgumentException("La edad no puede ser 0 ni negativa");
        }
        this.edad = edad;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public void setCantidadPosteos(int cantidadPosteos) {
        if (cantidadPosteos < 0){
            throw new IllegalArgumentException("La cantidad de posteos no puede ser negativa");
        }
        this.cantidadPosteos = cantidadPosteos;
    }

    public void setHorasEnPlataforma(float horasEnPlataforma) {
        if (horasEnPlataforma < 0){
            throw new IllegalArgumentException("La cantidad de horas no puede ser negativa");
        }
        this.horasEnPlataforma = horasEnPlataforma;
    }

    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
    }

    @Override
    public String toString() {
        return "Nombre: " + this.nombre + "\n"
                + "Dni: " + this.dni + "\n"
                + "Edad: " + this.edad + "\n"
                + "Ocupacion: " + this.ocupacion + "\n"
                + "Cantidad de Posteos: " + this.cantidadPosteos + "\n"
                + "Horas en plataforma: " + this.horasEnPlataforma + "\n"
                + "Verificado: " + this.verificado;
    }
}
