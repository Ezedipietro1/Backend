public class Mascota {
    private String nombre;
    private String estado;
    private int energia;    
    private int humor;
    private boolean vivo; 
    private int contIngesta; // Contador de ingestas
    private int contActividad; // Contador de actividades

    // Constructor
    public Mascota(String nombre, int energia, int humor) {
        this.nombre = nombre;

        if (energia < 0 || energia > 100) {
            throw new IllegalArgumentException("La energía debe estar entre 0 y 100.");
        }
        else {
            this.energia = energia;
        }
        if (humor < 1 || humor > 5) {
            throw new IllegalArgumentException("El humor debe estar entre 1 y 5.");
        }
        else {
            this.humor = humor;
        }
        
        this.estado = "Despierto";
        this.vivo = true;
    }

    // Getters
    public String getNombre() {
        return this.nombre;
    }

    public String getEstado() {
        return this.estado;
    }   

    public int getEnergia() {
        return this.energia;
    }

    public String getHumor() {
        if (this.humor == 1) {
            return "Muy enojado";
        }
        else if (this.humor == 2) {
            return "Enojado";
        }
        else if (this.humor == 3) {
            return "Neutral";
        }
        else if (this.humor == 4) {
            return "Contento";
        }
        else {
            return "Chocho";
        }
    }

    public String isVivo() {
        if (this.vivo) {
            return "Vivo";
        }  
        else {
            return "Muerto";
        }
    }

    // Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setEnergia(int energia) {
        if (energia <= 0 ) {
            this.morir();;
        }
        else if (energia > 100) {
            this.energia = 100;
        }
        else {
            this.energia = energia;
        }
    }

    public void setHumor(int humor) {
        if (humor < 1) {
            this.humor = 0;
            this.dormir();
        }
        else if (humor > 5) {
            this.humor = 5;
        }
        else {
            this.humor = humor;
        }
    }

    public void setVivo(boolean vivo) {
        this.vivo = vivo;
    }

    @Override
    public String toString() {
        if (this.vivo == false) {
            return this.nombre + " ha muerto.";
        }
        return "Nombre: " + this.nombre + "\n" +
               "Estado: " + this.estado + "\n" +
               "Energía: " + this.energia + "\n" +
               "Humor: " + this.getHumor() + "\n" +
               "Estado de vida: " + this.isVivo();
    }

    // Métodos adicionales
    // Método ingesta
    public boolean comer() {
        if (this.vivo == false) {
            System.out.println("La mascota está muerta y no puede comer.");
            return false;
        }
        this.setEnergia((int)(this.energia + this.energia * 0.1));
        this.setHumor(this.humor + 1);
        System.out.println(this.nombre + " ha comido y gano " + "\n" +
               "+" + (this.energia * 0.1) + " energia" + "\n" +
               "+ 1 humor" + "\n");
        this.contIngesta++;
        this.contActividad = 0;
        this.controlIngesta();
        return true;
    }

    public boolean beber() {
        if (this.vivo == false) {
            System.out.println("La mascota está muerta y no puede beber.");
            return false;
        }
        this.setEnergia((int)(this.energia + this.energia * 0.05));
        this.setHumor(this.humor + 1);
        System.out.println(this.nombre + " ha bebido y gano " + "\n" +
               "+" + (this.energia * 0.05) + " energia" + "\n" +
               "+ 1 humor" + "\n");
        this.contIngesta++;
        this.contActividad = 0;
        this.controlIngesta();
        return true;
    }

    // Método actividad
    public boolean correr() {
        if (this.vivo == false) {
            System.out.println("La mascota está muerta y no puede correr.");
            return false;
        }
        this.setEnergia((int)(this.energia - this.energia * 0.35));
        this.setHumor(this.humor - 2);
        System.out.println(this.nombre + " ha corrido " + "\n" +
               "-" + (this.energia * 0.35) + " energia" + "\n" +
               "- 2 humor" + "\n");
        this.contActividad++;
        this.contIngesta = 0;
        this.controlActividad();
        return true;
    }

    public boolean saltar() {
        if (this.vivo == false) {
            System.out.println("La mascota está muerta y no puede saltar.");
            return false;
        }
        this.setEnergia((int)(this.energia - this.energia * 0.15));
        this.setHumor(this.humor - 2);
        System.out.println(this.nombre + " ha corrido " + "\n" +
               "-" + (this.energia * 0.15) + " energia" + "\n" +
               "- 2 humor" + "\n");
        this.contActividad++;
        this.contIngesta = 0;
        this.controlActividad();
        return true;
    }

    // Método descanso
    public boolean dormir() {
        if (this.vivo == false) {
            System.out.println("La mascota está muerta y no puede dormir.");
            return false;
        }
        this.setEnergia((int)(this.energia + this.energia * 0.25));
        this.setHumor(this.humor + 2);
        System.out.println(this.nombre + " se durmio " + "\n" +
               "+" + (this.energia * 0.25) + " energia" + "\n" +
               "+ 2 humor" + "\n");
        this.setEstado("Dormido");
        return true;
    }

    public boolean despertar() {
        if (this.vivo == false) {
            System.out.println("La mascota está muerta y no puede despertar.");
            return false;
        }
        System.out.println(this.nombre + " se desperto " + "\n" +
               "- 1 humor" + "\n");
        this.setEstado("Despierto");
        this.setHumor(this.humor - 1);
        return true;
    }

    // Método muerte
    public void morir() {
        this.setVivo(false);
        this.setEstado("Muerto");
    }

    // Método control
    public void controlIngesta() {
        if (this.contIngesta >= 3 && this.contIngesta < 5) {
            this.setHumor(this.humor - 1);
        }
        else if (this.contIngesta == 5) {
            this.morir();
            System.out.println(this.getNombre() + " ha muerto por empacho.");
        }
    }

    public void controlActividad() {
        if (this.contActividad == 3) {
            this.dormir();
        }
    }
}
