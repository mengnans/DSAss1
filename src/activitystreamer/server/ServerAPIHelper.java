package activitystreamer.server;

import activitystreamer.util.JsonHelper;
import activitystreamer.util.Settings;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.ArrayList;

public class ServerAPIHelper {

    //send message to server or client
    public static void SendMessage(ServerConnection argConnection, JsonObject argMessageObject) {
        String _messageContent = JsonHelper.ObjectToString(argMessageObject);
        argConnection.writeMsg(_messageContent);
    }

    //search Object in JsonObject list based on the given key
    public static JsonObject searchObjects(String key, String value, ArrayList<JsonObject> jsonObjects) {
        for (JsonObject jsonObject : jsonObjects) {
            if (jsonObject.get(key).equals(value)) {
                return jsonObject;
            }
        }
        return null;
    }
}