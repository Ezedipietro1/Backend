package utn.frc.ar;

import java.util.ArrayList;
import java.util.Iterator;
import java.time.LocalDate;

public class Bilbioteca {
    private ArrayList<Libro> libros;

    public Bilbioteca() {
        this.libros = new ArrayList<Libro>();
    }

    public void agregarLibro(Libro libro) {
        this.libros.add(libro);
    }

    public void listarLibros() {
        for (Libro libro : this.libros) {
            System.out.println(libro);
        }
    }

    public ArrayList<Libro> buscarPorAutor(String autor) {
        ArrayList<Libro> resultados = new ArrayList<>();
        Iterator<Libro> iterator = this.libros.iterator();

        while (iterator.hasNext()) {
            Libro libro = iterator.next();
            if (libro.getAutor() == autor) {
                resultados.add(libro);
            }
        }

        return resultados;
    }

    public void eliminarPorTitulo(String titulo) {
        for (Libro libro : this.libros) {
            if (libro.getTitulo() == titulo) {
                libros.remove(libro);
                break; // Salir del bucle después de eliminar el libro
            }
        }
    }

    public double calcularPromedioAntiguedad() {
        int sumaAnios = 0;
        int cantidadLibros = this.libros.size();
        for (Libro libro : this.libros) {
            sumaAnios += LocalDate.now().getYear() - libro.getAnioPublicacion();
        }
        double promedio = (double) sumaAnios / cantidadLibros;
        return promedio;
    }
}
