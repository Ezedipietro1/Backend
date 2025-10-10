package utnfc.isi.back;

import org.junit.jupiter.api.Test;
import utnfc.isi.back.domain.BoardGame;

import static org.junit.jupiter.api.Assertions.*;

public class BoardGameLombokTest {

    @Test
    void settersAndGettersShouldWork() {
        BoardGame bg = new BoardGame();
        bg.setName("Catan");
        bg.setYearPublished(1995);
        bg.setMinPlayers(3);
        bg.setMaxPlayers(4);

        assertEquals("Catan", bg.getName());
        assertEquals(1995, bg.getYearPublished());
        assertEquals(3, bg.getMinPlayers());
        assertEquals(4, bg.getMaxPlayers());
    }
}
