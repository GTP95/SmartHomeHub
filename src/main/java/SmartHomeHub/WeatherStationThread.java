package SmartHomeHub;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import tools.JSONCreator;
import static java.lang.Math.exp;
import static java.lang.Math.log;


public class WeatherStationThread implements Runnable{
   private int port;
   private volatile String response;
   private static String myURL="meteofarlocco.tk";
   private static WeatherStationThread ourInstance;

    private WeatherStationThread(int port) {
        this.port = port;
        this.response=
                "<html>"+
                        "<head>"+
                            "<title>Stazione meteo</title>"+
                            "<META HTTP-EQUIV=\"Refresh\" CONTENT=\"150; url=" + myURL+"\">"+
                        "</head>"+
                        "<body>"+
                            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"+
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

    public String getResponse() {
        return response;
    }

    public void receiveJson(String json){
        float humidity=JSONCreator.parseFloatFiledFromJson(json, "humidity");
        float temperature=JSONCreator.parseFloatFiledFromJson(json, "temperature");
        float heatIndex=JSONCreator.parseFloatFiledFromJson(json, "heat index");
        double dewPoint=dewPoint(humidity, temperature);
        String lastUpdateTimestamp=JSONCreator.parseStringFiledFromJson(json,"timestamp");
        this.response=
                "<html>"+
                    "<head>"+
                        "<title>Stazione meteo</title>"+
                        "<META HTTP-EQUIV=\"Refresh\" CONTENT=\"150; url=" + myURL+"\">"+
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\">"+
                        "<style>html { font-family: 'Open Sans', sans-serif; display: block; margin: 0px auto; text-align: center;color: #444444;}"+
                        "body{margin-top: 50px;} h1 {color: #444444;margin: 50px auto 30px;}"+
                        "p {font-size: 24px;color: #444444;margin-bottom: 10px;}"+
                        "</style>"+
                    "</head>" +
                "<body>" +
                        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
                        "<div id=\"webpage\">"+
                        "<h1>Meteo farlocco</h1>"+
                        "&#x1f4a7 Umidità: " + humidity + "%<br>" +
                        "&#x1f321 Temperatura: " + temperature + "°C<br>" +
                        "&#x1f321 Temperatura percepita: " + heatIndex + "°C<br>" +
                        "&#x1f4a6 Punto di rugiada (dew point): " + dewPoint + "°C (Potrebbe esserci un errore nella formula, non fidatevi. Se non funziona prendetevela con Wikipedia)<br>" +
                        "&#x1f701 L'aria viene percepita come " + howAirFeels(dewPoint) + " (Idem come sopra)<br>" +
                        "&#x1f550 Ultimo aggiornamento: " + lastUpdateTimestamp +
                "</body></html>";
    }

    private double dewPoint(float humidity, float temperature){  //formula explanation: https://en.wikipedia.org/wiki/Dew_point
        double a, b ,c, d, dewPoint;
        d = 234.5;
        //switch/case can't operate on float...
        if(temperature>=0 && temperature<=50){
            a=6.1121;
            b=17.368;
            c=238.88;
        }
        else if(temperature>=-40 && temperature <0){
            a = 6.1121;
            b = 17.966;
            c = 247.15;
        }
        else{   //These valuations provide a maximum error of 0.1%, for −30 °C ≤ T ≤ 35°C and 1% < RH < 100%
            a = 6.112;
            b = 17.62;
            c = 243.12;
        }

        double psm=a*exp(b-temperature/d);
        double gamma=log(humidity/100*exp((b-temperature/d)*(temperature/(c+temperature))));
        dewPoint=c*gamma/(b-gamma);

        return dewPoint;
    }

    private String howAirFeels(double dewPoint){ //https://www.best-microcontroller-projects.com/dht22.html
        if(dewPoint<=13) return "secca";
        if(dewPoint>13 && dewPoint<=16) return "confortevole";
        if(dewPoint>16 && dewPoint<=18) return "abbastanza umida";
        if(dewPoint>18 && dewPoint<=21) return "umida";
        if(dewPoint>21 && dewPoint<=24) return "molto umida";
        else return "troppo umida";     //dewPoint>24
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket=new ServerSocket(port);
            System.out.println("Weather station webapp listening on port "+port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread weatherStationClientHandler=new Thread(new WeatherStationClientHandler(clientSocket));
                weatherStationClientHandler.start();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
