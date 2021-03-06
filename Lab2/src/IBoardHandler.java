import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBoardHandler extends Remote {
    String register(IUser u, IBoardListener l, String gameId, boolean ifComputerChallenge) throws RemoteException, UserRejectedException;

    void requestFirstMove(String gameId) throws RemoteException;

    void sendMove(IUser user, int order, String gameId, Boolean gameWithComputer) throws RemoteException;

    void nextTurnInGameWithComputer(String gameId) throws RemoteException;

    }