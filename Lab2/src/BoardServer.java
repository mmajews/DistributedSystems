import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;

public class BoardServer {
    static IBoardHandler nbi;

    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                System.out.println("Incorrect number of arguments");
            }
            System.out.println("Starting server...");
            nbi = new BoardHandlerImpl();
            IBoardHandler iBoardHandler = (IBoardHandler) UnicastRemoteObject.exportObject(nbi, 0);
            final String address = args[0];
//            final String address = "rmi://127.0.0.1:1099/note";
            Naming.rebind(address, iBoardHandler);
            System.out.println("Server started.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
