import java.rmi.Remote;
import java.rmi.RemoteException;

public interface INoteBoard extends Remote {
    public String getText() throws RemoteException;
    public void appendText( String newNote ) throws RemoteException;
    public void clean() throws RemoteException;
    public void clean(String nick) throws RemoteException, UserRejectedException;
    public void register( IUser u, INoteBoardListener l ) throws RemoteException, UserRejectedException;
}
