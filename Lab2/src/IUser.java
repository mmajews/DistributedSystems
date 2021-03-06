import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IUser extends Remote {
    String getNick() throws RemoteException;

    String getSymbol() throws RemoteException;

    void setSymbol(String symbol) throws RemoteException;

    String getGameId() throws RemoteException;

    void setGameId(String gameId) throws RemoteException;
}
