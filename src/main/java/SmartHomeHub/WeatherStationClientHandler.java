package SmartHomeHub;

import java.io.IOException;
import java.net.Socket;

public class WeatherStationClientHandler implements Runnable{
   private Socket clientSocket;

     public WeatherStationClientHandler(Socket clientSocket) {
     this.clientSocket=clientSocket;
    }

    @Override
    public void run() {
        String response=WeatherStationThread.getInstance().getResponse();
        try {
            HTTPSender.send(response, this.clientSocket);
        } catch (IOException e) {
            System.err.println(e.getMessage());

        }
    }
}
