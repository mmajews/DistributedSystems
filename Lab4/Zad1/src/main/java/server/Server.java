package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import impl.Echo;
import org.apache.log4j.Logger;

public class Server {

    private static Logger logger = Logger.getLogger(Server.class);

    private void serverStart() {
        logger.debug("serverStart() method invoked");
        int status = 0;
        Ice.Communicator communicator = null;

        Echo echo = new Echo();
        Gson gson = new GsonBuilder().create();
        System.out.println(gson.toJson(echo));

    }


    public static void main(String[] args) {
        logger.info("Starting server...");
        Server server = new Server();
        server.serverStart();
    }
}
