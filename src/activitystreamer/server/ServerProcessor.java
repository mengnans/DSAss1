package activitystreamer.server;

import activitystreamer.util.Settings;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class ServerProcessor {

    private static boolean already_authenticate = false;
    private static boolean already_login = false;
    private static boolean already_register = false;
    private static boolean isLockDenied = false;
    private static int numOfConnectingServer = 0;
    private static int numOfLockResponse = 0;
    private static ServerAPIHelper apiHelper = new ServerAPIHelper();
    private static JsonObject AUTHENTICATE = new JsonObject();
    private static JsonObject INVALID_MESSAGE = new JsonObject();
    private static JsonObject AUTHENTICATION_FAIL = new JsonObject();
    private static JsonObject LOGIN_SUCCESS = new JsonObject();
    private static JsonObject LOGIN_FAILED = new JsonObject();
    private static JsonObject SERVER_ANNOUNCE = new JsonObject();
    private static JsonObject LOCK_REQUEST = new JsonObject();
    private static JsonObject LOCK_DENIED = new JsonObject();
    private static JsonObject LOCK_ALLOWED = new JsonObject();
    private static JsonObject REGISTER_FAILED = new JsonObject();
    private static JsonObject REGISTER_SUCCESS = new JsonObject();
    private static JsonObject REDIRECT = new JsonObject();
    private static JsonObject ACTIVITY_BROADCAST = new JsonObject();

    /**
     * This function will be called when the server succeed in connecting to another server
     *
     * @param argConnection The ServerConnection object of this new connection
     */
    public static void ProcessConnectToServer(ServerConnection argConnection) {
        //send authenticate json object to other server
        AUTHENTICATE.addProperty("command", "AUTHENTICATE");
        AUTHENTICATE.addProperty("secret", Settings.getRemoteServerSecret());
        apiHelper.SendMessage(argConnection, AUTHENTICATE);
    }

    public static void ProcessServerAnnounce(ArrayList<ServerConnection> connections) {
        //send announce to all connected other server
        SERVER_ANNOUNCE.addProperty("command", "SERVER_ANNOUNCE");
        SERVER_ANNOUNCE.addProperty("id", Settings.getServerID());
        if (ServerItem.getConnectingClient() == null) {
            SERVER_ANNOUNCE.addProperty("load", 0);
        } else {
            SERVER_ANNOUNCE.addProperty("load", ServerItem.getConnectingClient().size());
        }
        SERVER_ANNOUNCE.addProperty("hostname", Settings.getLocalHostname());
        SERVER_ANNOUNCE.addProperty("port", Settings.getLocalPort());
        for (ServerConnection tempCon : connections) {
            if (tempCon.getConnectionType().equals("withServer"))
                apiHelper.SendMessage(tempCon, SERVER_ANNOUNCE);
        }
    }

    public static void ProcessLockRelatedMessage(ArrayList<ServerConnection> connections, JsonObject lock_request) {
        for (ServerConnection tempCon : connections) {
            if (tempCon.getConnectionType().equals("withServer")) {
                apiHelper.SendMessage(tempCon, lock_request);
            }
        }
    }

    public static void ProcessRegisterSuccessMessage(JsonObject argJsonObject, ArrayList<ServerConnection> connections) {
        if (numOfLockResponse == numOfConnectingServer && isLockDenied == false) {
            REGISTER_SUCCESS.addProperty("command", "REGISTER_SUCCESS");
            REGISTER_SUCCESS.addProperty("info", "register success for" + argJsonObject.get("username").toString());
            apiHelper.SendMessage(ServerItem.getResigterQue().get(0), REGISTER_SUCCESS);
            numOfLockResponse = 0;
            isLockDenied = false;
            connections.remove(ServerItem.getResigterQue().get(0));
            ServerItem.getResigterQue().remove(0);
        }
    }

    public static void ProcessRegisterFailMessage(JsonObject argJsonObject, ArrayList<ServerConnection> connections) {
        if (numOfLockResponse == numOfConnectingServer && isLockDenied == true) {
            REGISTER_FAILED.addProperty("command", "REGISTER_FAILED");
            REGISTER_FAILED.addProperty("info", argJsonObject.get("username").toString() + "is already registered with the system");
            apiHelper.SendMessage(ServerItem.getResigterQue().get(0), REGISTER_FAILED);
            numOfLockResponse = 0;
            isLockDenied = false;
            connections.remove(ServerItem.getResigterQue().get(0));
            ServerItem.getResigterQue().remove(0);
        }
    }

    public static JsonObject balanceServerLoad() {
        int localServerLoad = ServerItem.getConnectingClient().size();
        for (JsonObject tempObject : ServerItem.getServerAnnounceInfo()) {
            if (localServerLoad - 2 > Integer.parseInt(tempObject.get("load").toString())) {
                REDIRECT.addProperty("command", "REDIRECT");
                REDIRECT.addProperty("hostname", tempObject.get("hostname").toString());
                REDIRECT.addProperty("port", tempObject.get("port").toString());
                return REDIRECT;
            }
        }
        return null;
    }

    public static JsonObject processActivityObject(JsonObject activityObject, ServerConnection connection, JsonObject argJsonObject) {
        if (connection.getConnectionType().equals("withClient")) {
            activityObject.addProperty("authenticated_user", argJsonObject.get("username").toString());
        }
        return activityObject;
    }

    /**
     * This function will be called when a message is received through the network
     *
     * @param argConnection The ServerConnection object of this message
     * @param argJsonObject The json object containing the message data
     * @return return True to terminate the connection; return false to remain connecting
     */
    public static boolean ProcessNetworkMessage(ServerConnection argConnection, JsonObject argJsonObject, ArrayList<ServerConnection> connections) {
        String currentSocketAddress = Settings.socketAddress(argConnection.getSocket());
        ArrayList<JsonObject> ClientLoginInfo = new ArrayList<JsonObject>();
        for (ServerConnection tempCon : connections) {
            if (tempCon.getConnectionType().equals("withServer")) {
                numOfConnectingServer++;
            }
        }

        //if json has command filed, process it
        if (argJsonObject.has("command")) {
            // parse command
            switch (argJsonObject.get("command").toString()) {
                //AUTHENTICATE command(from server to server)
                case "\"AUTHENTICATE\"":
                    //used to judge whether server has already authenticate
                    already_authenticate = false;
                    for (ServerConnection connectionItem : connections) {
                        if (Settings.socketAddress(connectionItem.getSocket()).equals(currentSocketAddress)) {
                            already_authenticate = true;
                        }
                    }
                    //server has already authenticated, send INVALID_MESSAGE
                    if (already_authenticate) {
                        INVALID_MESSAGE.addProperty("command", "INVALID_MESSAGE");
                        INVALID_MESSAGE.addProperty("info", "server has already successfully authenticated");
                        apiHelper.SendMessage(argConnection, INVALID_MESSAGE);
                        return true;
                    }
                    //json object did not has secret field, send INVALID_MESSAGE
                    else if (!argJsonObject.has("secret")) {
                        INVALID_MESSAGE.addProperty("command", "INVALID_MESSAGE");
                        INVALID_MESSAGE.addProperty("info", "the received message did not contain a secret");
                        apiHelper.SendMessage(argConnection, INVALID_MESSAGE);
                        return true;
                    }
                    //secret is incorrect
                    else if (!Settings.getRemoteServerSecret().equals(argJsonObject.get("secret").toString())) {
                        AUTHENTICATION_FAIL.addProperty("command", "AUTHENTICATION_FAIL");
                        AUTHENTICATION_FAIL.addProperty("info", "the supplied secret is incorrect : " + argJsonObject.get("secret").toString());
                        apiHelper.SendMessage(argConnection, AUTHENTICATION_FAIL);
                        return true;
                    }
                    // authenticate success, no reply, keep connection
                    else {
                        argConnection.setConnectionType("withServer");
                        return false;
                    }
                case "\"INVALID_MESSAGE\"":
                    return true;
                case "\"AUTHENTICATION_FAIL\"":
                    return true;
                case "\"SERVER_ANNOUNCE\"":
                    already_authenticate = false;
                    for (ServerConnection connectionItem : connections) {
                        if (Settings.socketAddress(connectionItem.getSocket()).equals(currentSocketAddress)) {
                            already_authenticate = true;
                        }
                    }
                    //receive announcement from unauthenticated server
                    if (!already_authenticate) {
                        INVALID_MESSAGE.addProperty("command", "INVALID_MESSAGE");
                        INVALID_MESSAGE.addProperty("info", "announce from unauthenticated server");
                        apiHelper.SendMessage(argConnection, INVALID_MESSAGE);
                        return true;
                    }
                    //Announcement message is incomplete
                    else if (!argJsonObject.has("id") || !argJsonObject.has("load") || !argJsonObject.has("hostname") || !argJsonObject.has("port")) {
                        INVALID_MESSAGE.addProperty("command", "INVALID_MESSAGE");
                        INVALID_MESSAGE.addProperty("info", "the server_announce message lack required filed");
                        apiHelper.SendMessage(argConnection, INVALID_MESSAGE);
                        return true;
                    }
                    //save the SERVER_ANNOUNCE object to local
                    else {
                        ServerItem.getServerAnnounceInfo().add(argJsonObject);
                        return false;
                    }

                case "\"LOGIN\"":
                    already_register = false;
                    JsonObject localObject = apiHelper.searchObjects("username", argJsonObject.get("username").toString(), ServerItem.getClientResigterInfo());
                    for (JsonObject clientRegisterInfo : ServerItem.getClientResigterInfo()) {
                        if (clientRegisterInfo.get("username").toString().equals(argJsonObject.get("username").toString())) {
                            already_register = true;
                        }
                    }
                    //login message did not have username field, send INVALID_MESSAGE
                    if (!argJsonObject.has("username")) {
                        INVALID_MESSAGE.addProperty("command", "INVALID_MESSAGE");
                        INVALID_MESSAGE.addProperty("info", "the received login message did not contain a username");
                        apiHelper.SendMessage(argConnection, INVALID_MESSAGE);
                        return true;
                    }
                    //login username is not anonymous and with no secret, send INVALID_MESSAGE
                    else if (!argJsonObject.get("username").equals("anonymous") && !argJsonObject.has("secret")) {
                        INVALID_MESSAGE.addProperty("command", "INVALID_MESSAGE");
                        INVALID_MESSAGE.addProperty("info", "the received login username with no provide secret");
                        apiHelper.SendMessage(argConnection, INVALID_MESSAGE);
                        return true;
                    }
                    //login as anonymous
                    else if (argJsonObject.get("username").equals("anonymous")) {
                        JsonObject Client = new JsonObject();
                        Client.addProperty("username", argJsonObject.get("username").toString());
                        Client.addProperty("secret", "");
                        ServerItem.getConnectingClient().add(Client);
                        LOGIN_SUCCESS.addProperty("command", "LOGIN_SUCCESS");
                        LOGIN_SUCCESS.addProperty("info", "logged in as anonymous");
                        apiHelper.SendMessage(argConnection, LOGIN_SUCCESS);
                        if (balanceServerLoad() != null) {
                            apiHelper.SendMessage(argConnection, balanceServerLoad());
                            return true;
                        }
                        return false;
                    }
                    //user name did not exit in local storage
                    else if (!already_register) {
                        LOGIN_FAILED.addProperty("command", "LOGIN_FAILED");
                        LOGIN_FAILED.addProperty("info", "username did not been registered");
                        apiHelper.SendMessage(argConnection, LOGIN_FAILED);
                        return true;
                    }
                    // username did not match with secret
                    else if (!already_register && !(localObject.get("secret").equals(argJsonObject.get("secret")))) {
                        LOGIN_FAILED.addProperty("command", "LOGIN_FAILED");
                        LOGIN_FAILED.addProperty("info", "attempt to login with wrong secret");
                        apiHelper.SendMessage(argConnection, LOGIN_FAILED);
                        return true;
                    }
                    //LOGIN_SUCCESS
                    else {
                        JsonObject Client = new JsonObject();
                        Client.addProperty("username", argJsonObject.get("username").toString());
                        Client.addProperty("secret", argJsonObject.get("secret").toString());
                        ServerItem.getConnectingClient().add(Client);
                        LOGIN_SUCCESS.addProperty("command", "LOGIN_SUCCESS");
                        LOGIN_SUCCESS.addProperty("info", "logged in as " + argJsonObject.get("username").toString());
                        apiHelper.SendMessage(argConnection, LOGIN_SUCCESS);
                        if (balanceServerLoad() != null) {
                            apiHelper.SendMessage(argConnection, balanceServerLoad());
                            return true;
                        }
                        return false;
                    }
                case "\"REDIRECT\"":
                    JsonObject Client = new JsonObject();
                    Client.addProperty("username", argJsonObject.get("username").toString());
                    Client.addProperty("secret", argJsonObject.get("secret").toString());
                    ServerItem.getConnectingClient().add(Client);
                    return false;
                case "\"LOGOUT\"":
                    return true;
                case "\"REGISTER\"":
                    already_register = false;
                    already_login = false;
                    for (ServerConnection connectionItem : connections) {
                        if (Settings.socketAddress(connectionItem.getSocket()).equals(currentSocketAddress)) {
                            already_login = true;
                        }
                    }
                    for (JsonObject clientRegisterInfo : ServerItem.getClientResigterInfo()) {
                        if (clientRegisterInfo.get("username").toString().equals(argJsonObject.get("username").toString())) {
                            already_register = true;
                        }
                    }
                    //receive register message from a client already logged in
                    if (already_login) {
                        INVALID_MESSAGE.addProperty("command", "INVALID_MESSAGE");
                        INVALID_MESSAGE.addProperty("info", "you have already logged in");
                        apiHelper.SendMessage(argConnection, INVALID_MESSAGE);
                        return true;
                    }
                    //the register message is incomplete
                    else if (!argJsonObject.has("username") || !argJsonObject.has("secret")) {
                        INVALID_MESSAGE.addProperty("command", "INVALID_MESSAGE");
                        INVALID_MESSAGE.addProperty("info", "the register lack required field");
                        apiHelper.SendMessage(argConnection, INVALID_MESSAGE);
                        return true;
                    }
                    //the username is already registered in the local server
                    else if (already_register) {
                        REGISTER_FAILED.addProperty("command", "REGISTER_FAILED");
                        REGISTER_FAILED.addProperty("info", argJsonObject.get("username").toString() + "is already registered with the system");
                        apiHelper.SendMessage(argConnection, REGISTER_FAILED);
                        return true;
                    } else {
                        JsonObject regClient = new JsonObject();
                        regClient.addProperty("username", argJsonObject.get("username").toString());
                        regClient.addProperty("secret", argJsonObject.get("secret").toString());
                        ServerItem.getClientResigterInfo().add(regClient);
                        ServerItem.getResigterQue().add(argConnection);

                        LOCK_REQUEST.addProperty("command", "ProcessLockRequest");
                        LOCK_REQUEST.addProperty("username", argJsonObject.get("username").toString());
                        LOCK_REQUEST.addProperty("secret", argJsonObject.get("secret").toString());
                        ProcessLockRelatedMessage(connections, LOCK_REQUEST);
                        return false;
                    }
                case "\"LOCK_REQUEST\"":
                    already_register = false;
                    already_authenticate = false;
                    for (ServerConnection connectionItem : connections) {
                        if (Settings.socketAddress(connectionItem.getSocket()).equals(currentSocketAddress)) {
                            already_authenticate = true;
                        }
                    }
                    for (JsonObject clientRegisterInfo : ServerItem.getClientResigterInfo()) {
                        if (clientRegisterInfo.get("username").toString().equals(argJsonObject.get("username").toString())) {
                            already_register = true;
                        }
                    }
                    //receive message from unauthenticated server
                    if (!already_authenticate) {
                        INVALID_MESSAGE.addProperty("command", "INVALID_MESSAGE");
                        INVALID_MESSAGE.addProperty("info", "announce from unauthenticated server");
                        apiHelper.SendMessage(argConnection, INVALID_MESSAGE);
                        return true;
                    }
                    //message is incomplete
                    else if (!argJsonObject.has("username") || !argJsonObject.has("secret")) {
                        INVALID_MESSAGE.addProperty("command", "INVALID_MESSAGE");
                        INVALID_MESSAGE.addProperty("info", "LOCK_REQUEST Message lack required field");
                        apiHelper.SendMessage(argConnection, INVALID_MESSAGE);
                        return true;
                    }
                    //username already registered
                    else if (already_register) {
                        LOCK_DENIED.addProperty("command", "LOCK_DENIED");
                        LOCK_DENIED.addProperty("username", argJsonObject.get("username").toString());
                        LOCK_DENIED.addProperty("secret", argJsonObject.get("secret").toString());
                        ProcessLockRelatedMessage(connections, LOCK_DENIED);
                        return false;
                    }
                    //username not registered
                    else {
                        LOCK_ALLOWED.addProperty("command", "LOCK_ALLOWED");
                        LOCK_ALLOWED.addProperty("username", argJsonObject.get("username").toString());
                        LOCK_ALLOWED.addProperty("secret", argJsonObject.get("secret").toString());
                        ProcessLockRelatedMessage(connections, LOCK_ALLOWED);
                        return false;
                    }
                case "\"LOCK_DENIED\"":
                    numOfLockResponse++;
                    isLockDenied = true;
                    already_authenticate = false;
                    for (ServerConnection connectionItem : connections) {
                        if (Settings.socketAddress(connectionItem.getSocket()).equals(currentSocketAddress)) {
                            already_authenticate = true;
                        }
                    }
                    //receive message from unauthenticated server
                    if (!already_authenticate) {
                        INVALID_MESSAGE.addProperty("command", "INVALID_MESSAGE");
                        INVALID_MESSAGE.addProperty("info", "LOCK_REQUEST from unauthenticated server");
                        apiHelper.SendMessage(argConnection, INVALID_MESSAGE);
                        ProcessRegisterFailMessage(argJsonObject, connections);
                        return true;
                    }
                    //LOCK_DENIED message is incomplete
                    else if (!argJsonObject.has("username") || !argJsonObject.has("secret")) {
                        INVALID_MESSAGE.addProperty("command", "INVALID_MESSAGE");
                        INVALID_MESSAGE.addProperty("info", "LOCK_REQUEST Message lack required field");
                        apiHelper.SendMessage(argConnection, INVALID_MESSAGE);
                        ProcessRegisterFailMessage(argJsonObject, connections);
                        return true;
                    }
                    //remove username from local storage
                    else {
                        for (JsonObject registerClient : ServerItem.getClientResigterInfo()) {
                            if (registerClient.get("username").toString().equals(argJsonObject.get("username").toString()) && registerClient.get("secret").toString().equals(argJsonObject.get("secret").toString())) {
                                ServerItem.getClientResigterInfo().remove(registerClient);
                                ProcessRegisterFailMessage(argJsonObject, connections);
                                return false;
                            }
                        }
                    }
                case "\"LOCK_ALLOW\"":
                    numOfLockResponse++;
                    already_authenticate = false;
                    for (ServerConnection connectionItem : connections) {
                        if (Settings.socketAddress(connectionItem.getSocket()).equals(currentSocketAddress)) {
                            already_authenticate = true;
                        }
                    }
                    //receive message from unauthenticated server
                    if (already_authenticate) {
                        INVALID_MESSAGE.addProperty("command", "INVALID_MESSAGE");
                        INVALID_MESSAGE.addProperty("info", "LOCK_REQUEST from unauthenticated server");
                        apiHelper.SendMessage(argConnection, INVALID_MESSAGE);
                        ProcessRegisterSuccessMessage(argJsonObject, connections);
                        return true;
                    }
                    //LOCK_ALLOW message is incomplete
                    else if (!argJsonObject.has("username") || !argJsonObject.has("secret")) {
                        INVALID_MESSAGE.addProperty("command", "INVALID_MESSAGE");
                        INVALID_MESSAGE.addProperty("info", "LOCK_REQUEST Message lack required field");
                        apiHelper.SendMessage(argConnection, INVALID_MESSAGE);
                        ProcessRegisterSuccessMessage(argJsonObject, connections);
                        return true;
                    } else {
                        ProcessRegisterSuccessMessage(argJsonObject, connections);
                        return false;
                    }
                case "\"ACTIVITY_MESSAGE\"":
                    already_login = false;
                    for (ServerConnection connectionItem : connections) {
                        if (Settings.socketAddress(connectionItem.getSocket()).equals(currentSocketAddress)) {
                            already_login = true;
                        }
                    }
                    JsonObject localStorage = apiHelper.searchObjects("username", argJsonObject.get("username").toString(), ServerItem.getClientResigterInfo());
                    //message did not have username field, send INVALID_MESSAGE
                    if (!argJsonObject.has("username") || !argJsonObject.has("secret") || !argJsonObject.has("activity")) {
                        INVALID_MESSAGE.addProperty("command", "INVALID_MESSAGE");
                        INVALID_MESSAGE.addProperty("info", "the received ACTIVITY_MESSAGE lack of required field");
                        apiHelper.SendMessage(argConnection, INVALID_MESSAGE);
                        return true;
                    }
                    //activity is null
                    else if (argJsonObject.get("activity") == null) {
                        INVALID_MESSAGE.addProperty("command", "INVALID_MESSAGE");
                        INVALID_MESSAGE.addProperty("info", "the received activity is null");
                        apiHelper.SendMessage(argConnection, INVALID_MESSAGE);
                        return true;
                    }
                    //client did not login
                    else if (already_login) {
                        AUTHENTICATION_FAIL.addProperty("command", "AUTHENTICATION_FAIL");
                        AUTHENTICATION_FAIL.addProperty("info", "you did not login");
                        apiHelper.SendMessage(argConnection, AUTHENTICATION_FAIL);
                        return true;
                    }
                    //username is not anonymous or username/secret pair not match with local record
                    else if (argJsonObject.get("username").toString().equals("anonymous") && !(argJsonObject.get("username").toString().equals(localStorage.get("username").toString()) && argJsonObject.get("secret").toString().equals(localStorage.get("secret").toString()))) {
                        AUTHENTICATION_FAIL.addProperty("command", "AUTHENTICATION_FAIL");
                        AUTHENTICATION_FAIL.addProperty("info", "you did not login");
                        apiHelper.SendMessage(argConnection, AUTHENTICATION_FAIL);
                        return true;
                    } else {
                        ACTIVITY_BROADCAST.addProperty("command", "ACTIVITY_BROADCAST");
                        ACTIVITY_BROADCAST.add("activity", argJsonObject.get("activity"));
                        ACTIVITY_BROADCAST = processActivityObject(ACTIVITY_BROADCAST, argConnection, argJsonObject);
                        for (ServerConnection tempCon : connections) {
                            if (!currentSocketAddress.equals(Settings.socketAddress(tempCon.getSocket()))) {
                                apiHelper.SendMessage(tempCon, ACTIVITY_BROADCAST);
                            }
                        }
                        return false;
                    }
                case "\"ACTIVITY_BROADCAST\"":
                    already_authenticate = false;
                    for (ServerConnection connectionItem : connections) {
                        if (Settings.socketAddress(connectionItem.getSocket()).equals(currentSocketAddress)) {
                            already_authenticate = true;
                        }
                    }
                    if (already_authenticate) {
                        INVALID_MESSAGE.addProperty("command", "INVALID_MESSAGE");
                        INVALID_MESSAGE.addProperty("info", "you are unauthenticated");
                        apiHelper.SendMessage(argConnection, INVALID_MESSAGE);
                        return true;
                    } else if (!argJsonObject.has("activity")) {
                        INVALID_MESSAGE.addProperty("command", "INVALID_MESSAGE");
                        INVALID_MESSAGE.addProperty("info", "ACTIVITY_BROADCAST message did not have activity field");
                        apiHelper.SendMessage(argConnection, INVALID_MESSAGE);
                        return true;
                    } else if (argJsonObject.get("activity") == null) {
                        INVALID_MESSAGE.addProperty("command", "INVALID_MESSAGE");
                        INVALID_MESSAGE.addProperty("info", "ACTIVITY_BROADCAST message is null");
                        apiHelper.SendMessage(argConnection, INVALID_MESSAGE);
                        return true;
                    } else {
                        ACTIVITY_BROADCAST.addProperty("command", "ACTIVITY_BROADCAST");
                        ACTIVITY_BROADCAST.add("activity", argJsonObject.get("activity"));
                        for (ServerConnection tempCon : connections) {
                            if (!currentSocketAddress.equals(Settings.socketAddress(tempCon.getSocket()))) {
                                apiHelper.SendMessage(tempCon, ACTIVITY_BROADCAST);
                            }
                        }
                        return false;
                    }

                default:
                    INVALID_MESSAGE.addProperty("command", "INVALID_MESSAGE");
                    INVALID_MESSAGE.addProperty("info", "unknown commands");
                    apiHelper.SendMessage(argConnection, INVALID_MESSAGE);
                    return true;
            }
        }
        //if json object does not has command field, send INVALID_MESSAGE
        else {
            INVALID_MESSAGE.addProperty("command", "INVALID_MESSAGE");
            INVALID_MESSAGE.addProperty("info", "the received message did not contain a command");
            apiHelper.SendMessage(argConnection, INVALID_MESSAGE);
            return true;
        }
    }

}