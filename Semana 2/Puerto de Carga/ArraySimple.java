import java.util.NoSuchElementException;

public class ArraySimple {
    private Barco[] items;
    private int posicion; //Por default va a ser 0

    // constructor
    public ArraySimple() {
        this(10); // tamaño por defecto. el this solo llama a un constructor de la clase
    }

    public ArraySimple(int size) {
        this.items = new Barco[size];
        this.posicion = 0;
    } 

    // métodos
    public int size() {
        return this.items.length; // devuelve el tamaño del array, pq como es privado el test no lo puede ver
    }

    public Barco get(int index) {
        return this.items[index]; // devuelve el objeto que está en la posición index
    }

    public void set(Barco item,int posicion) throws IllegalArgumentException{ 
        if (posicion >= this.items.length){ 
            throw new IllegalArgumentException("No se puede agregar más items, el array está lleno");
        }
        this.items[posicion] = item;
        this.posicion++;
    }

    public void append(Barco item) {
        if (posicion >= this.items.length){ 
           this.asegurarCapacidad();
        }
        this.items[posicion] = item;
        this.posicion++;
    }

    private void asegurarCapacidad() {    // duplica el tamaño del array
        int nuevoTamanio = this.items.length * 2;
        Barco[] nuevoArray = new Barco[nuevoTamanio];

        // copia los elementos del array viejo al nuevo (vector viejo,  desde donde, vector nuevo, desde donde, cantidad de elementos)
        System.arraycopy(this.items, 0, nuevoArray, 0, this.items.length);
        this.items = nuevoArray;
    }

    public Barco remove(int posicion){
        if (posicion < 0 || posicion >= this.items.length) {
            throw new NoSuchElementException("Posición inválida");
        }
        Barco deleted = this.items[posicion];
        this.items[posicion] = null;
        this.mantenerIntegridad(posicion);
        return deleted;
    }

    public void mantenerIntegridad(int posicion){
        Barco[] nuevoArray = new Barco[this.size() - 1];
        System.arraycopy(this.items, 0, nuevoArray, 0, posicion);
        System.arraycopy(this.items, posicion + 1, nuevoArray, posicion, this.size() - 1);
        this.items = nuevoArray;
        this.posicion--; // Decrementa el contador de elementos
    }


}