package Scene;

import javafx.scene.paint.Color;
import Entiteti.Odgovor;
import Entiteti.Pitanja;
import database.PitanjaDAO;
import database.RezultatDAO;
import exceptions.OdgovorException;
import exceptions.OpcijaException;
import exceptions.PitanjeNijePronadjenoException;
import exceptions.PrazanException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProfesorDashboard extends Application {

    private Background background;
    private Scene pocetnaScena;

    public ProfesorDashboard(Background background, Scene pocetnaScena) {
        this.background = background;
        this.pocetnaScena = pocetnaScena;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Profesor Dashboard");

        Button novoPitanje = createButton("Novo Pitanje", e -> prikaziDodajPitanjeFormu());
        Button azurirajPitanje = createButton("Azuriraj Pitanje", e -> prikaziAzuriranuFormu());
        Button svaPitanja = createButton("Sva Pitanja", e -> prikaziTabeluSvihPitanja());
        Button izbrisiPitanje = createButton("Izbrisi Pitanje", e -> prikaziFormuZaBrisanje());
        Button sviRezultati = createButton("Student Rezultati", e -> prikaziSveRezultateStudenata());
        Button logoutButton = createButton("Logout", e -> logout(primaryStage));

        HBox topMenu = new HBox(10, novoPitanje, azurirajPitanje, svaPitanja, izbrisiPitanje, sviRezultati, logoutButton);
        topMenu.setAlignment(Pos.TOP_LEFT);
        topMenu.setPadding(new Insets(10, 10, 10, 10));

        VBox layout = new VBox(10, topMenu);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setBackground(background);

        Scene scene = new Scene(layout, 650, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMaxWidth(650);
        primaryStage.setMaxHeight(450);
        primaryStage.setMinWidth(650);
        primaryStage.setMinHeight(450);
    }

    private Button createButton(String text, EventHandler<ActionEvent> eventHandler) {
        Button button = new Button(text);
        button.setMinSize(80, 20);
        button.setOnAction(eventHandler);
        return button;
    }

    private void logout(Stage primaryStage) {
        try (Socket socket = new Socket("localhost", 12335)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("LOGOUT");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = in.readLine();

            if ("LOGOUT_OK".equals(response)) {
                System.out.println("Profesor izlogovan!");
                primaryStage.setScene(pocetnaScena);
                primaryStage.show();
            } else {
                System.out.println("Logout neuspesno!: " + response);
            }
        } catch (IOException e) {
            System.err.println("Neuspesno konektovanje na server: " + e.getMessage());
        }
    }

    private void prikaziSveRezultateStudenata() {
        Stage allResultsStage = new Stage();
        allResultsStage.setTitle("Rezultati Studenata");

        TableView<Odgovor> tableView = new TableView<>();
        TableColumn<Odgovor, Integer> id = new TableColumn<>("Odgovor ID");
        id.setCellValueFactory(new PropertyValueFactory<>("odgovorID"));  // Ispravljeno ime atributa

        TableColumn<Odgovor, String> ime = new TableColumn<>("Ime");
        ime.setCellValueFactory(new PropertyValueFactory<>("ime"));

        TableColumn<Odgovor, String> prezime = new TableColumn<>("Prezime");
        prezime.setCellValueFactory(new PropertyValueFactory<>("prezime"));

        TableColumn<Odgovor, String> pitanje = new TableColumn<>("Pitanje");
        pitanje.setCellValueFactory(new PropertyValueFactory<>("pitanje"));

        TableColumn<Odgovor, String> odgovor = new TableColumn<>("Odgovor");
        odgovor.setCellValueFactory(new PropertyValueFactory<>("odgovor"));

        tableView.getColumns().addAll(id, ime, prezime, pitanje, odgovor);

        TextField filterText = new TextField();
        Button filterButton = new Button("Filter");
        filterButton.setOnAction(e -> filtrirajRezultate(tableView, filterText.getText()));

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> {
            List<Odgovor> allResults = RezultatDAO.SvaPitanja();
            tableView.getItems().clear();
            tableView.getItems().addAll(allResults);
        });

        HBox filterBox = new HBox(10, new Label("Filtriraj po Odgovorima:"), filterText, filterButton, refreshButton);
        filterBox.setAlignment(Pos.CENTER);
        filterBox.setPadding(new Insets(10));

        List<Odgovor> allResults = RezultatDAO.SvaPitanja();
        tableView.getItems().addAll(allResults);

        VBox layout = new VBox(filterBox, tableView);
        Scene scene = new Scene(layout, 980, 450);
        allResultsStage.setScene(scene);
        allResultsStage.show();
        allResultsStage.setMaxWidth(980);
        allResultsStage.setMaxHeight(450);
        allResultsStage.setMinWidth(980);
        allResultsStage.setMinHeight(450);
    }

    private void prikaziDodajPitanjeFormu() {
        Stage addQuestionStage = new Stage();
        addQuestionStage.setTitle("Dodaj Novo Pitanje");

        Label pitanjelabel = new Label("Pitanje:");
        pitanjelabel.setTextFill(Color.WHITE); // Set text color to white
        TextField pitanjeField = new TextField();

        Label opcija1label = new Label("Opcija 1:");
        opcija1label.setTextFill(Color.WHITE);
        TextField opcija1Field = new TextField();

        Label opcija2Label = new Label("Opcija 2:");
        opcija2Label.setTextFill(Color.WHITE);
        TextField opcija2Field = new TextField();

        Label opcija3Label = new Label("Opcija 3:");
        opcija3Label.setTextFill(Color.WHITE);
        TextField opcija3Field = new TextField();

        Label odgoovrLabel = new Label("Odgovor:");
        odgoovrLabel.setTextFill(Color.WHITE);
        TextField odgovorField = new TextField();

        Button dodajPitanje = new Button("Dodaj Pitanje");
        dodajPitanje.setOnAction(e -> {
            try {
                Pitanja pitanja = new Pitanja(
                        pitanjeField.getText(),
                        opcija1Field.getText(),
                        opcija2Field.getText(),
                        opcija3Field.getText(),
                        odgovorField.getText()
                );

                PitanjaDAO.dodajPitanje(pitanja);
                sendUpdateMessage("NOVO_PITANJE", pitanja.getIDPitanja(), pitanja.getTekstPitanja());
                addQuestionStage.close();
            } catch (OpcijaException | OdgovorException | PrazanException ex) {
                showAlert("Greska", ex.getMessage());
            }
        });

        VBox dodajPitanjeLayout = new VBox(10);
        dodajPitanjeLayout.setBackground(background);
        dodajPitanjeLayout.setPadding(new Insets(20, 20, 20, 20));
        dodajPitanjeLayout.getChildren().addAll(
                pitanjelabel, pitanjeField,
                opcija1label, opcija1Field,
                opcija2Label, opcija2Field,
                opcija3Label, opcija3Field,
                odgoovrLabel, odgovorField,
                dodajPitanje
        );

        Scene addQuestionScene = new Scene(dodajPitanjeLayout, 400, 400);
        addQuestionScene.setFill(Color.WHITE);
        addQuestionStage.setScene(addQuestionScene);
        addQuestionStage.show();
    }

    private void prikaziTabeluSvihPitanja() {
        Stage allQuestionsStage = new Stage();
        allQuestionsStage.setTitle("Sva Pitanja");

        // Kreiranje TableView
        TableView<Pitanja> tableView = new TableView<>();

        // Kreiranje TableColumn za svaki atribut u Pitanja klasi
        TableColumn<Pitanja, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("IDPitanja"));

        TableColumn<Pitanja, String> text = new TableColumn<>("Text");
        text.setCellValueFactory(new PropertyValueFactory<>("tekstPitanja"));

        TableColumn<Pitanja, String> opcija1 = new TableColumn<>("Opcija 1");
        opcija1.setCellValueFactory(new PropertyValueFactory<>("opcija1"));

        TableColumn<Pitanja, String> opcija2 = new TableColumn<>("Opcija 2");
        opcija2.setCellValueFactory(new PropertyValueFactory<>("opcija2"));

        TableColumn<Pitanja, String> opcija3 = new TableColumn<>("Opcija 3");
        opcija3.setCellValueFactory(new PropertyValueFactory<>("opcija3"));

        TableColumn<Pitanja, String> odgovor = new TableColumn<>("Odgovor");
        odgovor.setCellValueFactory(new PropertyValueFactory<>("odgovor"));

        // Dodavanje TableColumn-ova u TableView
        tableView.getColumns().addAll(idColumn, text, opcija1, opcija2, opcija3, odgovor);

        // Učitavanje podataka iz baze
        List<Pitanja> allQuestions = PitanjaDAO.SvaPitanja();
        tableView.getItems().addAll(allQuestions);

        // Postavljanje Scene sa TableView
        Scene scene = new Scene(new VBox(tableView), 1480, 320);
        allQuestionsStage.setScene(scene);
        allQuestionsStage.show();
        allQuestionsStage.setMaxWidth(1480);
        allQuestionsStage.setMaxHeight(320);
        allQuestionsStage.setMinWidth(1480);
        allQuestionsStage.setMinHeight(320);
    }

    private void prikaziAzuriranuFormu() {
        Stage updateQuestionStage = new Stage();
        updateQuestionStage.setTitle("Azuriraj Pitanja");

        Label idLabel = new Label("Pitanje ID:");
        idLabel.setTextFill(Color.WHITE); // Set text color to white
        TextField idField = new TextField();

        Label pitanjeLabel = new Label("Pitanje:");
        pitanjeLabel.setTextFill(Color.WHITE);
        TextField pitanjeField = new TextField();

        Label opcija1Label = new Label("Opcija 1:");
        opcija1Label.setTextFill(Color.WHITE);
        TextField opcija1Field = new TextField();

        Label opcija2Label = new Label("Opcija 2:");
        opcija2Label.setTextFill(Color.WHITE);
        TextField opcija2Field = new TextField();

        Label opcija3Label = new Label("Opcija 3:");
        opcija3Label.setTextFill(Color.WHITE);
        TextField opcija3Field = new TextField();

        Label odgovorLabel = new Label("Odgovor:");
        odgovorLabel.setTextFill(Color.WHITE);
        TextField odgovorField = new TextField();

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            String inputText = idField.getText().trim();

            if (!inputText.isEmpty()) {
                try {
                    int questionId = Integer.parseInt(inputText);
                    Pitanja pitanja = PitanjaDAO.getQuestionById(questionId);

                    if (pitanja != null) {
                        // Popunjavamo polja sa podacima iz baze
                        pitanjeField.setText(pitanja.getTekstPitanja());
                        opcija1Field.setText(pitanja.getOpcija1());
                        opcija2Field.setText(pitanja.getOpcija2());
                        opcija3Field.setText(pitanja.getOpcija3());
                        odgovorField.setText(pitanja.getOdgovor());
                    } else {
                        throw new PitanjeNijePronadjenoException("Nijedno pitanje nije pronadjeno sa ID:" + questionId);
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Nepravilan Unos", "Molimo unesite ispravan broj za Pitanje ID.");
                } catch (PitanjeNijePronadjenoException ex) {
                    showAlert("Pitanje nije Pronadjeno", ex.getMessage());
                }
            } else {
                showAlert("Prazan Unos", "Molimo unesite Pitanje ID.");
            }
        });

        Button updateButton = new Button("Azuriraj Pitanje");
        updateButton.setOnAction(e -> {
            String inputText = idField.getText().trim();

            if (inputText.isEmpty()) {
                showAlert("Prazan Unos", "Molimo unesite Pitanje ID.");
            } else {
                try {
                    int questionId = Integer.parseInt(inputText);
                    Pitanja existingQuestion = PitanjaDAO.getQuestionById(questionId);

                    if (existingQuestion != null) {
                        Pitanja updatedQuestion = new Pitanja(
                                pitanjeField.getText(),
                                opcija1Field.getText(),
                                opcija2Field.getText(),
                                opcija3Field.getText(),
                                odgovorField.getText()
                        );

                        PitanjaDAO.azurirajPitanje(questionId, updatedQuestion);
                        sendUpdateMessage("AZURIRAJ_PITANJE", questionId, updatedQuestion.getTekstPitanja());
                        updateQuestionStage.close();
                    } else {
                        showAlert("Pitanje nije pronadjeno", "Nijedno pitanje nije pronadjeno sa ID: " + questionId);
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Pogresan Unos", "Molimo unesite ispravan broj za Pitanje ID.");
                } catch (OpcijaException | OdgovorException | PrazanException ex) {
                    showAlert("Greska", ex.getMessage());
                }
            }
        });

        VBox updateQuestionLayout = new VBox(10);
        updateQuestionLayout.setBackground(background);
        updateQuestionLayout.setPadding(new Insets(20, 20, 20, 20));
        updateQuestionLayout.getChildren().addAll(
                idLabel, idField,
                searchButton,
                pitanjeLabel, pitanjeField,
                opcija1Label, opcija1Field,
                opcija2Label, opcija2Field,
                opcija3Label, opcija3Field,
                odgovorLabel, odgovorField,
                updateButton
        );

        Scene updateQuestionScene = new Scene(updateQuestionLayout, 400, 470);
        updateQuestionStage.setScene(updateQuestionScene);
        updateQuestionStage.show();
    }

    private void sendUpdateMessage(String command, int questionId, String additionalInfo) {
        try (Socket socket = new Socket("localhost", 12335)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(command + " " + questionId + " " + additionalInfo);
        } catch (IOException e) {
            System.err.println("Neuspesno konektovanje na server: " + e.getMessage());
        }

    }

    private void prikaziFormuZaBrisanje() {
        Stage deleteQuestionStage = new Stage();
        deleteQuestionStage.setTitle("Izbrisi Pitanje");

        Label idLabel = new Label("Pitanje ID:");
        idLabel.setTextFill(Color.WHITE);
        TextField idField = new TextField();

        Button deleteButton = new Button("Izbrisi");
        deleteButton.setOnAction(e -> {
            try {
                String inputText = idField.getText().trim();

                if (inputText.isEmpty()) {
                    throw new PrazanException("Unesite ID Pitanja.");
                }

                int questionId = Integer.parseInt(inputText);
                boolean success = PitanjaDAO.izbrisiPitanjePoId(questionId);

                if (success) {
                    sendUpdateMessage("OBRISI_PITANJE", questionId, null);
                    showAlert("Uspesno", "Pitanje sa ID " + questionId + " uspesno obrisano.");
                } else {
                    throw new PitanjeNijePronadjenoException("Nijedno pitanje nije pronadjeno sa ID:" + questionId);
                }

                deleteQuestionStage.close();
            } catch (NumberFormatException ex) {
                showAlert("Pogresan Unos", "Molimo unesite ispravan broj za Pitanje ID.");
            } catch (PrazanException | PitanjeNijePronadjenoException ex) {
                showAlert("Greska", ex.getMessage());
            }
        });

        VBox deleteQuestionLayout = new VBox(10);
        deleteQuestionLayout.setBackground(background);
        deleteQuestionLayout.setPadding(new Insets(20, 20, 20, 20));
        deleteQuestionLayout.getChildren().addAll(idLabel, idField, deleteButton);

        Scene deleteQuestionScene = new Scene(deleteQuestionLayout, 300, 150);
        deleteQuestionStage.setScene(deleteQuestionScene);
        deleteQuestionStage.show();
    }

    private void filtrirajRezultate(TableView<Odgovor> tableView, String filter) {
        List<Odgovor> filtriraniRezultati = RezultatDAO.RezultatiPoOdgovoru(filter);
        tableView.getItems().clear();
        tableView.getItems().addAll(filtriraniRezultati);
    }

    private Button Dugme(String text, EventHandler<ActionEvent> eventHandler) {
        Button button = new Button(text);
        button.setMinSize(80, 20);
        return button;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
