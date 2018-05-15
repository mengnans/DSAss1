package activitystreamer.server;

import com.google.gson.JsonObject;

public class ServerProcessor {

    public static void ProcessServerAnnounce() {
        JsonObject _message = ServerCommandData.ServerAnnounce();
        ServerAPIHelper.BroadcastToServer(_message);
    }

    /**
     * This function will be called when the server succeed in connecting to another server
     *
     * @param argConnection The ServerConnection object of this new connection
     */
    public static void ProcessConnectToServer(ServerConnection argConnection) {
        JsonObject _message = ServerCommandData.Authenticate();
        ServerAPIHelper.BroadcastToServer(_message);
        ServerAPIHelper.SendMessage(argConnection, _message);
    }

    /**
     * This function will be called when a message is received through the network
     *
     * @param argConnection The ServerConnection object of this message
     * @param argJsonObject The json object containing the message data
     */
    public static boolean ProcessNetworkMessage(ServerConnection argConnection, JsonObject argJsonObject) {

        return false;
    }

}