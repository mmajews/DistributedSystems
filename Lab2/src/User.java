import java.io.Serializable;
import java.rmi.RemoteException;

public class User implements IUser, Serializable {
    private String nick;
    private String symbol;
    private String gameId;

    public User(String nick, String gameId) {
        this.nick = nick;
        this.gameId = gameId;
    }

    @Override
    public String getNick() {
        return this.nick;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String getGameId() throws RemoteException {
        return gameId;
    }

    @Override
    public void setGameId(String gameId) throws RemoteException {
        this.gameId = gameId;
    }
}
