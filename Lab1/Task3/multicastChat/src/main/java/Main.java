import java.io.IOException;
import java.net.InetAddress;

public class Main {


    static final String DEFAULT_GROUP = "239.1.2.3";
    static final int DEFAULT_PORT = 1234;
    static final int DEFAULT_TTL = 1;

    public static void main (String[] args) throws IOException {
        if ((args.length > 3) || ((args.length <= 0))) {
            System.out.println("Wrong number of arguments!");
            System.out.println("<ip adress> <port> <nick>");
            System.exit (0);
        }
        String groupStr = args[0];
        InetAddress group = InetAddress.getByName (groupStr);
        int port = Integer.parseInt (args[1]);
        String nick = args[2];
        MulticastChat multicastChat = new MulticastChat(group,port,nick);
        multicastChat.netStart();
    }
}
