package SmartHomeHub;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import tools.JSONCreator;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class WeatherStationThread implements Runnable{
   private volatile float humidity;
   private volatile float temperature;
   private BufferedWriter out;
   private int port;
   private String lastUpdateTimestamp;
   private String response;

    public WeatherStationThread(int port) {
        this.port = port;
        this.response="<html><head><title>Stazione meteo</title></head>"+
                "<body>"+"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">"+
                "Umidità: " + "In attesa dei dati<br>"+
                "Temperatura: In attesa dei dati<br>"+
                "Ultimo aggiornamento: non ho ancora ricevuto dati"+
                "</body></html>";
    }

    public void getJson(String json){
        this.humidity=JSONCreator.parseFloatFiledFromJson(json, "humidity");
        this.temperature=JSONCreator.parseFloatFiledFromJson(json, "temperature");
        this.lastUpdateTimestamp=new Timestamp(System.currentTimeMillis()).toString();
        this.response="<html><head><title>Stazione meteo</title></head>"+
                "<body>"+"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">"+
                "Umidità: " + this.humidity+"%<br>"+
                "Temperatura: " + this.temperature+"<br>"+
                "Ultimo aggiornamento: " + this.lastUpdateTimestamp+
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
