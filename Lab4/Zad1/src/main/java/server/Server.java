package server;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import server.locators.*;

public class Server {
    /*
	1: lazy initialization
	2: nowy serwant dla każdego uzycia, jakieś obliczenie co długo trwa
	3: balansowanie obciążenia
	4: jeden servant dla wielu uczestników
	5: ograncziona pamiec i koniecznosc zapisywania dawno nie uzytych servantów do pliku
	*/
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
            adapter.addServantLocator(new Task3Locator("t3"),"c3");
            adapter.addServantLocator(new Task4Locator("t4"),"c4");
            adapter.addServantLocator(new Task5Locator("t5"),"c5");
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
