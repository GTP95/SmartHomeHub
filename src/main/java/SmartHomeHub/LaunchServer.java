package SmartHomeHub;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import tools.JSONCreator;

/**
 * This class is the entry point of the server
 */
public class LaunchServer {
    private static int portNumber;

    public static void main(String[] args) {
        try {
            if(args.length>0) portNumber = Integer.parseInt(args[0]);
            else
                try{portNumber=JSONCreator.parseIntFieldFromFile("src/main/java/SmartHomeHub/Settings/ServerSettings.json","port");}
                catch (FileNotFoundException e){
                    portNumber=1024;
                    System.err.println("File \"ServerSetting.json\" not found, falling back to default port 1024");
                }
            ServerSocket serverSocket=new ServerSocket(portNumber);
            System.out.println("Server started on port " + portNumber);

            Thread weatherStationThread=new Thread(WeatherStationThread.getInstance(1025));   //TODO: make port configurable
            weatherStationThread.start();

            while (true) {
                Socket clientSocket = serverSocket.accept();

              Thread socketClientHandler=new Thread(new SocketClientHandler(clientSocket));

              socketClientHandler.start();

            }
        }
        catch (NumberFormatException e) {
            System.out.println("Please provide a valid port number");
        }
        catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Usage: ");
            System.out.println("java LaunchServer portNumber");
            System.out.println("NOTE: using a port below 1024 may require root privileges");
        }
        catch(IOException e){
            e.getCause();
        }



    }
}
