package database;

import Entiteti.Odgovor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RezultatDAO {
    

    public static void sacuvajRezultat(Odgovor odgovor) {
        String sql = "INSERT INTO odgovor (ime, prezime, pitanje, odgovoreno) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnector.connect();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, odgovor.getIme());
            preparedStatement.setString(2, odgovor.getPrezime());
            preparedStatement.setString(3, odgovor.getPitanje());
            preparedStatement.setString(4, odgovor.getOdgovor());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Odgovor> SvaPitanja() {
        List<Odgovor> answers = new ArrayList<>();

        String sql = "SELECT * FROM odgovor";

        try (Connection connection = DatabaseConnector.connect();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Odgovor answer = IzdvojiOdgovoreIzRezultata(resultSet);
                answers.add(answer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return answers;
    }

    private static Odgovor IzdvojiOdgovoreIzRezultata(ResultSet resultSet) throws SQLException {
        Odgovor answer = new Odgovor();
        answer.setOdgovorID(resultSet.getInt("Odgovor_ID"));
        answer.setIme(resultSet.getString("ime"));
        answer.setPrezime(resultSet.getString("prezime"));
        answer.setPitanje(resultSet.getString("pitanje"));
        answer.setOdgovor(resultSet.getString("odgovoreno"));
        return answer;
    }

    public static List<Odgovor> RezultatiPoOdgovoru(String answer) {
        String sql = "SELECT * FROM odgovor WHERE odgovoreno LIKE ?";
        List<Odgovor> results = new ArrayList<>();

        try (Connection connection = DatabaseConnector.connect();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + answer + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("Odgovor_ID");
                String ime = resultSet.getString("ime");
                String prezime = resultSet.getString("prezime");
                String pitanje = resultSet.getString("pitanje");
                String odgovoreno = resultSet.getString("odgovoreno");

                Odgovor odgovor = new Odgovor(ime, prezime, pitanje, odgovoreno);
                odgovor.setOdgovorID(id);
                results.add(odgovor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}
