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
    private BufferedReader in;
    private BufferedWriter writer;
    private String deviceID;
    private String json;
    private File deviceDir;
    private Date date;

    public SocketClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket=clientSocket;

            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));



    }

    @Override
    public void run() {
        this.date=new Date();
        try{
            while(!in.ready()); //wait for input
            this.json=in.readLine();
            this.deviceID=JSONCreator.parseStringFiledFromJson(json, deviceID);
          /*  if(!Files.exists(Paths.get(deviceID))){ //If doesn't exist a folder named after the device creates it
                deviceDir=new File(deviceID);
                deviceDir.mkdir();
            }*/
        }
        catch (java.io.IOException e){
            System.err.println(e.getMessage());
        }
        while(true){
            try {
                if(in.ready()){
                    json=in.readLine();
                    Files.write(Paths.get(deviceDir+"/"+date.toString()), json.getBytes()); //creates a new file for each json received
                    switch(deviceID){   //notifies the right thread that new data arrived TODO
                        case "Weather station":
                            WeatherStationThread.getInstance().receiveJson(json);
                            break;
                        default:
                            System.err.println("Received data from unidentified device!");
                    }
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }

        }
    }
}
