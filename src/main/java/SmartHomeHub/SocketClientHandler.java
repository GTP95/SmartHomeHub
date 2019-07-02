package SmartHomeHub;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;

    public SocketClientHandler(Socket clientSocket) {
        this.clientSocket=clientSocket;
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
        }
        catch(java.io.IOException e){
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void run() {

    }
}
