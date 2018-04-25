package activitystreamer.client;

import activitystreamer.server.ServerItem;
import activitystreamer.util.JsonHelper;
import activitystreamer.util.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.Socket;

public class ClientItem extends Thread {
    private static final Logger log = LogManager.getLogger();
    private static ClientItem clientItem;
    private ClientAPIHelper clientProcessor;
    private FormWindow formWindowItem;
    private Socket socket;

    private DataInputStream in;
    private DataOutputStream out;
    private BufferedReader inreader;
    private PrintWriter outwriter;

    private boolean term = false;

    public static ClientItem getInstance() {
        if (clientItem == null) {
            clientItem = new ClientItem();
        }
        return clientItem;
    }

    public ClientItem() {
        clientProcessor = new ClientAPIHelper(this);
        ClientProcessor.SetInitial(clientProcessor);
        formWindowItem = new FormWindow();
        start();
    }

    public boolean SetConnect(String argHost, int argPort) {
        try {
            socket = new Socket(argHost, argPort);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            inreader = new BufferedReader(new InputStreamReader(in));
            outwriter = new PrintWriter(out, true);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void SendMessageToServer(JsonObject argMessageObject) {
        if (term == false) {
            String _msg = JsonHelper.ObjectToString(argMessageObject);
            outwriter.println(_msg);
            outwriter.flush();
        }
    }

    public void disconnect() {
        term = true;
    }

    public void run() {
        try {
            String data;
            while (!term && (data = inreader.readLine()) != null) {
                JsonObject _json = JsonHelper.StringToObject(data);
                ClientProcessor.ProcessNetworkMessage(_json);
            }
            log.debug("connection closed to " + Settings.socketAddress(socket));
            in.close();
        } catch (IOException e) {
            log.error("connection " + Settings.socketAddress(socket) + " closed with exception: " + e);
        }
    }

    public void ProcessUserMessage(JsonObject argMessageObject) {
        ClientProcessor.ProcessUserMessage(argMessageObject);
    }

    /**
     * Show given json object on screen
     *
     * @param argMessageObject The json message object
     */
    public void SetDisplay(JsonObject argMessageObject) {
        formWindowItem.setOutputText(argMessageObject);
    }

    /**
     * Show given json object on screen
     *
     * @param argMessage The json message object
     */
    public void SetDisplay(String argMessage) {
        formWindowItem.setOutputText(argMessage);
    }

}