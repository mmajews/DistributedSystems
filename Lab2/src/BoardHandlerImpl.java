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
    public String register(IUser user, IBoardListener l) throws RemoteException, UserRejectedException {
        if (!userListenerMap.containsKey(user.getNick()) && userListenerMap.size() < 2 && ownersOfSymbols.size() < 2) {
            userListenerMap.put(user.getNick(), l);
            listOfNicks.add(user.getNick());
            String selectedSymbol = getFreeUserSymbol();
            user.setSymbol(selectedSymbol);
            System.out.println(String.format("User %s is now registered with symbol %s", user.getNick(), user.getSymbol()));
            ownersOfSymbols.put(selectedSymbol, user);
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

    @Override
    public void sendMove(IUser user, int order) throws RemoteException {
        System.out.println("Move from " + user.getNick() + " registered. Processing...");
        board.performMove(user, order);
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
