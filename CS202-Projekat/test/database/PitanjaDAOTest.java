package database;

import Entiteti.Pitanja;
import org.junit.Test;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PitanjaDAOTest {

    @Test
    public void testGetQuestionById() throws SQLException {
        // Mock-ovanje objekata za bazu podataka
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        // Postavljanje mock-ova u DAO
        PitanjaDAO pitanjaDAO = new PitanjaDAO(mockConnection);

        // ID pitanja koje želimo dohvatiti
        int testQuestionId = 1;

        // Postavljanje očekivanih rezultata u ResultSet (simulacija)
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);  // Samo jedan rezultat
        when(mockResultSet.getInt("IDPitanja")).thenReturn(testQuestionId);
        when(mockResultSet.getString("tekstpitanja")).thenReturn("Koji jezik se često koristi za frontend web razvoj?");
        when(mockResultSet.getString("opcija1")).thenReturn("Java");
        when(mockResultSet.getString("opcija2")).thenReturn("Python");
        when(mockResultSet.getString("opcija3")).thenReturn("JavaScript");
        when(mockResultSet.getString("odgovor")).thenReturn("JavaScript");

        // Postavljanje mock-ova za izvršavanje upita (simulacija)
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Dohvatanje pitanja po ID-u
        Pitanja dohvacenoPitanje = pitanjaDAO.getQuestionById(testQuestionId);

        // Provera rezultata
        assertNotNull(dohvacenoPitanje);
        assertEquals(testQuestionId, dohvacenoPitanje.getIDPitanja());
        assertEquals("Koji jezik se često koristi za frontend web razvoj?", dohvacenoPitanje.getTekstPitanja());
        assertEquals("Java", dohvacenoPitanje.getOpcija1());
        assertEquals("Python", dohvacenoPitanje.getOpcija2());
        assertEquals("JavaScript", dohvacenoPitanje.getOpcija3());
        assertEquals("JavaScript", dohvacenoPitanje.getOdgovor());
    }
}
