/*quick and dirty way to send a webpage to a browser, might replace later wit a servlet or a web framework*/
package SmartHomeHub;

import java.io.*;
import java.net.Socket;


public class HTTPSender {
    public static void send(String content, Socket clientSocket) throws IOException {
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        BufferedWriter out=new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        while(!(line = reader.readLine()).isEmpty());   //wait for client to send request, then ignore it completely and send the answer :D
        int contentLength=content.length();
        String httpAnswer="HTTP/1.0 200 OK\r\nDate: Fri, 31 Dec 1999 23:59:59 GMT\r\nContent-Type: text/html\r\nContent-Length:"+contentLength+"\r\n\r\n"+content;
        out.write(httpAnswer);
        out.flush();
    }
}
