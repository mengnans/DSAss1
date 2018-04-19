package activitystreamer;

import activitystreamer.client.ClientCommandHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import activitystreamer.client.ClientItem;

public class Client {

    private static final Logger log = LogManager.getLogger();

    public static void main(String[] args) {
        ClientCommandHelper.SetReadCommands(args);
        log.info("starting client");
        ClientItem _clientItem = ClientItem.getInstance();
    }

}