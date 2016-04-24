package client;

import org.apache.log4j.Logger;

public class Client {
    private static final Logger logger = Logger.getLogger(Client.class);

    public static void main(String[] args) {
        logger.info("Starting client...");
        int status =0 ;
        Ice.Communicator communicator;

        communicator = Ice.Util.initialize(args);
        final String proxyConfString = "c1/o1:tcp -h localhost -p 10000:udp -h localhost -p 10000:ssl -h localhost -p 10001";
        Ice.ObjectPrx base = communicator.stringToProxy(proxyConfString);

    }

}
