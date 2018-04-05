package activitystreamer.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import activitystreamer.util.Settings;

public class ServerItem extends Thread {
    private static final Logger log = LogManager.getLogger();
    private static ArrayList<ServerConnection> connections;
    private static boolean term = false;
    private static ServerListener listener;
    private static ServerProcesser processer;

    protected static ServerItem serverItem = null;

    public static ServerItem getInstance() {
        if (serverItem == null) {
            serverItem = new ServerItem();
        }
        return serverItem;
    }

    private ServerItem() {
        // initialize the connections array
        connections = new ArrayList<ServerConnection>();
        // start a listener
        try {
            listener = new ServerListener();
        } catch (IOException e1) {
            log.fatal("failed to startup a listening thread: " + e1);
            System.exit(-1);
        }
    }

    @Override
    public void run() {
        log.info("using activity interval of " + Settings.getActivityInterval() + " milliseconds");
        while (!term) {
            // do something with 5 second intervals in between
            try {
                Thread.sleep(Settings.getActivityInterval());
            } catch (InterruptedException e) {
                log.info("received an interrupt, system is shutting down");
                break;
            }
            if (!term) {
                log.debug("doing activity");
                term = doActivity();
            }
        }
        log.info("closing " + connections.size() + " connections");
        // clean up
        for (ServerConnection connection : connections) {
            connection.closeCon();
        }
        listener.setTerm(true);
    }

    public void initiateConnection() {
        // make a connection to another server if remote hostname is supplied
        if (Settings.getRemoteHostname() != null) {
            try {
                outgoingConnection(new Socket(Settings.getRemoteHostname(), Settings.getRemotePort()));
            } catch (IOException e) {
                log.error("failed to make connection to " + Settings.getRemoteHostname() + ":" + Settings.getRemotePort() + " :" + e);
                System.exit(-1);
            }
        }
    }

    /*
     * Processing incoming messages from the connection.
     * Return true if the connection should close.
     */
    public synchronized boolean process(ServerConnection con, String msg) {
        return true;
    }

    /*
     * The connection has been closed by the other party.
     */
    public synchronized void connectionClosed(ServerConnection con) {
        if (!term) connections.remove(con);
    }

    /*
     * A new incoming connection has been established, and a reference is returned to it
     */
    public synchronized ServerConnection SetNewConnection(Socket s) throws IOException {
        log.debug("incomming connection: " + Settings.socketAddress(s));
        ServerConnection c = new ServerConnection(s);
        connections.add(c);
        return c;
    }

    /*
     * A new outgoing connection has been established, and a reference is returned to it
     */
    public synchronized ServerConnection outgoingConnection(Socket s) throws IOException {
        log.debug("outgoing connection: " + Settings.socketAddress(s));
        ServerConnection c = new ServerConnection(s);
        connections.add(c);
        return c;
    }

    public boolean doActivity() {
        return false;
    }

    public final void setTerm(boolean t) {
        term = t;
    }

    public final ArrayList<ServerConnection> getConnections() {
        return connections;
    }
}