package activitystreamer;

import activitystreamer.client.ClientCommandHelper;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import activitystreamer.client.ClientItem;
import activitystreamer.util.Settings;

public class Client {

    private static final Logger log = LogManager.getLogger();

    public static void main(String[] args) {
        ClientCommandHelper.SetReadCommands(args);
        log.info("starting client");
        ClientItem c = ClientItem.getInstance();
    }

}