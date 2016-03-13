
import com.google.common.primitives.Longs;
import com.sun.deploy.util.ArrayUtil;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;


public class Main {
    private static final String separator = "%%%";
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        if (args.length != 1) {
            System.out.println("Input parameters: <port>");
            System.exit(-1);
        }

        System.out.println("Selected port: " + args[0]);
        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]))) {
            while (true) {
                logger.info("Server is up and waiting for data");
                Socket socket = serverSocket.accept();
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
                byte[] howManyBytesWillReceive = new byte[4];
                inputStream.read(howManyBytesWillReceive);
                int howManyBytes = new Byte(howManyBytesWillReceive[0]).intValue();
                logger.info("Server will receive " + howManyBytes + " bytes");

                //Receiving number
                byte[] numberToBeReceived = new byte[howManyBytes];
                inputStream.read(numberToBeReceived);
                String inBinaryForm = "";
                for (byte b : numberToBeReceived) {
                    String num = Integer.toBinaryString(b & 0xFF);
                    if (num.length() < 8) {
                        int toAddZeroes = 8 - num.length();
                        for (int i = 0; i < toAddZeroes; i++) {
                            num = "0" + num;
                        }
                    }
                    inBinaryForm = num + inBinaryForm;
                }

                long numberRecevied = convertToLong(inBinaryForm);
                logger.info(String.format("Number received: %d", numberRecevied));
                char getNthDigitOfPi = getNthDigitOfPi(numberRecevied);
                System.out.println(getNthDigitOfPi);
                outputStream.write(getNthDigitOfPi);
            }
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private static char getNthDigitOfPi(long numberRecevied) {
        Bpp bpp = new Bpp();
        return String.valueOf(bpp.getDecimal(numberRecevied)).charAt(0);
    }

    private static long convertToLong(String inBinary) {
        int index = inBinary.length() - 1;
        long num = 0;
        for (int pow = 0; index >= 0; index--, pow++) {
            num += Math.pow(2, pow) * (inBinary.charAt(index) - 48);
        }
        return num;
    }

}
