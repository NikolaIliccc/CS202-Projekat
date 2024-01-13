package Scene;




import Main.Main;
import database.ProfesorDAO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ProfesorLogin extends Application {

    private TextField usernameField;
    private PasswordField passwordField;
    private Background background;

    public ProfesorLogin(Background background) {
        this.background = background;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Profesor Login");
        Label welcomeLabel = new Label("Profesor Login:");
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
            Main mainApp = new Main();
            mainApp.start(primaryStage);
        });

        usernameField.setOnKeyPressed(event -> EnterKeyPress(event.getCode(), primaryStage));
        passwordField.setOnKeyPressed(event -> EnterKeyPress(event.getCode(), primaryStage));

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        layout.setBackground(background);

        HBox usernameBox = new HBox(10);
        usernameBox.getChildren().addAll(usernameField);
        usernameBox.setAlignment(Pos.CENTER);

        HBox passwordBox = new HBox(10);
        passwordBox.getChildren().addAll(passwordField);
        passwordBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(welcomeLabel,usernameBox, passwordBox, loginButton, backButton);

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
        if (ProfesorDAO.proveraProfesora(username, password)) {
            prikaziUspesanLogin();
            openProfesorDashboard(primaryStage);
            primaryStage.close();
        } else {
            PrikaziNeuspesanLogin();
        }
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

    private void EnterKeyPress(KeyCode keyCode, Stage primaryStage) {
        if (keyCode == KeyCode.ENTER) {
            Login(usernameField.getText(), passwordField.getText(), primaryStage);
        }
    }

    private void openProfesorDashboard(Stage primaryStage) {
        ProfesorDashboard profesorDashboard = new ProfesorDashboard(background);
        Stage dashboardStage = new Stage();
        profesorDashboard.start(dashboardStage);

    }

}
