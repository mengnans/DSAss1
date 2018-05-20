package activitystreamer.server;

import activitystreamer.util.JsonHelper;
import activitystreamer.util.Settings;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ServerItem extends Thread {
   private static final Logger log = LogManager.getLogger();
   private static ServerItem serverItem = null;
   private static boolean term = false;
   private static ServerListener listener;

   public static ArrayList<ServerConnection> connections = new ArrayList<>();
   public static ArrayList<String[]> lstUserInfo = new ArrayList<>();

   private ServerItem() {
      // start a listener
      try {
         lstUserInfo.add(new String[]{"anonymous", "null"});
         listener = new ServerListener();
      } catch (IOException e1) {
         log.fatal("failed to startup a listening thread: " + e1);
         System.exit(-1);
      }
      start();
   }

   public static ServerItem getInstance() {
      if (serverItem == null) {
         serverItem = new ServerItem();
      }
      return serverItem;
   }

   @Override
   public void run() {
      log.info("using activity interval of " + Settings.getActivityInterval() + " milliseconds");
      ConnectToServer();
      while (!term) {
         // do something with 5 second intervals in between
         ServerProcessor.ProcessServerAnnounce();
         try {
            Thread.sleep(Settings.getActivityInterval());
         } catch (InterruptedException e) {
            log.info("received an interrupt, system is shutting down");
            break;
         }

      }
      log.info("closing " + connections.size() + " connections");
      // clean up
      for (ServerConnection connection : connections) {
         connection.closeCon();
      }
      listener.setTerm(true);
   }

   /*
    * A new incoming connection has been established, and a reference is returned to it
    */
   public synchronized void ReceiveNewConnection(Socket s) throws IOException {
      log.debug("Received a new connection: " + Settings.socketAddress(s));
      ServerConnection _connection = new ServerConnection(s);
      connections.add(_connection);
   }

   public synchronized boolean ReceivedMessage(ServerConnection argConnection, String argMessageObject) {
      log.debug("Received a new message: " + argMessageObject);
      JsonObject _jsonObject = JsonHelper.StringToObject(argMessageObject);
      return ServerProcessor.ProcessNetworkMessage(argConnection, _jsonObject);
   }

   private synchronized void ConnectToServer() {
      // make a connection to another server if remote hostname is supplied
      if (Settings.getRemoteHostname() != null) {
         try {
            Socket _socket = new Socket(Settings.getRemoteHostname(), Settings.getRemotePort());
            log.debug("Connected to another server: " + Settings.socketAddress(_socket));
            ServerConnection _connection = new ServerConnection(_socket);
            _connection.connectionType = ServerConnection.ConnectionType.ConnectedToServer;
            connections.add(_connection);
            _connection.isRegistered = true;
            ServerProcessor_Server.DoAuthenticate(_connection);
            ServerProcessor_Server.SendRegisteredUserList(_connection);
         } catch (IOException e) {
            log.error("failed to make connection to " + Settings.getRemoteHostname() + ":" + Settings.getRemotePort() + " :" + e);
            System.exit(-1);
         }
      }
   }

   /*
    * The connection has been closed by the other party.
    */
   public synchronized void connectionClosed(ServerConnection con) {
      if (!term) connections.remove(con);
   }

   /**
    * Shutdown this server.
    * Actually, this function should not be called according to the requirement
    */
   public final void SetServerShutdown() {
      term = true;
   }

}