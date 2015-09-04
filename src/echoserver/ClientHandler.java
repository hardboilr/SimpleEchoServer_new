package echoserver;

import echoclient.EchoClient;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;

/**
 * @author Tobias Jacobsen
 */
public class ClientHandler extends Thread {

    private Scanner input;
    private PrintWriter writer;
    private Socket socket;
    private EchoServer echoserver;

    public ClientHandler(Socket socket, EchoServer echoserver) throws IOException {
        this.socket = socket;
        this.echoserver = echoserver;
        input = new Scanner(socket.getInputStream());
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public void send(String message) {
        writer.println(message);
    }

    @Override
    public void run() {
        try {
            String message = input.nextLine(); //IMPORTANT blocking call
            Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message));
            while (!message.equals(ProtocolStrings.STOP)) {
                echoserver.send(message);
                Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message.toUpperCase()));
                message = input.nextLine(); //IMPORTANT blocking call
            }
            writer.println(ProtocolStrings.STOP);//Echo the stop message back to the client for a nice closedown
            socket.close();
            echoserver.removeHandler(this);
            Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Closed a Connection");
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
