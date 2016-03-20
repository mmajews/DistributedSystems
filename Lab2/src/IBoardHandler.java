import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBoardHandler extends Remote {
    String register(IUser u, IBoardListener l) throws RemoteException, UserRejectedException;

    void requestFirstMove() throws RemoteException;

    void sendMove(IUser user, int order) throws RemoteException;
}
