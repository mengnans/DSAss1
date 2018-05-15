package activitystreamer;

import activitystreamer.server.ServerCommandHelper;
import activitystreamer.server.ServerItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server {
    private static final Logger log = LogManager.getLogger();

    public static void main(String[] args) {
        log.info("reading command line options");
        ServerCommandHelper.SetReadCommands(args);

        log.info("starting server");
        final ServerItem _serverItem = ServerItem.getInstance();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                _serverItem.SetServerShutdown();
                _serverItem.interrupt();
            }
        });
    }
}