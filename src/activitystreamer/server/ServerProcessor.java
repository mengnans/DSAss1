package activitystreamer.server;

import com.google.gson.JsonObject;

public class ServerProcessor {

    private static ServerAPIHelper apiHelper;

    public static void SetInitial(ServerAPIHelper argApiHelper) {
        apiHelper = argApiHelper;
    }

    /**
     * This function will be called when the server succeed in connecting to another server
     *
     * @param argConnection The ServerConnection object of this new connection
     * @param argJsonObject The json object containing the message data
     */
    public static void ProcessConnectToServerMessage(ServerConnection argConnection, JsonObject argJsonObject) {

    }

    /**
     * This function will be called when a message is received through the network
     *
     * @param argConnection The ServerConnection object of this new connection
     * @param argJsonObject The json object containing the message data
     */
    public static void ProcessNewConnectionMessage(ServerConnection argConnection, JsonObject argJsonObject) {

    }

    /**
     * This function will be called when a message is received through the network
     *
     * @param argConnection The ServerConnection object of this message
     * @param argJsonObject The json object containing the message data
     */
    public static void ProcessNetworkMessage(ServerConnection argConnection, JsonObject argJsonObject) {

    }

}