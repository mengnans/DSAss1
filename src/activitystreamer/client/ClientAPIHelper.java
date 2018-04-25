package activitystreamer.client;

import com.google.gson.JsonObject;

public class ClientAPIHelper {

    private ClientItem clientItem;

    public ClientAPIHelper(ClientItem argClientItem) {
        clientItem = argClientItem;
    }

    /**
     * Make the client connect to a given server.
     *
     * @param argHost The data of the host of the server
     * @param argPort The data of the port of the server
     * @return True if succeed; False if failed
     */
    public boolean SetConnection(String argHost, int argPort) {
        return clientItem.SetConnect(argHost, argPort);
    }

    /**
     * Show given json object on screen
     *
     * @param argMessageObject The json message object
     */
    public void SetDisplayMessage(JsonObject argMessageObject) {
        clientItem.SetDisplay(argMessageObject);
    }

    /**
     * Send the json object to the server
     *
     * @param activityObject The json message object
     */
    public void SendActivityObject(JsonObject activityObject) {
        clientItem.sendActivityObject(activityObject);
    }

}
