import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IBoardListener extends Remote {
    void onNewText(String text) throws RemoteException;

    void onYourTurn(List<Field> freeSpots, String visualRepresentation) throws RemoteException;
}