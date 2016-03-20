import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class NoteBoardImpl implements INoteBoard {
    private StringBuffer buf;
    private Map<String, INoteBoardListener> map = new HashMap<>();
    public NoteBoardImpl() {
        buf = new StringBuffer();
    }

    @Override
    public String getText() throws RemoteException {
        return buf.toString();
    }

    public void appendText(String newNote) throws RemoteException {
        map.forEach((k, v) -> {
            try {
                v.onNewText(newNote);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        buf.append("\n"+newNote);
    }

    public void clean() throws RemoteException {
        buf = new StringBuffer();
    }

    @Override
    public void clean(String nick) throws RemoteException, UserRejectedException {
            map.remove(nick);
    }

    @Override
    public void register(IUser u, INoteBoardListener l) throws RemoteException, UserRejectedException {
        if (!map.containsKey(u.getNick())) {
            map.put(u.getNick(), l);
        } else {
            throw new UserRejectedException();
        }
    }
}
