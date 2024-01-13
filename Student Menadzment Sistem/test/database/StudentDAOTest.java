/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import org.junit.Test;
import static org.junit.Assert.*;

public class StudentDAOTest {

    @Test
    public void testProveraStudentaIspravniPodaci() {
        String ispravnoKorisnickoIme = "student";
        String ispravnaLozinka = "student123";

        assertTrue(StudentDAO.proveraStudenta(ispravnoKorisnickoIme, ispravnaLozinka));
    }

    @Test
    public void testProveraStudentaNeispravniPodaci() {
        String neispravnoKorisnickoIme = "Nikola";
        String neispravnaLozinka = "123";

        assertFalse(StudentDAO.proveraStudenta(neispravnoKorisnickoIme, neispravnaLozinka));
    }

    @Test
    public void testProveraStudentaNullPodaci() {
        // Provera ponašanja metoda kada su korisničko ime i lozinka null
        assertFalse(StudentDAO.proveraStudenta(null, null));
    }

    @Test
    public void testProveraStudentaPrazniPodaci() {
        // Provera ponašanja metoda kada su korisničko ime i lozinka prazni stringovi
        assertFalse(StudentDAO.proveraStudenta("", ""));
    }
    
}
