package ClientServer;

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
                    case "LOGOUT":
                        out.println("LOGOUT_OK");
                        break;

                    case "AZURIRAJ_PITANJE":
                        int azuriranoPitanjeID = Integer.parseInt(parts[1]);
                        String azuriranoPitanjeText = inputLine.substring(inputLine.indexOf(" ", inputLine.indexOf(" ") + 1) + 1);

                        System.out.println("Profesor je azurirao pitanje br " + azuriranoPitanjeID + " sa tekstom: " + "\n" + azuriranoPitanjeText);
                        break;
                    case "NOVO_PITANJE":
                        String novoPitanjeTekst = inputLine.substring(inputLine.indexOf(" ", inputLine.indexOf(" ") + 1) + 1);
                        System.out.println("Tekst Pitanja:" + novoPitanjeTekst);
                        break;
                    case "OBRISI_PITANJE":
                        int deletedQuestionId = Integer.parseInt(parts[1]);
                        System.out.println("Profesor je obrisao pitanje br " + deletedQuestionId);
                        break;
                    case "STUDENT_ODGOVOR":
                        String studentName = parts[1];
                        String studentSurname = parts[2];
                        String question = inputLine.substring(inputLine.indexOf("Pitanje:") + 8, inputLine.indexOf("Odgovor:")).trim();
                        String answer = inputLine.substring(inputLine.indexOf("Odgovor:") + 8).trim();

                        System.out.println(studentName + " " + studentSurname + " je odgovorio na pitanje: " + question + " sa odgovorom: " + answer);
                        break;

                    case "STUDENT_PODACI":
                        String studentuser = parts[1];
                        String ime = parts[2];
                        String prezime = parts[3];
                        String jmbg = parts[4];

                        // Ekstraktovanje adrese i telefona
                        StringBuilder addressBuilder = new StringBuilder();
                        for (int i = 5; i < parts.length - 1; i++) {
                            addressBuilder.append(parts[i]).append(" ");
                        }
                        String adresa = addressBuilder.toString().trim();

                        String telefon = parts[parts.length - 1]; // Uzimamo zadnji za telefon

                        System.out.println("Podaci od studenta " + studentuser + ":");
                        System.out.println("Ime: " + ime);
                        System.out.println("Prezime: " + prezime);
                        System.out.println("JMBG: " + jmbg);
                        System.out.println("Adresa: " + adresa);
                        System.out.println("Telefon: " + telefon);
                        break;

                    case "STUDENT_LOGIN":
                        String studentUsername = parts[1];
                        String studentPassword = parts[2];

                        boolean studentLoginSuccessful = proveriStudentaUBazi(studentUsername, studentPassword);

                        if (studentLoginSuccessful) {
                            out.println("OK"); // Uspešna prijava

                            System.out.println("Student se povezao na server. Adresa: " + clientSocket.getInetAddress());
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

                            System.out.println("Profesor se povezao na server. Adresa: " + clientSocket.getInetAddress());
                        } else {
                            out.println("FAIL"); // Neuspešna prijava
                        }
                        break;

                    default:
                        out.println("NEPOZNATA_KOMANDA");
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Greska prilikom obrade klijenta!");
        }
    }

    private boolean proveriStudentaUBazi(String username, String password) {
        return StudentDAO.proveraStudenta(username, password);
    }

    private boolean proveriProfesoraUBazi(String username, String password) {
        return ProfesorDAO.proveraProfesora(username, password);
    }
}
