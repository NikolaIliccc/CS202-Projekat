package Scene;




import Entiteti.Odgovor;
import Entiteti.Pitanja;
import database.PitanjaDAO;
import database.RezultatDAO;
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

    public Kviz(Background background, String studentIme, String studentPrezime) {
        this.background = background;
        this.pitanja = PitanjaDAO.SvaPitanja();
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

    private void PrikaziPitanje() {
        odgovoreno = false; // Resetujemo status odgovora za novo pitanje

        if (trenutnoPitanjeIndex < pitanja.size()) {
            Pitanja trenutnoPitanje = pitanja.get(trenutnoPitanjeIndex);

            Label pitanjeLabel = new Label(trenutnoPitanje.getTekstPitanja());

            toggleGroup = ToggleGroupForQuestion(trenutnoPitanje);

            RadioButton opcija1 = new RadioButton(trenutnoPitanje.getOpcija1());
            RadioButton opcija2 = new RadioButton(trenutnoPitanje.getOpcija2());
            RadioButton opcija3 = new RadioButton(trenutnoPitanje.getOpcija3());

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
            Label prezimeLabel = new Label("Prezime: " + studentPrezime);
            Label datumLabel = new Label("Datum: " + currentDate);
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

    private void startTimer() {
        Thread timerThread = new Thread(() -> {
            try {
                while (remainingTime > 0) {
                    AzurirajTimerLabel();
                    Thread.sleep(1000);
                    remainingTime--;
                }
                ZavrsiKviz();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        timerThread.setDaemon(true);
        timerThread.start();
    }

    private void AzurirajTimerLabel() {
        Platform.runLater(() -> {
            int minutes = remainingTime / 60;
            int seconds = remainingTime % 60;
            String formattedTime = String.format("%02d:%02d", minutes, seconds);
            timerLabel.setText("Vreme: " + formattedTime);
        });
    }

    private void ZavrsiKviz() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Kviz završen");
            alert.setHeaderText(null);
            alert.setContentText("Da li ste sigurni da želite zatvoriti kviz?");
            alert.showAndWait();

            Stage stage = (Stage) timerLabel.getScene().getWindow();
            stage.close();
        });
    }

    private void PrikaziSledecePitanje() {
        sacuvajPitanje();

        trenutnoPitanjeIndex++;

        if (trenutnoPitanjeIndex < pitanja.size()) {
            centerLayout.getChildren().clear();
            PrikaziPitanje();
        } else {
            ZavrsiKviz();
        }
    }

    private ToggleGroup ToggleGroupForQuestion(Pitanja currentQuestion) {
        ToggleGroup toggleGroup = new ToggleGroup();

        RadioButton opcija1 = new RadioButton(currentQuestion.getOpcija1());
        RadioButton opcija2 = new RadioButton(currentQuestion.getOpcija2());
        RadioButton opcija3 = new RadioButton(currentQuestion.getOpcija3());

        opcija1.setToggleGroup(toggleGroup);
        opcija2.setToggleGroup(toggleGroup);
        opcija3.setToggleGroup(toggleGroup);

        return toggleGroup;
    }

    private String TrenutnoVreme() {
        LocalDate danas = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return danas.format(formatter);
    }

    private void sacuvajPitanje() {
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
