package activitystreamer.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.Socket;

public class ClientItem extends Thread {
    private static final Logger log = LogManager.getLogger();
    private static ClientItem clientItem;
    private ClientAPIHelper clientProcessor;
    private FormWindow formWindowItem;
    private Socket socket;

    public static ClientItem getInstance() {
        if (clientItem == null) {
            clientItem = new ClientItem();
        }
        return clientItem;
    }

    public ClientItem() {
        clientProcessor = new ClientAPIHelper(this);
        ClientProcessor.SetInitial(clientProcessor);
        formWindowItem = new FormWindow();
        start();
    }

    public boolean SetConnect(String argHost, int argPort) {
        try {
            socket = new Socket(argHost, argPort);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void disconnect() {

    }

    public void run() {

    }

    public void sendActivityObject(JsonObject argMessageObject) {
        ClientProcessor.ProcessUserMessage(argMessageObject);
    }

    /**
     * Show given json object on screen
     *
     * @param argMessageObject The json message object
     */
    public void SetDisplay(JsonObject argMessageObject) {
        formWindowItem.setOutputText(argMessageObject);
    }

    /**
     * Show given json object on screen
     *
     * @param argMessage The json message object
     */
    public void SetDisplay(String argMessage) {
        formWindowItem.setOutputText(argMessage);
    }

}