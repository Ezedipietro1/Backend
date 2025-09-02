public class Capitan {
    private String id;
    private String nombre;
    private String apellido;
    private int antiguedad;

    public Capitan(String id, String nombre, String apellido, int antiguedad){
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.antiguedad = antiguedad;
    }

    //Getters 
    public String getId(){
        return this.id;
    }

    public String getNombre(){
        return this.nombre;
    }

    public String getApellidos(){
        return this.apellido;
    }

    public int getAntiguedad(){
        return this.antiguedad;
    }

    //Setters
    public void setId(String id){
        this.id = id;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public void setApellido(String apellido){
        this.apellido = apellido;
    }

    public void setAntiguedad(int antiguedad){
        this.antiguedad = antiguedad;
    }
    
}
