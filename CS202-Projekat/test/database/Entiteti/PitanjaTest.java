package Entiteti;

import exceptions.OdgovorException;
import exceptions.OpcijaException;
import exceptions.PrazanException;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PitanjaTest {

    @Test
    public void testJeJednaOdOpcija() throws OpcijaException, OdgovorException, PrazanException {
        Pitanja pitanje = new Pitanja("Koja od sledećih nije funkcija operativnog sistema?",
                "Data Encryption", "Memory Allocation", "File Management", "Data Encryption");

        assertTrue(pitanje.jeJednaOdOpcija("Data Encryption"));
        assertTrue(pitanje.jeJednaOdOpcija("Memory Allocation"));
        assertTrue(pitanje.jeJednaOdOpcija("File Management"));

        assertFalse(pitanje.jeJednaOdOpcija("Invalid Answer"));
    }

    @Test
    public void testGetIDPitanja() throws OpcijaException, OdgovorException, PrazanException {
        Pitanja pitanje = new Pitanja("Koja od sledećih nije funkcija operativnog sistema?",
                "Data Encryption", "Memory Allocation", "File Management", "Data Encryption");

        assertEquals(0, pitanje.getIDPitanja());

        pitanje.setIDPitanja(1);
        assertEquals(1, pitanje.getIDPitanja());
    }

}
