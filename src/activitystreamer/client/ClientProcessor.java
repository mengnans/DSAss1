package activitystreamer.client;

import activitystreamer.util.Settings;
import com.google.gson.JsonObject;

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
                //这里功能不全，除了显示结果，还应该解析数据，并且连接到新的服务器上
                apiHelper.SetDisplayMessage("Redirected to host:" + argJsonObject.get("hostname") + " prot:" + argJsonObject.get("port"));
                break;
            case "REGISTER_FAILED":
                apiHelper.SetDisplayMessage("Failed to register because " + argJsonObject.get("info"));
                break;
            case "REGISTER_SUCCESS":
                //注册成功了是不是应该直接登陆上？
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
        String _command = argJsonObject.get("command").toString();
        System.out.println(_command);
        switch (_command) {
            case "REGISTER":
                apiHelper.SendMessage(argJsonObject);
                break;
            case "LOGIN":
                apiHelper.SendMessage(argJsonObject);
                break;
            case "LOGOUT":
                apiHelper.SendMessage(argJsonObject);
                apiHelper.CloseConnection();
                break;
            case "ACTIVITY_MESSAGE":
                apiHelper.SendMessage(argJsonObject);
                break;
            default:
                apiHelper.SetDisplayMessage("Illegal message");
                break;
        }
    }
}