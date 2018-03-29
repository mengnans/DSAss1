package activitystreamer;

import activitystreamer.server.CommandServerHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import activitystreamer.server.Control;

public class Server {
   private static final Logger log = LogManager.getLogger();

   public static void main(String[] args) {
      log.info("reading command line options");
      CommandServerHelper.SetReadCommands(args);

      JsonObject _object = new JsonObject();
      _object.addProperty("name", "Ruby");
      _object.addProperty("IQ", 1.5);
      String _content=_object.toString();
      JsonParser parser = new JsonParser();

      JsonObject _obj = (JsonObject) parser.parse(_content);

      log.info("starting server");
      final Control c = Control.getInstance();
      // the following shutdown hook doesn't really work, it doesn't give us enough time to
      // cleanup all of our connections before the jvm is terminated.
      Runtime.getRuntime().addShutdownHook(new Thread() {
         public void run() {
            c.setTerm(true);
            c.interrupt();
         }
      });
   }

}