import java.io.Serializable;

public class User implements IUser, Serializable {
    private String nick;
    private String symbol;

    public User(String nick) {
        this.nick = nick;
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
}
