package SmartHomeHub;

import tools.JSONCreator;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private String json;
    private File deviceDir;

    public SocketClientHandler(Socket clientSocket) {
        this.clientSocket=clientSocket;
        try {
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer= Files.newBufferedWriter(Paths.get(deviceID), Charset.forName("ASCII"), APPEND);    // TODO: move inside run and switch to using a folder
        }
        catch(java.io.IOException e){
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        try{
            while(!in.ready()); //wait for input
            this.json=in.readLine();
            this.deviceID=JSONCreator.parseStringFiledFromJson(json, deviceID);
            if(!Files.exists(Paths.get(deviceID))){ //If doesn't exixst a folder named after the device creates it
                deviceDir=new File(deviceID);
                deviceDir.mkdir();
            }
        }
        catch (java.io.IOException e){
            System.err.println(e.getMessage());
        }
        while(true){

            try {   //TODO: rewrite
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
