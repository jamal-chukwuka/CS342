package controllers;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Card;
import model.PokerInfo;

/**
 * Handles all client-side game logic and UI updates for the Poker game.
 * Manages player interactions, communicates with the server, and updates the display accordingly.
 */
public class GamePlayController {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private int playerNumber;
    
    
    
    @FXML
    private BorderPane gameRoot; 

    @FXML
    private Label playerNameLabel, playerBetLabel, playerWinningsLabel, gameInfoLabel;
    
    @FXML
    private HBox playerCardsBox, dealerCardsBox, cardBox, opponentCardsBox; 


    @FXML
    private TextField anteField, pairPlusField;

    @FXML
    private Button playButton, foldButton;
    
    @FXML
    private MenuItem freshStartMenuItem, newLookMenuItem;
    
    private Stage primaryStage;

    /**
     * Initializes the controller and applies the default CSS style.
     * Runs after FXML elements are loaded.
     */
    public void initialize() {
        Platform.runLater(() -> {
            if (gameRoot.getScene() != null) {
                applyCSS("/views/style.css"); // Load default styles
            } else {
                System.err.println("Scene is null at initialization.");
            }
        });
    }

    /**
     * Applies a specified CSS stylesheet to the scene.
     *
     * @param cssPath The path to the CSS file.
     */
    private void applyCSS(String cssPath) {
        URL cssFile = getClass().getResource(cssPath);
        if (cssFile != null) {
            Scene scene = gameRoot.getScene();
            if (scene != null) {
                scene.getStylesheets().clear();
                scene.getStylesheets().add(cssFile.toExternalForm());
                System.out.println("Applied CSS: " + cssPath);
            } else {
                System.err.println("Scene is null. CSS not applied.");
            }
        } else {
            System.err.println("Failed to load CSS from " + cssPath);
        }
    }

