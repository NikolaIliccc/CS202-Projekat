
package database;

import Entiteti.Odgovor;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

public class RezultatDAOTest {

    @Test
    public void testSvaPitanja() {
        List<Odgovor> sviOdgovori = RezultatDAO.SvaPitanja();
        assertNotNull(sviOdgovori);
        assertTrue(sviOdgovori.size() > 0);
    }

    @Test
    public void testRezultatiPoOdgovoru() {
        // Pretpostavljamo da postoji barem jedan odgovor u bazi
        List<Odgovor> sviOdgovori = RezultatDAO.SvaPitanja();
        assertTrue(sviOdgovori.size() > 0);

        String testOdgovor = sviOdgovori.get(0).getOdgovor();

        // Provera da li se odgovori dobro filtriraju po zadatom odgovoru
        List<Odgovor> rezultati = RezultatDAO.RezultatiPoOdgovoru(testOdgovor);
        assertNotNull(rezultati);
        assertTrue(rezultati.size() > 0);

        for (Odgovor odgovor : rezultati) {
            assertTrue(odgovor.getOdgovor().contains(testOdgovor));
        }
    }
}