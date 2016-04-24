package client;

import Zad1.IEchoPrx;
import Zad1.IEchoPrxHelper;
import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {
    private static final Logger logger = Logger.getLogger(Client.class);

    public static void main(String[] args) {
        logger.info("Starting client...");
        int status =0 ;
        Ice.Communicator communicator;

        communicator = Ice.Util.initialize(args);
        final String proxyConfString = "c5/o1:tcp -h localhost -p 10000:udp -h localhost";
        Ice.ObjectPrx base = communicator.stringToProxy(proxyConfString);

        IEchoPrx echo = IEchoPrxHelper.checkedCast(base);
        Preconditions.checkNotNull(echo,"Echo should not be null!");

        logger.info("Initialization completed!");

        String line = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        do {
            try {
                System.out.print("==> ");
                System.out.flush();
                line = in.readLine();

                if(line == null){
                    line = "";
                    break;
                }
                if (line.equals("append")){
                    String str = echo.appendString("Appended");
                    System.out.println(String.format("Result %s", str));
                }
                if(line.equals("remove")){
                    String str = echo.removeLastLetter();
                    System.out.println(String.format("Result %s", str));
                }

            } catch (IOException e) {
                logger.error("Error while reading from line", e);
            }
        } while (!line.equals("x"));

    }

}
