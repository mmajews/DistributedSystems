import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;


public class Main {
    public static final int SIZE_OF_BUFFER = 1;
    private static final String separator = "%%%";
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        if (args.length != 2) {
            System.out.println("Input parameters: <IP> <port>");
            System.exit(-1);
        }

        try (ServerSocket ssocket = new ServerSocket(Integer.parseInt(args[1]))) {
            Socket socket = ssocket.accept();
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            logger.info("Server is up and waiting for data");

            //Receiving length of file
            byte[] lengthOfFileByte = new byte[2];
            inputStream.read(lengthOfFileByte);
            String inBinaryForm = "";
            for (byte b : lengthOfFileByte) {
                String num = Integer.toBinaryString(b & 0xFF);
                if (num.length() < 8) {
                    int toAddZeroes = 8 - num.length();
                    for (int i = 0; i < toAddZeroes; i++) {
                        num = "0" + num;
                    }
                }
                inBinaryForm = num + inBinaryForm;
            }
            long lengthOfFile = convertToLong(inBinaryForm);
            logger.info("Length of file to be received: " + lengthOfFile);

            byte[] wholeDataReceived;

            byte[] receivedData = new byte[SIZE_OF_BUFFER];
            ByteArrayOutputStream receivedDataByteArrayOutputStream = new ByteArrayOutputStream();
            int n = 0;
            while(inputStream.read(receivedData) > 0){
//                if(n>lengthOfFile){
//                    break;
//                }
                logger.info("Receiving..." + n++);
                if(receivedData[0]==38 && receivedData[1] ==19 && receivedData[2] == 64){
                    logger.info("End of transimission!");
                    break;
                }
                receivedDataByteArrayOutputStream.write(receivedData);
                receivedData = new byte[SIZE_OF_BUFFER];
            }

            wholeDataReceived = receivedDataByteArrayOutputStream.toByteArray();
            String fileContent = IOUtils.toString(wholeDataReceived, String.valueOf(StandardCharsets.UTF_8));
            final String[] splittedFileContent = fileContent.split("%%%");
            final String[] pathSplitted = splittedFileContent[0].split("/");
            final String filename = pathSplitted[pathSplitted.length - 1];
            logger.info(String.format("Filename of file received: %s", filename));
            logger.info("Creating file filled with content received on socket");
            final byte[] bytesToBeWritten = splittedFileContent[1].getBytes(StandardCharsets.UTF_8);
            File file = new File(filename);
            FileUtils.writeByteArrayToFile(file, bytesToBeWritten);
            logger.info("Writing to file finished");
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

    private static long convertToLong(String inBinary) {
        int index = inBinary.length() - 1;
        long num = 0;
        for (int pow = 0; index >= 0; index--, pow++) {
            num += Math.pow(2, pow) * (inBinary.charAt(index) - 48);
        }
        return num;
    }
}
