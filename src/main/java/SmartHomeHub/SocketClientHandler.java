package SmartHomeHub;

import tools.JSONCreator;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import static java.nio.file.StandardOpenOption.APPEND;

/**
 * This class handles the connection with the various devices
 */

public class SocketClientHandler implements Runnable {
    private Socket clientSocket;
    private InputStreamReader in;
    private PrintWriter out;
    private String deviceID;
    private String json;

    public SocketClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket=clientSocket;

            this.in = new InputStreamReader(clientSocket.getInputStream());
            this.out= new PrintWriter(clientSocket.getOutputStream(), true);
    }

    @Override
    public void run() {
        System.out.print("Waiting for client to send data");
        try{    //BufferedWriter with check for in.ready() didn't work, PrintWriter with check for in.ready() didn't work either, PrintWriter without check for in.ready() works. Didn't bother to try BufferedReader without in.ready()
            char c;
            String receivingJson="";
            while(true){
                c=(char)in.read();
                receivingJson+=c;
                if(c=='}') break;
            }
            this.json=receivingJson;
            this.deviceID=JSONCreator.parseStringFiledFromJson(json, "deviceID");
        }
        catch (IOException e ){
            System.err.println(e.getMessage());
        }
        switch(deviceID){
                        case "Weather station":
                            System.out.println("Received data from Weather station");
                            out.println("ok");
                            WeatherStationThread.getInstance().receiveJson(json);
                            break;
                        default:
                            System.err.println("Received data from unidentified device!");
                    }
                }
}

