package activitystreamer.client;

import activitystreamer.util.Settings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class ClientProcessor {

    private static ClientAPIHelper apiHelper;

    public static void SetInitial(ClientAPIHelper argApiHelper) {
        apiHelper = argApiHelper;
    }

    /**
     * This function will be called when the client wants to connect a server
     */
    public static void ProcessConnection() {
        String serverAddress = Settings.getRemoteHostname();
        int serverPort = Settings.getRemotePort();

        boolean isConnection = apiHelper.SetConnection(serverAddress, serverPort);
        if (isConnection) {
            apiHelper.SetDisplayMessage("The client successfully connects to the server");
        } else {
            apiHelper.SetDisplayMessage("The client fails to connect the server");
        }
    }

    /**
     * This function will be called when a message is received through the network
     *
     * @param argJsonObject The json object received
     */
    public static void ProcessNetworkMessage(JsonObject argJsonObject) {
        switch (argJsonObject.get("command").toString()) {
            case "INVALID_MESSAGE":
                apiHelper.SetDisplayMessage(argJsonObject.get("info").toString());
                break;
            case "AUTHENTICATION_FAIL":
                apiHelper.CloseConnection();
                break;
            case "LOGIN_SUCCESS":
                apiHelper.SetDisplayMessage(argJsonObject.get("info").toString());
                break;
            case "LOGIN_FAILED":
                apiHelper.SetDisplayMessage("Failed to log in: " + argJsonObject.get("info"));
                apiHelper.CloseConnection();
                break;
            case "ACTIVITY_BROADCAST":
                apiHelper.SetDisplayMessage(argJsonObject);
                break;
            case "REDIRECT":
                apiHelper.SetDisplayMessage("Redirected to host:" + argJsonObject.get("hostname") + " prot:" + argJsonObject.get("port"));
                break;
            case "REGISTER_FAILED":
                apiHelper.SetDisplayMessage("Failed to register because " + argJsonObject.get("info"));
                break;
            case "REGISTER_SUCCESS":
                apiHelper.SetDisplayMessage(argJsonObject.get("info").toString());
                break;
            default:
        }
    }

    /**
     * This function will be called when a message is received through the GUI
     *
     * @param argJsonObject The json object received
     */
    public static void ProcessUserMessage(JsonObject argJsonObject) {
        switch (argJsonObject.get("command").toString()) {
            case "REGISTER":
                apiHelper.SendRegRequest(argJsonObject);
                //apiHelper.SendRegRequest(argJsonObject.get("username"),argJsonObject.get("secret"));
                break;
            case "LOGIN":
                apiHelper.SendLoginRequest(argJsonObject);
                break;
            case "LOGOUT":
                apiHelper.SendLogoutRequest(argJsonObject);
                apiHelper.CloseConnection();
                break;
            case "ACTIVITY_MESSAGE":
                apiHelper.SendActivityObject(argJsonObject);
                break;
            default:
                apiHelper.InvalidMessage(argJsonObject);
        }
    }
}