package Scene;

import Main.Main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StudentLogin extends Application {

    private TextField usernameField;
    private PasswordField passwordField;
    private Background background;
    private Scene pocetnaScena;

    public StudentLogin(Background background, Scene pocetnaScena) {
        this.background = background;
        this.pocetnaScena = pocetnaScena;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Student Login");

        Label welcomeLabel = new Label("Student Login:");
        welcomeLabel.setStyle("-fx-font-size: 25px; -fx-font-weight: bold; -fx-text-fill: white;");

        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMinWidth(200);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMinWidth(200);

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> Login(usernameField.getText(), passwordField.getText(), primaryStage));

        Button backButton = new Button("Nazad na Pocetnu");
        backButton.setOnAction(e -> {
            // Vratite se na pocetnu scenu
            primaryStage.setScene(pocetnaScena);
            primaryStage.show();
        });

        usernameField.setOnKeyPressed(event -> handleEnterKeyPress(event.getCode(), primaryStage));
        passwordField.setOnKeyPressed(event -> handleEnterKeyPress(event.getCode(), primaryStage));

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setBackground(background);

        HBox usernameBox = new HBox(10);
        usernameBox.getChildren().addAll(usernameField);
        usernameBox.setAlignment(Pos.CENTER);

        HBox passwordBox = new HBox(10);
        passwordBox.getChildren().addAll(passwordField);
        passwordBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(welcomeLabel, usernameBox, passwordBox, loginButton, backButton);

        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 710, 400);

        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMaxWidth(710);
        primaryStage.setMaxHeight(450);
        primaryStage.setMinWidth(710);
        primaryStage.setMinHeight(450);

    }

    private void Login(String username, String password, Stage primaryStage) {
        if (validateInput(username, password)) {
            try (Socket socket = new Socket("localhost", 12335)) {
                // Slanje informacija serveru
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("STUDENT_LOGIN " + username + " " + password);

                // Čekanje odgovora servera
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = in.readLine();
                System.out.println("Odgovor servera: " + response);

                // Ovde možete obraditi odgovor servera
                if ("OK".equals(response)) {
                    prikaziUspesanLogin();
                    openStudentInfo(primaryStage,username);
                } else if ("FAIL".equals(response)) {
                    PrikaziNeuspesanLogin();
                } else {
                    System.out.println("Nepoznat odgovor od servera: " + response);
                }
            } catch (IOException e) {
                PrikaziNeuspesanLogin();
                System.err.println("Greska prilikom povezivanja sa serverom: " + e.getMessage());
            }
        } else {
            PrikaziNeuspesanLogin();
        }
    }

    private boolean validateInput(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void prikaziUspesanLogin() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Login Successful");
        alert.setHeaderText(null);
        alert.setContentText("Login uspesan!");
        alert.showAndWait();
    }

    private void PrikaziNeuspesanLogin() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Failed");
        alert.setHeaderText(null);
        alert.setContentText("Pogresan username ili password. Molimo pokusajte ponovo.");
        alert.showAndWait();
    }

    private void handleEnterKeyPress(KeyCode keyCode, Stage primaryStage) {
        if (keyCode == KeyCode.ENTER) {
            Login(usernameField.getText(), passwordField.getText(), primaryStage);
        }
    }

    private void openStudentInfo(Stage primaryStage, String username) {
        primaryStage.close();
        StudentInfo studentInfoApp = new StudentInfo(username, background);
        Stage infoStage = new Stage();
        studentInfoApp.start(infoStage);
    }
}
