package activitystreamer;

import activitystreamer.client.ClientCommandHelper;
import activitystreamer.client.ClientItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client {

    private static final Logger log = LogManager.getLogger();

    public static void main(String[] args) {
        ClientCommandHelper.SetReadCommands(args);
        log.info("starting client");
        ClientItem _clientItem = ClientItem.getInstance();
    }
}