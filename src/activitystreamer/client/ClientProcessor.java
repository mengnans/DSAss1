package activitystreamer.client;

import org.json.simple.JSONObject;

public class ClientProcessor {

    private ClientItem clientItem;

    public ClientProcessor(ClientItem argClientItem) {
        clientItem = argClientItem;
    }

    public void ProcessMessage(JSONObject argMessageObject) {

    }

    /**
     * Show given json object on screen
     *
     * @param argMessageObject The json message object
     */
    private void SetDisplay(JSONObject argMessageObject) {
        clientItem.SetDisplay(argMessageObject);
    }

}
