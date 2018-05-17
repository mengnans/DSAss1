package activitystreamer.server;

import activitystreamer.util.Settings;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerConnection extends Thread {
   private static final Logger log = LogManager.getLogger();
   private DataInputStream in;
   private DataOutputStream out;
   private BufferedReader inReader;
   private PrintWriter outWriter;
   private boolean open;
   private Socket socket;
   private boolean term = false;

   public boolean isRegistered = false;
   public int clientAmount = 0;
   public int portForClient = -1;
   public ArrayList<JsonObject> lstReceivedMessage = new ArrayList<>();
   public ArrayList<JsonObject> lstToBeSentMessage = new ArrayList<>();
   public ConnectionType connectionType = ConnectionType.Undefined;

   public ServerConnection(Socket socket) throws IOException {
      in = new DataInputStream(socket.getInputStream());
      out = new DataOutputStream(socket.getOutputStream());
      inReader = new BufferedReader(new InputStreamReader(in));
      outWriter = new PrintWriter(out, true);
      this.socket = socket;
      open = true;
      start();
   }

   public Socket getSocket() {
      return socket;
   }

   public void run() {
      try {
         String data;
         while (term == false && (data = inReader.readLine()) != null) {
            term = ServerItem.getInstance().ReceivedMessage(this, data);
         }
         log.debug("connection closed to " + Settings.socketAddress(socket));
         ServerItem.getInstance().connectionClosed(this);
         in.close();
      } catch (IOException e) {
         log.error("connection " + Settings.socketAddress(socket) + " closed with exception: " + e);
         ServerItem.getInstance().connectionClosed(this);
      }
      open = false;
   }

   /*
    * returns true if the message was written, otherwise false
    */
   public boolean writeMsg(String msg) {
      if (open) {
         outWriter.println(msg);
         outWriter.flush();
         return true;
      }
      return false;
   }

   public void closeCon() {
      if (open) {
         log.info("closing connection " + Settings.socketAddress(socket));
         try {
            term = true;
            inReader.close();
            out.close();
            ServerItem.getInstance().connectionClosed(this);
         } catch (IOException e) {
            // already closed?
            log.error("received exception closing the connection " + Settings.socketAddress(socket) + ": " + e);
         }
      }
   }

   public enum ConnectionType {
      Undefined, ConnectedToServer, ConnectedToClient,
   }

}