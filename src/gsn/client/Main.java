package gsn.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by niennguyen on 10/12/17.
 */
public class Main {
    public static void main(String args[]) {
        Scenario scenario1 = new Scenario(3, 100, 20);
        Client client = new Client(0, scenario1);
        Executors.newSingleThreadExecutor().execute(client);
    }
}
