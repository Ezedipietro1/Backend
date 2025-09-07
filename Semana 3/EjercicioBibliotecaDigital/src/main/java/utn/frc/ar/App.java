package utn.frc.ar;

public class App {
    public static void main(String[] args) {
        Bilbioteca biblioteca = new Bilbioteca();

        Libro libro1 = new Libro("1984", "George Orwell", 1949, "Distopía");
        Libro libro2 = new Libro("Cien Años de Soledad", "Gabriel García Márquez", 1967, "Realismo Mágico");
        Libro libro3 = new Libro("El Principito", "Antoine de Saint-Exupéry", 1943, "Fantasía");

        biblioteca.agregarLibro(libro1);
        biblioteca.agregarLibro(libro2);
        biblioteca.agregarLibro(libro3);

        System.out.println("Listado de libros:");
        biblioteca.listarLibros();

        System.out.println("\nLibros de Gabriel García Márquez:");
        for (Libro libro : biblioteca.buscarPorAutor("Gabriel García Márquez")) {
            System.out.println(libro);
        }

        System.out.println("\nEliminando '1984'...");
        biblioteca.eliminarPorTitulo("1984");
        System.out.println("Listado de libros después de la eliminación:");
        biblioteca.listarLibros();

        System.out.println("\nPromedio de antigüedad de los libros: " + biblioteca.calcularPromedioAntiguedad() + " años");
    }
}
