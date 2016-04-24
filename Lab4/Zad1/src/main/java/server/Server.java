package server;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import impl.Echo;
import org.apache.log4j.Logger;

public class Server {

    private static Logger logger = Logger.getLogger(Server.class);

    private void serverStart(String[] args) {
        Ice.Communicator communicator = null;
        try {
            logger.debug("serverStart() method invoked");
            communicator = Ice.Util.initialize(args);
            final String connProp = "tcp -h localhost -p 10000:udp -h localhost -p 10000";

            Preconditions.checkNotNull(communicator, "Communicator must not be null!");

            Ice.ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("Apr1", connProp);
            adapter.addServantLocator(new Task1Locator("t1", adapter), "c1");
            adapter.addServantLocator(new Task2Locator("t2"),"c2");
            adapter.activate();

            logger.info("Entering processing loop...");
            communicator.waitForShutdown();
        } catch (Exception e) {
            logger.error("Error while starting server. Exiting..", e);
            System.exit(1);
        } finally {
            try {
                if (communicator != null) {
                    communicator.destroy();
                }
            } catch (Exception e) {
                logger.error("Error while detroying communicator", e);
                System.exit(1);
            }
        }
    }


    public static void main(String[] args) {
        logger.info("Starting server...");
        Server server = new Server();
        server.serverStart(args);
    }
}
