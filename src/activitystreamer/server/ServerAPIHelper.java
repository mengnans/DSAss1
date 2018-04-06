package activitystreamer.server;

import activitystreamer.util.JsonHelper;
import com.google.gson.JsonObject;

public class ServerAPIHelper {

    public static void SendMessage(ServerConnection argConnection, JsonObject argMessageObject) {
        String _messageContent = JsonHelper.ObjectToString(argMessageObject);
        argConnection.writeMsg(_messageContent);
    }

}