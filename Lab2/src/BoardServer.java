import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;

public class BoardServer {
    static IBoardHandler nbi;

    public static void main(String[] args) {
        try {
            System.out.println("Starting server...");
            nbi = new BoardHandlerImpl();
            IBoardHandler iBoardHandler = (IBoardHandler) UnicastRemoteObject.exportObject(nbi, 0);
            Naming.rebind("rmi://127.0.0.1:1099/note", iBoardHandler);
            System.out.println("Server started.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
