package Scene;

import database.ProfesorDAO;
import database.StudentDAO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {

    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // Razdvajanje komande od podataka
                String[] parts = inputLine.split(" ");
                String command = parts[0];

                switch (command) {
                    case "STUDENT_LOGIN":
                        String studentUsername = parts[1];
                        String studentPassword = parts[2];

                        // Pozivamo metodu za proveru u bazi podataka
                        boolean studentLoginSuccessful = proveriStudentaUBazi(studentUsername, studentPassword);

                        // Slanje odgovora servera
                        if (studentLoginSuccessful) {
                            out.println("OK"); // Uspešna prijava

                            // Isprintaj poruku da se student povezao
                            System.out.println("Student se povezao na server.");
                        } else {
                            out.println("FAIL"); // Neuspešna prijava
                        }
                        break;

                    case "PROFESOR_LOGIN":
                        String profesorUsername = parts[1];
                        String profesorPassword = parts[2];

                        // Pozivamo metodu za proveru u bazi podataka
                        boolean profesorLoginSuccessful = proveriProfesoraUBazi(profesorUsername, profesorPassword);

                        // Slanje odgovora servera
                        if (profesorLoginSuccessful) {
                            out.println("OK"); // Uspešna prijava

                            // Isprintaj poruku da se profesor povezao
                            System.out.println("Profesor se povezao na server.");
                        } else {
                            out.println("FAIL"); // Neuspešna prijava
                        }
                        break;

                    // Dodajte druge komande po potrebi
                    default:
                        out.println("UNKNOWN COMMAND");
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Greska prilikom obrade klijenta: " + e.getMessage());
        }
    }

    private boolean proveriStudentaUBazi(String username, String password) {
        return StudentDAO.proveraStudenta(username, password);
    }

    private boolean proveriProfesoraUBazi(String username, String password) {
        return ProfesorDAO.proveraProfesora(username, password);
    }
}
