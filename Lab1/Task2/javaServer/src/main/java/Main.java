import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;


public class Main {
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
            byte[] receivedData = new byte[2048];
            logger.info("Server is up and waiting for data");
            int receivedBytes = inputStream.read(receivedData);

            String fileContent = IOUtils.toString(receivedData, String.valueOf(StandardCharsets.UTF_8));
            final String[] splittedFileContent = fileContent.split("%%%");
            final String[] pathSplitted = splittedFileContent[0].split("/");
            final String filename = pathSplitted[pathSplitted.length - 1];
            logger.info(String.format("Size of received data: %s", receivedBytes));
            logger.info(String.format("Filename of file received: %s", filename));
            logger.info("Creating file filled with content received on socket");
            final byte[] bytesToBeWritten = splittedFileContent[1].getBytes(StandardCharsets.UTF_8);
            File file = new File(filename);
            FileUtils.writeByteArrayToFile(file, bytesToBeWritten);
            logger.info("Writing to file finished");

            outputStream = socket.getOutputStream();
            outputStream.write("File transfer finished!".getBytes());

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
}
