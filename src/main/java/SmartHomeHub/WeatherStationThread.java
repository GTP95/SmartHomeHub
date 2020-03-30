package SmartHomeHub;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import tools.JSONCreator;
import java.sql.Timestamp;


public class WeatherStationThread implements Runnable{
   private int port;
   private volatile String response;
   private static WeatherStationThread ourInstance;

    private WeatherStationThread(int port) {
        this.port = port;
        this.response="<html><head><title>Stazione meteo</title></head>"+
                "<body>"+"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"+
                "Umidità: " + "In attesa dei dati<br>"+
                "Temperatura: In attesa dei dati<br>"+
                "Ultimo aggiornamento: non ho ancora ricevuto dati"+
                "</body></html>";
    }

    public synchronized static WeatherStationThread getInstance(int port){
        if(ourInstance!=null){
            System.err.println("Warning: ignoring port parameter for WeatherStationThread.getInstance(int port) since object was already created.\nYou should call WeatherStationThread.getInstance() instead");
            return ourInstance;
        }
        ourInstance=new WeatherStationThread(port);
        return ourInstance;
    }

    public synchronized static WeatherStationThread getInstance(){
        if(ourInstance==null){
            System.err.println("Warning: creating (but not starting!) WeatherStationThread with default port 1025 since it hasn't been created yet.\nYou should call WeatherStationThread.getInstance(int port) instead");
            ourInstance=new WeatherStationThread(1025);
        }
        return ourInstance;
    }

    public void receiveJson(String json){
        float humidity=JSONCreator.parseFloatFiledFromJson(json, "humidity");
        float temperature=JSONCreator.parseFloatFiledFromJson(json, "temperature");
        String lastUpdateTimestamp=new Timestamp(System.currentTimeMillis()).toString();
        this.response="<html><head><title>Stazione meteo</title></head>"+
                "<body>"+"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=charset=UTF-8\">"+
                "Umidità: " + humidity+"%<br>"+
                "Temperatura: " + temperature+"<br>"+
                "Ultimo aggiornamento: " + lastUpdateTimestamp+
                "</body></html>";
    }





    @Override
    public void run() {
        try {
            ServerSocket serverSocket=new ServerSocket(port);
            System.out.println("Weather station webapp listening on port "+port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
               HTTPSender.send(response, clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
