package Scene;

import Entiteti.Odgovor;
import Entiteti.Pitanja;
import database.PitanjaDAO;
import database.RezultatDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import javafx.scene.paint.Color;

public class Kviz extends Application {

    private Label timerLabel;
    private int remainingTime = 600;
    private Background background;
    private List<Pitanja> pitanja;
    private int trenutnoPitanjeIndex = 0;
    private VBox centerLayout;
    private String studentIme;
    private String studentPrezime;
    private String currentDate;
    private ToggleGroup toggleGroup;
    private RezultatDAO rezultatDAO = new RezultatDAO();
    private boolean odgovoreno = false;
    private RadioButton selectedRadioButton;

    public Kviz(Background background, String studentIme, String studentPrezime) {
        this.background = background;
        this.pitanja = PitanjaDAO.SvaPitanjaIzmesana();
        this.studentIme = studentIme;
        this.studentPrezime = studentPrezime;
        this.currentDate = TrenutnoVreme();

    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Kviz");

        timerLabel = new Label("Vreme: 10:00");
        Button endQuizButton = new Button("Završi kviz");
        endQuizButton.setOnAction(e -> {
            sacuvajPitanje();
            ZavrsiKviz();
        });

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setBackground(background);

        HBox timerBox = new HBox(10);
        timerBox.getChildren().addAll(timerLabel);
        timerBox.setAlignment(Pos.TOP_RIGHT);
        layout.setTop(timerBox);

        centerLayout = new VBox(10);
        centerLayout.setAlignment(Pos.CENTER);
        layout.setCenter(centerLayout);

        HBox buttonsBox = new HBox(10);
        buttonsBox.getChildren().addAll(endQuizButton);
        buttonsBox.setAlignment(Pos.BOTTOM_CENTER);
        layout.setBottom(buttonsBox);

        Scene scene = new Scene(layout, 710, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMaxWidth(710);
        primaryStage.setMaxHeight(450);
        primaryStage.setMinWidth(710);
        primaryStage.setMinHeight(450);
        startTimer();

        PrikaziPitanje();
    }

    public void PrikaziPitanje() {
        odgovoreno = false; // Resetujemo status odgovora za novo pitanje

        if (trenutnoPitanjeIndex < 10 && trenutnoPitanjeIndex < pitanja.size()) {
            Pitanja trenutnoPitanje = pitanja.get(trenutnoPitanjeIndex);

            Label pitanjeLabel = new Label(trenutnoPitanje.getTekstPitanja());
            pitanjeLabel.setTextFill(Color.WHITE);

            toggleGroup = ToggleGroupForQuestion(trenutnoPitanje);

            RadioButton opcija1 = new RadioButton(trenutnoPitanje.getOpcija1());
            opcija1.setTextFill(Color.WHITE);
            RadioButton opcija2 = new RadioButton(trenutnoPitanje.getOpcija2());
            opcija2.setTextFill(Color.WHITE);
            RadioButton opcija3 = new RadioButton(trenutnoPitanje.getOpcija3());
            opcija3.setTextFill(Color.WHITE);

            opcija1.setToggleGroup(toggleGroup);
            opcija2.setToggleGroup(toggleGroup);
            opcija3.setToggleGroup(toggleGroup);

            VBox radioButtonsLayout = new VBox(5, opcija1, opcija2, opcija3);
            radioButtonsLayout.setAlignment(Pos.CENTER);

            Button nextButton = new Button("Next");
            nextButton.setOnAction(e -> PrikaziSledecePitanje());
            HBox buttonsLayout = new HBox(10, nextButton);
            buttonsLayout.setAlignment(Pos.CENTER);

            VBox userDataLayout = new VBox(5);
            Label imeLabel = new Label("Ime: " + studentIme);
            imeLabel.setTextFill(Color.WHITE);
            Label prezimeLabel = new Label("Prezime: " + studentPrezime);
            prezimeLabel.setTextFill(Color.WHITE);
            Label datumLabel = new Label("Datum: " + currentDate);
            datumLabel.setTextFill(Color.WHITE);
            userDataLayout.getChildren().addAll(imeLabel, prezimeLabel, datumLabel);
            userDataLayout.setAlignment(Pos.CENTER_LEFT);

            VBox questionAndButtonsLayout = new VBox(20);
            questionAndButtonsLayout.getChildren().addAll(userDataLayout, pitanjeLabel, radioButtonsLayout, buttonsLayout);
            questionAndButtonsLayout.setAlignment(Pos.CENTER);

            centerLayout.getChildren().add(questionAndButtonsLayout);
        } else {
            ZavrsiKviz();
        }
    }

    public void startTimer() {
        Thread timerThread = new Thread(() -> {
            try {
                while (remainingTime > 0) {
                    AzurirajTimerLabel();
                    Thread.sleep(1000);
                    remainingTime--;
                }
                ZavrsiKviz();

            } catch (InterruptedException e) {
            }
        });

        timerThread.setDaemon(true);
        timerThread.start();
    }

    public void AzurirajTimerLabel() {
        Platform.runLater(() -> {
            int minutes = remainingTime / 60;
            int seconds = remainingTime % 60;
            String formattedTime = String.format("%02d:%02d", minutes, seconds);
            timerLabel.setText("Vreme: " + formattedTime);
            timerLabel.setTextFill(Color.WHITE);
        });
    }

    public void ZavrsiKviz() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Kviz završen");
            alert.setHeaderText(null);
            alert.setContentText("Zavrsili ste kviz!");
            alert.showAndWait();

            Stage stage = (Stage) timerLabel.getScene().getWindow();
            stage.close();
        });
    }

    public void PrikaziSledecePitanje() {
        sacuvajPitanje();

        if (!odgovoreno) {
            // Ako nijedno pitanje nije selectovano izbaciti upozorenje
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Upozorenje");
            alert.setHeaderText(null);
            alert.setContentText("Molimo vas označite jedan od odgovora pre nego što nastavite na sledeće pitanje.");
            alert.showAndWait();
            return;
        }

        trenutnoPitanjeIndex++;

        if (trenutnoPitanjeIndex < pitanja.size()) {
            centerLayout.getChildren().clear();
            PrikaziPitanje();
        } else {
            ZavrsiKviz();
        }
        informisiServeroOdgvoru();
    }

    private void informisiServeroOdgvoru() {
        if (trenutnoPitanjeIndex - 1 < pitanja.size()) {
            Pitanja trenutnoPitanje = pitanja.get(trenutnoPitanjeIndex - 1);

            if (selectedRadioButton != null) {
                String selectedAnswer = selectedRadioButton.getText();
                String message = "STUDENT_ODGOVOR " + studentIme + " " + studentPrezime
                        + " Pitanje: " + trenutnoPitanje.getTekstPitanja() + " Odgovor: " + selectedAnswer;

                posaljiServeru(message);
            }
        }
    }

    private void posaljiServeru(String message) {
        try (Socket socket = new Socket("localhost", 12335)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            System.err.println("Neuspesno slanje podataka na server: " + e.getMessage());
        }
    }

    public ToggleGroup ToggleGroupForQuestion(Pitanja currentQuestion) {
        ToggleGroup toggleGroup = new ToggleGroup();

        RadioButton opcija1 = new RadioButton(currentQuestion.getOpcija1());
        RadioButton opcija2 = new RadioButton(currentQuestion.getOpcija2());
        RadioButton opcija3 = new RadioButton(currentQuestion.getOpcija3());

        opcija1.setToggleGroup(toggleGroup);
        opcija2.setToggleGroup(toggleGroup);
        opcija3.setToggleGroup(toggleGroup);

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            selectedRadioButton = (RadioButton) newValue;
        });

        return toggleGroup;
    }

    public String TrenutnoVreme() {
        LocalDate danas = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return danas.format(formatter);
    }

    public void sacuvajPitanje() {
        if (!odgovoreno) {
            Pitanja trenutnoPitanje = pitanja.get(trenutnoPitanjeIndex);
            RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();

            if (selectedRadioButton != null) {
                odgovoreno = true;
                String selectedAnswer = selectedRadioButton.getText();
                Odgovor odgovor = new Odgovor(studentIme, studentPrezime, trenutnoPitanje.getTekstPitanja(), selectedAnswer);
                RezultatDAO.sacuvajRezultat(odgovor);
            }
        }
    }
}
