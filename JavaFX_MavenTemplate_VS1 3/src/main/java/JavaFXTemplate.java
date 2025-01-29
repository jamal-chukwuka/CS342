import controllers.GamePlayController;
import controllers.WelcomeScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.Socket;

public class JavaFXTemplate extends Application {

    private Stage primaryStage;
    private Socket clientSocket;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showWelcomeScreen(); // Start with the welcome screen
    }

    public void showWelcomeScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/WelcomeScreen.fxml"));
            Scene scene = new Scene(loader.load());
            
            WelcomeScreenController controller = loader.getController();
            controller.setPrimaryStage(this.primaryStage);
            
            // Apply UIC theme stylesheet
            scene.getStylesheets().add(getClass().getResource("/views/uic_theme.css").toExternalForm());
            
            primaryStage.setScene(scene);
            primaryStage.setTitle("Welcome to Three Card Poker");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showGamePlayScreen(Socket socket) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GamePlayScreen.fxml"));
            Scene scene = new Scene(loader.load());
            
            GamePlayController gameController = loader.getController();
            gameController.initializeGame(socket);
            
            primaryStage.setScene(scene);
            primaryStage.setTitle("3 Card Poker - Game");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args); // Launch JavaFX application
    }
}
