import sun.util.resources.cldr.lag.LocaleNames_lag;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.time.LocalDateTime;

public class MulticastChat {
    public static final String SEPARATOR = "::";
    private MulticastSocket socket;
    private BufferedReader in;
    private OutputStreamWriter out;
    private Thread listener;
    private InetAddress group;
    private int port;
    private Thread writer;
    private String nick;

    public MulticastChat(InetAddress group, int port, String nick) {
        this.group = group;
        this.port = port;
        this.nick = nick;
        System.out.println("Your nick: " + nick);
    }

    void netStart() throws IOException {
        socket = new MulticastSocket(port);
        socket.joinGroup(group);

        in = new BufferedReader(new InputStreamReader(new DatagramInputStream(socket), "UTF8"));
        out = new OutputStreamWriter(new DatagramOutputStream(socket, group, port, 512), "UTF8");
        listener = new Thread() {
            public void run() {
                receive();
            }
        };
        listener.start();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                String msg = bufferedReader.readLine();
                send(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    void stop() throws IOException {
        listener.interrupt();
        socket.leaveGroup(group);
        socket.close();
    }

    void send(String message) {
        try {
            final String messageBody = nick + SEPARATOR + message + SEPARATOR + LocalDateTime.now();
            int checksum = messageBody.hashCode();
            String toBeSend = messageBody + SEPARATOR + checksum + '\n';
            out.write(toBeSend);
            out.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void receive() {
        try {
            Thread myself = Thread.currentThread();
            while (listener == myself) {
                String message = in.readLine();
                if (!message.split(SEPARATOR)[0].equals(nick)) {
                    String[] splittedMessage = message.split(SEPARATOR);
                    String nick = splittedMessage[0];
                    String actualMessage = splittedMessage[1];
                    String time = splittedMessage[2];
                    Integer receivedChecksum = Integer.parseInt(splittedMessage[3]);
                    String messageToBeChecked = nick + SEPARATOR + actualMessage + SEPARATOR + time;
                    Integer calculatedChecksum = messageToBeChecked.hashCode();
                    final int differenceInChecksum = calculatedChecksum - receivedChecksum;
                    if (differenceInChecksum != 0) {
                        System.out.println("Malformed message received..");
                    } else {
                        System.out.println(String.format("User %s at %s send message: %s", nick, time, actualMessage));
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}