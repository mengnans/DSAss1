package activitystreamer.server;

import com.google.gson.JsonObject;

public class ServerProcessor {

    private static ServerAPIHelper apiHelper;

    public static void SetInitial(ServerAPIHelper argApiHelper) {
        apiHelper = argApiHelper;
    }

    /**
     * This function will be called whe na message is received through the network
     *
     * @param argJsonObject The json object containing the message data
     */
    public static void ProcessNetworkMessage(JsonObject argJsonObject) {

    }

}