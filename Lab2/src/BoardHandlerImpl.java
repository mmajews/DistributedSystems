import java.rmi.RemoteException;
import java.util.*;

public class BoardHandlerImpl implements IBoardHandler {

    private StringBuffer buf;
    private String lastSelectedUser;
    private List<String> listOfNicks = new ArrayList<>();
    private Map<String, IUser> ownersOfSymbols = new HashMap<>();
    private final List<String> availableSymbolsToTake = Arrays.asList("X", "Y");
    private Map<String, IBoardListener> userListenerMap = new HashMap<>();
    private Board board = new Board();

    public BoardHandlerImpl() {
        buf = new StringBuffer();

    }

    @Override
    public String getText() throws RemoteException {
        return buf.toString();
    }

    public void appendText(String newNote) throws RemoteException {
        userListenerMap.forEach((k, v) -> {
            try {
                v.onNewText(newNote);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        buf.append("\n" + newNote);
    }

    public void clean() throws RemoteException {
        buf = new StringBuffer();
    }

    @Override
    public void clean(String nick) throws RemoteException, UserRejectedException {
        userListenerMap.remove(nick);
    }

    @Override
    public String register(IUser u, IBoardListener l) throws RemoteException, UserRejectedException {
        if (!userListenerMap.containsKey(u.getNick()) && userListenerMap.size() < 2 && ownersOfSymbols.size() < 2) {
            userListenerMap.put(u.getNick(), l);
            listOfNicks.add(u.getNick());
            String selectedSymbol = getFreeUserSymbol();
            ownersOfSymbols.put(selectedSymbol, u);
            return selectedSymbol;
        } else {
            return "";
        }
    }

    private void nextTurn() {
        if (userListenerMap.size() == 2) {
            System.out.println("Next turn!");
            if (lastSelectedUser == null) {
                lastSelectedUser = listOfNicks.get(0);
            } else {
                lastSelectedUser = listOfNicks.stream().filter(user -> !Objects.equals(user, lastSelectedUser)).findFirst().get();
            }
            requestMoveFromClient(lastSelectedUser);
        }
    }

    @Override
    public void requestFirstMove() {
        nextTurn();
    }

    private void requestMoveFromClient(String selectedUser) {
        System.out.println(String.format("Sending request to %s", selectedUser));
        try {
            userListenerMap.get(selectedUser).onYourTurn(board.getFreeSpots(), board.getVisualRepresentation());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String getFreeUserSymbol() {
        return availableSymbolsToTake.stream().filter(symbol -> !ownersOfSymbols.containsKey(symbol)).findFirst().get();
    }
}
