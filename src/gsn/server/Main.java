package gsn.server;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Server mainServer = new Server(9999);
        try {
            mainServer.start();
            Server.instance = mainServer;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
