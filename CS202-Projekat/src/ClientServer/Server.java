package ClientServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static final int PORT = 12335;

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server je pokrenut. Ceka se konekcija...");

            while (true) {
                Socket clientSocket = serverSocket.accept();

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }

        } catch (IOException e) {
            System.err.println("Greska prilikom pokretanja servera!");
        }
    }
}
