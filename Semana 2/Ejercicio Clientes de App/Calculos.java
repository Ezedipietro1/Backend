public class Calculos {
    private Cliente[] arrayClientes;

    public Calculos(Cliente[] arrayClientes) {
        this.arrayClientes = arrayClientes;
    }

    public int calcularMayoresDe(int edad) { 
        int contador = 0;
        for (int i = 0; i < arrayClientes.length; i++) {
            if (arrayClientes[i].getEdad() > edad) {
                contador ++;
            }
        }
        return contador;
    }

    public int totalPosteos() {
        int sumador = 0;
        for (int i = 0; i < arrayClientes.length; i++) {
            sumador += arrayClientes[i].getCantidadPosteos();
        }
        return sumador;
    }

    public float[] calcularPuntuacionTodos() {
        float puntuacion = 0;
        float[] arrayPuntos = new float[arrayClientes.length];
        for (int i = 0; i < arrayClientes.length; i++) {
            if (arrayClientes[i].getEdad() > 25 ) { 
                puntuacion = (arrayClientes[i].getHorasEnPlataforma() * 2);
            }
            else {
                puntuacion = (arrayClientes[i].getHorasEnPlataforma() * 3);
            }
            if (arrayClientes[i].isVerificado()) {
                puntuacion += 20;
            }
            arrayPuntos[i] = puntuacion;    
        }
        return arrayPuntos;
    }

    public float calcularPuntuacion(Cliente cliente) {
        float puntuacion = 0;
        if (cliente.getEdad() > 25 ) { 
            puntuacion = (cliente.getHorasEnPlataforma() * 2);
        }
        else {
            puntuacion = (cliente.getHorasEnPlataforma() * 3);
        }
        if (cliente.isVerificado()) {
            puntuacion += 20;
        }
        return puntuacion;
    }
}
