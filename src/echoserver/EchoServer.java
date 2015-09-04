package echoserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Utils;

/**
 * @author Tobias Jacobsen
 */

public class EchoServer {

    private static boolean keepRunning = true;
    private static ServerSocket serverSocket;
    private static final Properties properties = Utils.initProperties("server.properties");
    
    private List <ClientHandler> clientHandlerList = new ArrayList();
    
    public static void stopServer() {
        keepRunning = false;
    }

    private void handleClient(Socket socket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(socket, this);
        clientHandlerList.add(clientHandler);
        clientHandler.start();
    }

    private void runServer() {
        int port = Integer.parseInt(properties.getProperty("port"));
        String ip = properties.getProperty("serverIp");

        Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Sever started. Listening on: " + port + ", bound to: " + ip);
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));
            do {
                Socket socket = serverSocket.accept(); //Important Blocking call
                Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Connected to a client");
                handleClient(socket);
            } while (keepRunning);
        } catch (IOException ex) {
            Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void removeHandler(ClientHandler ch) {
        clientHandlerList.remove(ch);
    }
    
    public void send(String message) {
        for (ClientHandler clientHandler : clientHandlerList) {
            clientHandler.send(message.toUpperCase());
        }
    }

    public static void main(String[] args) {
        new EchoServer().runServer();
        try {
            String logFile = properties.getProperty("logFile");
            Utils.setLogFile(logFile, EchoServer.class.getName());
        } finally {
            Utils.closeLogger(EchoServer.class.getName());
        }
    }
}
