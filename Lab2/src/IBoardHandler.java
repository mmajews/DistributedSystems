import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBoardHandler extends Remote {
    String getText() throws RemoteException;

    void appendText(String newNote) throws RemoteException;

    void clean() throws RemoteException;

    void clean(String nick) throws RemoteException, UserRejectedException;

    String register(IUser u, IBoardListener l) throws RemoteException, UserRejectedException;

    void requestFirstMove() throws RemoteException;
}
