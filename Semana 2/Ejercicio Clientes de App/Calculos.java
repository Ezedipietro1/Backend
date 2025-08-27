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

    public float calcularPuntuacionTodos() {
        float puntuacion = 0;
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
        }
        return puntuacion;
    }
}