    /**
     * Sets up the client connection to the server and prepares to receive updates.
     *
     * @param socket The socket connection to the server.
     */
    public void initializeGame(Socket socket) {
        try {
            this.socket = socket;
            this.output = new ObjectOutputStream(socket.getOutputStream());
            this.input = new ObjectInputStream(socket.getInputStream());

            // Read player number assigned by the server
            int playerNumber = input.readInt(); 
            System.out.println("Client received Player Number: " + playerNumber);
            
            // Start listening for game updates from the server
            listenForUpdates();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Continuously listens for server updates and updates the UI accordingly.
     */
    private void listenForUpdates() {
        new Thread(() -> {
            try {
                while (socket != null && !socket.isClosed()) {
                    try {
                        PokerInfo info = (PokerInfo) input.readObject();
                        Platform.runLater(() -> updateUI(info)); // Ensure UI updates run on the JavaFX thread
                    } catch (EOFException e) {
                        System.err.println("Server closed the connection.");
                        break;
                    } catch (IOException | ClassNotFoundException e) {
                        System.err.println("Error receiving data: " + e.getMessage());
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    /**
     * Updates the UI with the latest game state received from the server.
     *
     * @param info The game state information received from the server.
     */
    private void updateUI(PokerInfo info) {
        if (info == null) return;

        playerBetLabel.setText(String.format("Ante: $%d | Pair Plus: $%d | Play: $%d", 
                        info.getAnteBet(), info.getPairPlusBet(), info.getPlayBet()));
        playerWinningsLabel.setText("Total Winnings: $" + info.getTotalWinnings());
        gameInfoLabel.setText(info.getGameMessage());

        updateCards(playerCardsBox, info.getPlayerHand());
        updateCards(opponentCardsBox, info.getOpponentHand());

        if (!info.isDealerCardsHidden()) {
            updateCards(dealerCardsBox, info.getDealerHand());
        } else {
            showFaceDownCards(dealerCardsBox, 3);
        }
    }

    
    private void showFaceDownCards(HBox cardBox, int count) {
        cardBox.getChildren().clear();
        
        String imagePath = "/views/cards/Sparky.jpeg"; // Ensure the correct path
        URL imageUrl = getClass().getResource(imagePath);

        if (imageUrl == null) {
            System.err.println("Error: Card back image not found! " + imagePath);
            return; // Avoid NullPointerException
        }

        for (int i = 0; i < count; i++) {
            ImageView cardBack = new ImageView(new Image(imageUrl.toExternalForm()));
            cardBack.setFitWidth(100);
            cardBack.setPreserveRatio(true);
            cardBox.getChildren().add(cardBack);
        }
    }

    
    /**
     * Switches to the Win/Loss screen with game results.
     */
    private void showWinLossScreen(String message, int winnings) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/WinLoseScreen.fxml"));
            Scene winLossScene = new Scene(loader.load());

            // Get the WinLoseController and pass the results
            WinLoseController winLoseController = loader.getController();
            winLoseController.setPrimaryStage((Stage) gameRoot.getScene().getWindow());
            winLoseController.setSocket(socket);  // Keep the socket connection
            winLoseController.setResults(message, winnings);

            // Switch scenes
            Stage stage = (Stage) gameRoot.getScene().getWindow();
            stage.setScene(winLossScene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading WinLoseScreen.fxml");
            e.printStackTrace();
        }
    }

    /**
     * Updates the card display for a given HBox container.
     *
     * @param cardBox The UI container for the cards.
     * @param hand The list of cards to display.
     */
    private void updateCards(HBox cardBox, ArrayList<Card> hand) {
        if (cardBox == null) {
            System.err.println("Error: CardBox is NULL. Check FXML fx:id.");
            return;
        }

        cardBox.getChildren().clear(); // Clear previous cards
        System.out.println("Updating card display. Received " + hand.size() + " cards.");

        for (Card card : hand) {
            String imagePath = card.getImagePath();
            URL imageUrl = getClass().getResource(imagePath);

            if (imageUrl != null) {
                ImageView cardImage = new ImageView(new Image(imageUrl.toExternalForm()));
                cardImage.setFitWidth(100);
                cardImage.setPreserveRatio(true);
                cardBox.getChildren().add(cardImage);
            } else {
                System.err.println("Error: Image not found for " + imagePath + ". Using placeholder.");
                addPlaceholderCard(cardBox);
            }
        }
    }


    /**
     * Adds a placeholder (face-down) card to a given card box.
     *
     * @param cardBox The UI container for the cards.
     */
    private void addPlaceholderCard(HBox cardBox) {
        URL placeholderUrl = getClass().getResource("/views/cards/Sparky.jpeg"); // Ensure the correct path
        if (placeholderUrl != null) {
            ImageView placeholderImage = new ImageView(new Image(placeholderUrl.toExternalForm()));
            placeholderImage.setFitWidth(100);
            placeholderImage.setPreserveRatio(true);
            cardBox.getChildren().add(placeholderImage);
        } else {
            System.err.println("Error: Placeholder image missing.");
        }
    }

    /**
     * Sends the player's bet information to the server when they click "Deal."
     */
    @FXML
    private void handleDeal() {
        try {
            PokerInfo info = new PokerInfo();
            info.setAnteBet(Integer.parseInt(anteField.getText()));
            info.setPairPlusBet(Integer.parseInt(pairPlusField.getText()));

            output.writeObject(info);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a play action to the server when the player chooses to continue.
     */
    @FXML
    private void handlePlay() {
        try {
            PokerInfo info = new PokerInfo();
            info.setPlayBet(info.getAnteBet()); // The play bet matches the ante
            output.writeObject(info);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a fold action to the server when the player chooses to fold.
     */
    @FXML
    private void handleFold() {
        try {
            PokerInfo info = new PokerInfo();
            info.setPlayerFolded(true);
            output.writeObject(info);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the application when the player chooses to exit.
     */
    @FXML
    private void handleExitGame() {
        System.exit(0);
    }

    /**
     * Resets the game state to start a new round.
     */
    @FXML
    private void handleFreshStart() {
        playerWinningsLabel.setText("Total Winnings: $0");
        gameInfoLabel.setText("New game started!");
    }

    /**
     * Applies an alternate theme to the game UI.
     */
    @FXML
    private void handleNewLook() {
        System.out.println("Applying New Look...");

        Platform.runLater(() -> {
            if (gameRoot != null) {
                Scene scene = gameRoot.getScene();
                if (scene != null) {
                    scene.getStylesheets().clear();
                    URL newLook = getClass().getResource("/views/alternateStyle.css"); 
                    if (newLook != null) {
                        scene.getStylesheets().add(newLook.toExternalForm());
                        System.out.println("New Look Applied!");
                    } else {
                        System.err.println("Error: alternateStyle.css not found!");
                    }
                } else {
                    System.err.println("Scene is null. Trying again...");
                }
            } else {
                System.err.println("Error: gameRoot is NULL in handleNewLook()!");
            }
        });
    }
    
    
}
