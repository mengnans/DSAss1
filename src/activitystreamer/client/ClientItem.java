package activitystreamer.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

public class ClientItem extends Thread {
    private static final Logger log = LogManager.getLogger();
    private static ClientItem clientItem;
    private ClientProcessor clientProcessor;
    private FormWindow formWindowItem;

    public static ClientItem getInstance() {
        if (clientItem == null) {
            clientItem = new ClientItem();
        }
        return clientItem;
    }

    public ClientItem() {
        clientProcessor = new ClientProcessor(this);
        formWindowItem = new FormWindow();
        start();
    }

    public void run() {

    }

    @SuppressWarnings("unchecked")
    public void sendActivityObject(JSONObject argMessageObject) {
        clientProcessor.ProcessMessage(argMessageObject);
    }

    public void disconnect() {

    }


    /**
     * Show given json object on screen
     *
     * @param argMessageObject The json message object
     */
    public void SetDisplay(JSONObject argMessageObject) {
        formWindowItem.setOutputText(argMessageObject);
    }

}