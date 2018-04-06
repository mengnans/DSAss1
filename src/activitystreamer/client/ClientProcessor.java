package activitystreamer.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ClientProcessor {

    private static ClientAPIHelper apiHelper;

    public static void SetInitial(ClientAPIHelper argApiHelper) {
        apiHelper = argApiHelper;
    }


    /**
     * This function will be called whe na message is received through the network
     *
     * @param argJsonObject
     */
    public static void ProcessNetworkMessage(JsonObject argJsonObject) {

    }

    /**
     * This function will be called whe na message is received through the GUI
     *
     * @param argJsonObject
     */
    public static void ProcessUserMessage(JsonObject argJsonObject) {

    }

}