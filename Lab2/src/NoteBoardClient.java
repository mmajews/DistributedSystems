import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;

public class NoteBoardClient {

    static class Listener implements INoteBoardListener, Serializable{

        @Override
        public void onNewText(String text) {
            System.out.println(text);
        }
    }

    public static void main(String[] args) {
        try {
            INoteBoard nb = (INoteBoard) Naming.lookup( "rmi://127.0.0.1/note" );
            System.err.println( "dodajemy: aqq1, aqq2, aqq3" );
            nb.appendText( "aqq1" );
            INoteBoardListener l = (INoteBoardListener) UnicastRemoteObject.exportObject(new Listener(), 0);
            nb.register(new User("user1"), l);
            nb.appendText( "aqq2" );
            nb.appendText( "aqq3" );
            INoteBoardListener l1 = (INoteBoardListener) UnicastRemoteObject.exportObject(new Listener(), 0);
            nb.register(new User("user2"), l1);
            nb.appendText( "aqq2" );
            nb.appendText( "aqq3" );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
