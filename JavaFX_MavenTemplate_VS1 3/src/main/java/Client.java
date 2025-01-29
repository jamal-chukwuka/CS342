import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import model.PokerInfo;

public class Client {
    private static final String SERVER_ADDRESS = "localhost"; // Change if needed
    private static final int SERVER_PORT = 3000; // Ensure this matches the server's port

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("Connected to Poker Server!");

            // Example: Sending PokerInfo to the server
            PokerInfo info = new PokerInfo();
            info.setAnteBet(10);
            info.setPairPlusBet(5);
            output.writeObject(info);
            output.flush();

            // Receive response from server
            PokerInfo response = (PokerInfo) input.readObject();
            System.out.println("Server Response: " + response.getGameMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }
        JavaFXTemplate.main(args);
    }
}
