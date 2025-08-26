public class Capitan {
    private int id;
    private String nombre;
    private String apellido;
    private int antiguedad;

    public Capitan(int id, String nombre, String apellido, int antiguedad){
        this.id = id
        this.nombre = nombre
        this.apellido = apellido
        this.antiguedad = antiguedad
    }

    //Getters 
    public int getId(){
        return this.id
    }

    public String getNombre(){
        return this.nombre
    }

    public String getApellidos(){
        return this.apellido
    }

    public int getAntiguedad(){
        return this.antiguedad
    }

    //Setters
    
    
}
