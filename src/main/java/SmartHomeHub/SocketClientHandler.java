package SmartHomeHub;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.APPEND;

/**
 * This class handles the connection with the various devices
 */

public class SocketClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter writer;
    private String deviceID;

    public SocketClientHandler(Socket clientSocket) {
        this.clientSocket=clientSocket;
        try {
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer= Files.newBufferedWriter(Paths.get(deviceID), Charset.forName("ASCII"), APPEND);    // Create/open file with deviceID as name
        }
        catch(java.io.IOException e){
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        try{
            while(!in.ready()); //wait for input
            this.deviceID=in.readLine();    //the first thing sent by the client is it's ID.
        }
        catch (java.io.IOException e){
            System.err.println(e.getMessage());
        }
        while(true){

            try {
                if (in.ready()) writer.write(in.readLine());
                else if(clientSocket.isClosed()){   //save the file and exit
                    writer.close();
                    break;
                }
            } catch (IOException e) {
               System.err.println(e.getMessage());
            }

        }
    }
}
