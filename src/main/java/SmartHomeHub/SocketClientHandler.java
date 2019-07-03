package SmartHomeHub;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SocketClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter writer;

    public SocketClientHandler(Socket clientSocket) {
        this.clientSocket=clientSocket;
        try {
            this.in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            this.writer= Files.newBufferedWriter(Paths.get("test"), Charset.forName("ASCII"));
        }
        catch(java.io.IOException e){
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        while(true){

            try {
                if (in.ready()) writer.write(in.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
