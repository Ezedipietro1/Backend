import org.junit.jupiter.api.Test;

public class BarcoTest {

    @Test
    public void SeCreaExitosamenteUnBarco(){
        // Arrange -> Toda la logica que me permite aislar a la minima unidad
        String cap = "Pepe Popeye";
        int matricula = 1234;
        int darsena = 1;
        double carga = 10;
        
        // Act -> Ejecutar el metodo
        Barcos barco = new Barco(cap, matricula, darsena, carga);

        // Assert -> Comprobar que mi metodo se ejecuto con los resultados esperados
        assertNotNull(barco);
        assertEqual(matricula, barco.getMatricula);
    }
}