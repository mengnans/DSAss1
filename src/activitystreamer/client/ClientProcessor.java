package activitystreamer.client;

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
     * This function will be called when the client wants to register a new username
     *
     * @param reJsonObject
     */
    public static void ProcessRegister(JsonObject reJsonObject) {

    }

    /**
     * This function will be called when the client wants to connect a server
     *
     * @param
     */
    public static void ProcessConnection() {
        BufferedReader fileReader = new BufferedReader(new FileReader("config"));
        //Read the only line from the file
        String configLine = fileReader.readLine();

        if(configLine != null) {

            //Split the string into substrings delimited by tabs
            String[] configParams = configLine.split("\t");

            //We should have two substrings, one for the IP and one for the port
            if(configParams.length == 2) {
                //the server is the first parameter in the line
                String serverAddress = configParams[0];
                //the port is the second parameter in the line
                int serverPort = Integer.parseInt(configParams[1]);

                boolean isConnection = apiHelper.SetConnection(serverAddress,serverPort);
            }
        }
    }

    /**
     * This function will be called when a message is received through the network
     *
     * @param argJsonObject
     */
    public static void ProcessNetworkMessage(JsonObject argJsonObject) {
        apiHelper.SetDisplayMessage(argJsonObject);
    }

    /**
     * This function will be called when a message is received through the GUI
     *
     * @param argJsonObject
     */
    public static void ProcessUserMessage(JsonObject argJsonObject) {
        //Use a scanner to read input from the console
        Scanner scanner = new Scanner(System.in);
        String inputStr = null;

        //While the user input differs from "exit"
        while (!(inputStr = scanner.nextLine()).equals("exit")) {

            // Send the input string to the server by writing to the socket output stream
            apiHelper.SendActivityObject(argJsonObject);
            // writer.write(inputStr + "\n");
            // writer.flush();
        }
    }

}