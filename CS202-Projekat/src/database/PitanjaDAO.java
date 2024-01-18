package database;

import Entiteti.Pitanja;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PitanjaDAO {

    private Connection connection;

    public PitanjaDAO(Connection connection) {
        this.connection = connection;
    }

    public static void dodajPitanje(Pitanja pitanja) {
        String sql = "INSERT INTO pitanja (tekstpitanja, opcija1, opcija2, opcija3, odgovor) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnector.connect();
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, pitanja.getTekstPitanja());
            preparedStatement.setString(2, pitanja.getOpcija1());
            preparedStatement.setString(3, pitanja.getOpcija2());
            preparedStatement.setString(4, pitanja.getOpcija3());
            preparedStatement.setString(5, pitanja.getOdgovor());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating question failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int idPitanja = generatedKeys.getInt(1);
                    System.out.println("Pitanje je uspešno dodato. ID pitanja: " + idPitanja);
                } else {
                    throw new SQLException("Creating question failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void azurirajPitanje(int idPitanja, Pitanja updatedPitanja) {
        String sql = "UPDATE pitanja SET tekstpitanja=?, opcija1=?, opcija2=?, opcija3=?, odgovor=? WHERE IDPitanja=?";

        try (Connection connection = DatabaseConnector.connect();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, updatedPitanja.getTekstPitanja());
            preparedStatement.setString(2, updatedPitanja.getOpcija1());
            preparedStatement.setString(3, updatedPitanja.getOpcija2());
            preparedStatement.setString(4, updatedPitanja.getOpcija3());
            preparedStatement.setString(5, updatedPitanja.getOdgovor());
            preparedStatement.setInt(6, idPitanja);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Pitanja getQuestionById(int questionId) {
        String sql = "SELECT * FROM pitanja WHERE IDPitanja = ?";
        try (Connection connection = DatabaseConnector.connect();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, questionId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return IzdvojiPitanjeIzRezultata(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

      public static List<Pitanja> SvaPitanja() {
        List<Pitanja> pitanja = new ArrayList<>();

        String sql = "SELECT * FROM pitanja";

        try (Connection connection = DatabaseConnector.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Pitanja pitanje = IzdvojiPitanjeIzRezultata(resultSet);
                pitanja.add(pitanje);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pitanja;
    }

    // Retrieve shuffled questions
    public static List<Pitanja> SvaPitanjaIzmesana() {
        List<Pitanja> svapitanja = SvaPitanja();
        List<Pitanja> izmesanapitanja = new ArrayList<>(svapitanja);

        // Shuffle the questions
        Collections.shuffle(izmesanapitanja);

        return izmesanapitanja;
    }
    private static Pitanja IzdvojiPitanjeIzRezultata(ResultSet resultSet) throws SQLException {
        Pitanja pitanje = new Pitanja();
        pitanje.setIDPitanja(resultSet.getInt("IDPitanja"));
        pitanje.setTekstPitanja(resultSet.getString("tekstpitanja"));
        pitanje.setOpcija1(resultSet.getString("opcija1"));
        pitanje.setOpcija2(resultSet.getString("opcija2"));
        pitanje.setOpcija3(resultSet.getString("opcija3"));
        pitanje.setOdgovor(resultSet.getString("odgovor"));
        return pitanje;
    }

    public static boolean izbrisiPitanjePoId(int questionId) {
        String sql = "DELETE FROM pitanja WHERE IDPitanja = ?";

        try (Connection connection = DatabaseConnector.connect();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, questionId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                return true;  // Pitanje je uspešno obrisano
            } else {
                return false;  // Pitanje nije pronađeno sa datim ID-om
            }
        } catch (SQLException e) {
            return false;  // Došlo je do greške prilikom brisanja pitanja
        }
    }
}
