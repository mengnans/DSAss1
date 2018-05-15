package activitystreamer.server;

import activitystreamer.util.JsonHelper;
import com.google.gson.JsonObject;

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

    public static void BroadcastToServer(JsonObject argMessage) {
        for (ServerConnection _connectionItem : ServerItem.connections) {
            if (_connectionItem.connectionType == ServerConnection.ConnectionType.ConnectedToServer) {
                SendMessage(_connectionItem, argMessage);
            }
        }
    }

    public static void BroadcastToServer(JsonObject argMessage, ServerConnection argExceptionConnection) {
        for (ServerConnection _connectionItem : ServerItem.connections) {
            if (argExceptionConnection == _connectionItem)
                continue;
            if (_connectionItem.connectionType == ServerConnection.ConnectionType.ConnectedToServer) {
                SendMessage(_connectionItem, argMessage);
            }
        }
    }

    public static void BroadcastToClient(JsonObject argMessage) {
        for (ServerConnection _connectionItem : ServerItem.connections) {
            if (_connectionItem.connectionType == ServerConnection.ConnectionType.ConnectedToClient) {
                SendMessage(_connectionItem, argMessage);
            }
        }
    }

    public static int GetConnectedClientAmount() {
        int _amount = 0;
        for (ServerConnection _connectionItem : ServerItem.connections) {
            if (_connectionItem.connectionType == ServerConnection.ConnectionType.ConnectedToClient) {
                _amount++;
            }
        }
        return _amount;
    }

    public static int GetConnectedServerAmount() {
        int _amount = 0;
        for (ServerConnection _connectionItem : ServerItem.connections) {
            if (_connectionItem.connectionType == ServerConnection.ConnectionType.ConnectedToClient) {
                _amount++;
            }
        }
        return _amount;
    }

}