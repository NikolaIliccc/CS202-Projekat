package database;

import Entiteti.Student;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDAO {

    public static boolean proveraStudenta(String username, String password) {
        String sql = "SELECT * FROM studentlogin WHERE username = ? AND password = ?";

        try (Connection connection = DatabaseConnector.connect();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void sacuvajStudentInfo(Student student) {
        String sql = "INSERT INTO student (ime, prezime, jmbg, adresa, telefon) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnector.connect();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, student.getName());
            preparedStatement.setString(2, student.getSurname());
            preparedStatement.setString(3, student.getJmbg());
            preparedStatement.setString(4, student.getAddress());
            preparedStatement.setString(5, student.getMobile());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
