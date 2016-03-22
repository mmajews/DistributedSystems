import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IBoardListener extends Remote {
    void onNewText(String text) throws RemoteException;

    void onYourTurn(List<Field> freeSpots, String visualRepresentation, String gameId) throws RemoteException;

    void onYourTurnComputerGame(List<Field> freeSpots, String visualRepresentation, String gameId) throws RemoteException;

    void onGameEnd(String winnerSymbol) throws RemoteException;

    void onTie() throws RemoteException;
}