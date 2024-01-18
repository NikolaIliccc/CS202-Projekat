package Main;

import Scene.StudentLogin;
import Scene.ProfesorLogin;
import ClientServer.Server;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {

    private Scene pocetnaScena;
    public static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Main.primaryStage = primaryStage;
        primaryStage.setTitle("Start");

        String imagePath = "/slike/background.jpg";
        Image backgroundImage = new Image(imagePath);
        BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT
        );
        Background backgroundObj = new Background(background);

        Label welcomeLabel = new Label("Dobrodošli! Izaberite način logovanja:");
        welcomeLabel.setStyle("-fx-font-size: 25px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button studentButton = new Button("Student Login");
        studentButton.setOnAction(e -> StudentLogin(backgroundObj));

        Button professorButton = new Button("Profesor Login");
        professorButton.setOnAction(e -> ProfesorLogin(backgroundObj));

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.getChildren().addAll(welcomeLabel, studentButton, professorButton);
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(backgroundObj);

        pocetnaScena = new Scene(layout, 710, 400);
        primaryStage.setScene(pocetnaScena);
        primaryStage.show();
        primaryStage.setMaxWidth(710);
        primaryStage.setMaxHeight(450);
        primaryStage.setMinWidth(710);
        primaryStage.setMinHeight(450);
        
        
        new Thread(() -> startServer()).start();

    }

    private void StudentLogin(Background background) {
        try {
            StudentLogin studentLogin = new StudentLogin(background, pocetnaScena);
            studentLogin.start(primaryStage);
        } catch (Exception e) {
        }
    }

    private void ProfesorLogin(Background background) {
        try {
            ProfesorLogin profesorLogin = new ProfesorLogin(background, pocetnaScena);
            profesorLogin.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startServer() {
        Server server = new Server();
        server.startServer();
    }
}
