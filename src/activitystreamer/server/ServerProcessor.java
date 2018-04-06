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
     */
    public static void ProcessConnectToServerMessage(ServerConnection argConnection) {

    }

    /**
     * This function will be called when a message is received through the network
     *
     * @param argConnection The ServerConnection object of this new connection
     */
    public static void ProcessNewConnectionMessage(ServerConnection argConnection) {

    }

    /**
     * This function will be called when a message is received through the network
     *
     * @param argConnection The ServerConnection object of this message
     * @param argJsonObject The json object containing the message data
     * @return return True to terminate the connection; return false to remain connecting
     */
    public static boolean ProcessNetworkMessage(ServerConnection argConnection, JsonObject argJsonObject) {

        return false;
    }

}