import java.io.Serializable;

/**
 * Created by konradmarzec on 15.03.2016.
 */
public class User implements IUser, Serializable {
    private String nick;

    public User(String nick) {
        this.nick = nick;
    }

    @Override
    public String getNick() {
        return this.nick;
    }
}
