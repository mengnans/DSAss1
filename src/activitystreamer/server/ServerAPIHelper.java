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

   public static void BroadcastToServer(JsonObject argMessage) {
      for (ServerConnection _connectionItem : ServerItem.connections) {
         if (_connectionItem.connectionType == ServerConnection.ConnectionType.ConnectedToServer) {
            SendMessage(_connectionItem, argMessage);
         }
      }
   }

   public static void BroadcastToServer(JsonObject argMessage, ServerConnection argExceptionConnection) {
      for (ServerConnection _connectionItem : ServerItem.connections) {
         if (argExceptionConnection == _connectionItem) continue;
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

   public static ArrayList<ServerConnection> GetClientConnection() {
      ArrayList<ServerConnection> _lstConnection = new ArrayList<>();
      for (ServerConnection _connectionItem : ServerItem.connections) {
         if (_connectionItem.connectionType == ServerConnection.ConnectionType.ConnectedToClient) {
            _lstConnection.add(_connectionItem);
         }
      }
      return _lstConnection;
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

   public static boolean TestIsUserNameExisted(String argUserName) {
      for (int _proUserIndex = 0; _proUserIndex < ServerItem.lstUserInfo.size(); _proUserIndex++) {
         if (argUserName.equals(ServerItem.lstUserInfo.get(_proUserIndex)[0])) {
            return true;
         }
      }
      return false;
   }

   public static boolean TestIsUserInfoExisted(String argUserName, String argUserSecret) {
      for (int _proUserIndex = 0; _proUserIndex < ServerItem.lstUserInfo.size(); _proUserIndex++) {
         if (argUserName.equals(ServerItem.lstUserInfo.get(_proUserIndex)[0])) {
            if (argUserSecret.equals(ServerItem.lstUserInfo.get(_proUserIndex)[1])) {
               return true;
            }
            return false;
         }
      }
      return false;
   }

   public static boolean UpdateUserInfoList(String[] argUserName, String[] argUserSecret) {
      boolean _isChanged = false;
      for (int _newUserIndex = 0; _newUserIndex < argUserName.length; _newUserIndex++) {
         if (ServerAPIHelper.TestIsUserNameExisted(argUserName[_newUserIndex])) {
            ServerItem.lstUserInfo.add(new String[]{argUserName[_newUserIndex], argUserSecret[_newUserIndex]});
            _isChanged = true;
         }
      }
      return _isChanged;
   }

}