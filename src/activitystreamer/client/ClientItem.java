package activitystreamer.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

public class ClientItem extends Thread {
    private static final Logger log = LogManager.getLogger();
    private static ClientItem clientSolution;
    private FormWindow formWindowItem;

    public static ClientItem getInstance() {
        if (clientSolution == null) {
            clientSolution = new ClientItem();
        }
        return clientSolution;
    }

    public ClientItem() {
        formWindowItem = new FormWindow();
        start();
    }

    @SuppressWarnings("unchecked")
    public void sendActivityObject(JSONObject activityObj) {

    }

    public void disconnect() {

    }

    public void run() {

    }

}