/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import static org.junit.Assert.*;
import org.junit.Test;

public class ProfesorDAOTest {

    @Test
    public void testProveraProfesoraIspravniPodaci() {
        // Unesite stvarne ispravne podatke za profesora
        String ispravnoKorisnickoIme = "NikolaIlic";
        String ispravnaLozinka = "Nikola123";

        assertTrue(ProfesorDAO.proveraProfesora(ispravnoKorisnickoIme, ispravnaLozinka));
    }

    @Test
    public void testProveraProfesoraNeispravniPodaci() {
        // Unesite stvarne neispravne podatke za profesora
        String neispravnoKorisnickoIme = "Nikola";
        String neispravnaLozinka = "123";

        assertFalse(ProfesorDAO.proveraProfesora(neispravnoKorisnickoIme, neispravnaLozinka));
    }

    @Test
    public void testProveraProfesoraNullPodaci() {
        // Provera ponašanja metoda kada su korisničko ime i lozinka null
        assertFalse(ProfesorDAO.proveraProfesora(null, null));
    }

    @Test
    public void testProveraProfesoraPrazniPodaci() {
        // Provera ponašanja metoda kada su korisničko ime i lozinka prazni stringovi
        assertFalse(ProfesorDAO.proveraProfesora("", ""));
    }
}
