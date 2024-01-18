package Scene;

import Entiteti.Student;
import database.StudentDAO;
import exceptions.AdresaException;
import exceptions.ImeException;
import exceptions.JMBGException;
import exceptions.PrezimeException;
import exceptions.TelefonException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class StudentInfo extends Application {

    private TextField imeField;
    private TextField prezimeField;
    private TextField jmbgField;
    private TextField adresaField;
    private TextField telefonField;
    private String username;
    private Background background;
    private Stage primaryStage;

    public StudentInfo(String username, Background background) {
        this.username = username;
        this.background = background;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Student Info Entry");
        Label welcomeLabel = new Label("Unesite vase podatke:");
        welcomeLabel.setStyle("-fx-font-size: 25px; -fx-font-weight: bold; -fx-text-fill: white;");

        imeField = createTextField("Ime");
        prezimeField = createTextField("Prezime");
        jmbgField = createTextField("JMBG");
        adresaField = createTextField("Adresa");
        telefonField = createTextField("Telefon");

        Button saveButton = new Button("Sacuvaj");
        saveButton.setOnAction(e -> {
            try {
                sacuvajStudentInfo();
            } catch (ImeException | PrezimeException | JMBGException | AdresaException | TelefonException ex) {
                prikaziAlert("Greska!", ex.getMessage());
            }
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        layout.setBackground(background);

        layout.getChildren().addAll(welcomeLabel, imeField, prezimeField, jmbgField, adresaField, telefonField, saveButton);

        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 710, 450);

        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMaxWidth(710);
        primaryStage.setMaxHeight(450);
        primaryStage.setMinWidth(710);
        primaryStage.setMinHeight(450);
    }

    private TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setMaxWidth(200);
        return textField;
    }

    private void sacuvajStudentInfo() throws ImeException, PrezimeException, JMBGException, AdresaException, TelefonException {
        String ime = imeField.getText();
        String prezime = prezimeField.getText();
        String jmbg = jmbgField.getText();
        String adresa = adresaField.getText();
        String telefon = telefonField.getText();

        if (validateInput(ime, prezime, jmbg, adresa, telefon)) {
            Student student = new Student(ime, prezime, jmbg, adresa, telefon);
            StudentDAO.sacuvajStudentInfo(student);

            prikaziUspesanLogin();
            imeField.clear();
            prezimeField.clear();
            jmbgField.clear();
            adresaField.clear();
            telefonField.clear();

            posaljiPodatkeNaServer(ime, prezime, jmbg, adresa, telefon);
            Stage currentStage = (Stage) imeField.getScene().getWindow();
            currentStage.close();

            prikaziInstrukcije(student);
        } else {
            PrikaziNeuspesanLogin();
        }
    }

    private void posaljiPodatkeNaServer(String ime, String prezime, String jmbg, String adresa, String telefon) {
        try (Socket socket = new Socket("localhost", 12335)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("STUDENT_PODACI " + username + " " + ime + " " + prezime + " " + jmbg + " " + adresa + " " + telefon);
        } catch (IOException e) {
            System.err.println("Neuspesno konektovanje na server: " + e.getMessage());
        }
    }

    private void prikaziInstrukcije(Student student) {
        String instructionsText = "Uputstva za kviz:\n\n1. Odgovorite na sva pitanja.\n2. Kliknite na dugme 'Započni kviz' kako biste počeli sa kvizom.\n3.Student ne sme koristiti bilo kakvo sredstvo za pomaganje (svesku sa zadacima, internet...).\n"
                + "4.Student mora da uradi 10 pitanja sa više ponuđenih odgovora za manje od 10 minuta.\n5.Student mora odgovoriti na svako pitanje zato što se ne može vraćati na pitanja."
                + "\n\nBroj pitanja: 10\nVreme na testu: 10min\nSrećno!!!";

        VBox instructionsLayout = new VBox(10);
        instructionsLayout.setPadding(new Insets(10, 10, 10, 10));
        instructionsLayout.setBackground(background);

        Label instructionsLabel = new Label(instructionsText);
        instructionsLabel.setStyle("-fx-font-size: 16;");

        Button startQuizButton = new Button("Započni kviz");
        startQuizButton.setOnAction(e -> ZapocniKviz(student));

        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().add(startQuizButton);
        buttonLayout.setAlignment(Pos.CENTER);

        instructionsLayout.getChildren().addAll(instructionsLabel, buttonLayout);
        Scene instructionsScene = new Scene(instructionsLayout, 710, 450);
        primaryStage.setScene(instructionsScene);
        primaryStage.show();
    }

    private void ZapocniKviz(Student student) {
        try {
            String username = student.getName();

            Kviz quizApp = new Kviz(background, student.getName(), student.getSurname());
            quizApp.start(primaryStage);

        } catch (Exception e) {
            e.printStackTrace();
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
        alert.setTitle("Save Successful");
        alert.setHeaderText(null);
        alert.setContentText("Uspesno ste sacuvali podatke!");
        alert.showAndWait();
    }

    private void PrikaziNeuspesanLogin() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Save Failed");
        alert.setHeaderText(null);
        alert.setContentText("Molimo unesite sve podatke pre cuvanja.");
        alert.showAndWait();
    }

    private void prikaziAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
