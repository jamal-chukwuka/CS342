

import controller.ServerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main entry point for the Poker Server application.
 * This launches the JavaFX UI that allows the server host to control the game.
 */
public class JavaFXTemplate extends Application {

    /**
     * Initializes and displays the server UI.
     * 
     * @param primaryStage The main JavaFX window where the UI is shown.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("Loading FXML for the server UI...");

            // Load the FXML layout for the server control panel.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Server.fxml"));

            // Verify that the FXML file was found. If not, print an error.
            if (loader.getLocation() == null) {
                throw new IllegalStateException("FXML Location is NULL. Check file path.");
            }

            // Create the scene from the loaded FXML file.
            Scene scene = new Scene(loader.load());

            // Get the controller instance and associate it with the primary stage.
            ServerController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);

            // Set the primary stage's scene and display the UI.
            primaryStage.setScene(scene);
            primaryStage.setTitle("Poker Server - Start");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load Server.fxml. Check if the file exists and is correctly referenced.");
        }
    }

    /**
     * Main method to launch the JavaFX application.
     *
     * @param args Command-line arguments (not used here).
     */
    public static void main(String[] args) {
        launch(args);
    }
}
