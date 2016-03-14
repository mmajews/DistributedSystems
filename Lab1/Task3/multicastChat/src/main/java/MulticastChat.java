import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastChat {
    private MulticastSocket socket;
    private BufferedReader in;
    private OutputStreamWriter out;
    private Thread listener;
    private InetAddress group;
    private int port;
    private String nick;

    public MulticastChat(InetAddress group, int port, String nick) {
        this.group = group;
        this.port = port;
        this.nick = nick;
    }

    private Thread writer;


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

    void netStop() throws IOException {
        listener.interrupt();
        listener = null;
        socket.leaveGroup(group);
        socket.close();
    }

    void send(String message) {
        try {
            out.write(nick+':'+message+"\n");
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
                if (!message.split(":")[0].equals(nick)) {
                    System.out.println(message);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}