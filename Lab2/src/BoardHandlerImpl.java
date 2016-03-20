import java.rmi.RemoteException;
import java.util.*;

public class BoardHandlerImpl implements IBoardHandler {

    private String lastSelectedUser;
    private List<IUser> listOfUsers = new ArrayList<>();
    private Map<String, IUser> ownersOfSymbols = new HashMap<>();
    private final List<String> availableSymbolsToTake = Arrays.asList("X", "Y");
    private Map<String, IBoardListener> userListenerMap = new HashMap<>();
    private Board board = new Board();

    @Override
    public String register(IUser user, IBoardListener l, String gameId) throws RemoteException, UserRejectedException {
        if (!userListenerMap.containsKey(user.getNick()) && userListenerMap.size() < 2 && ownersOfSymbols.size() < 2) {
            userListenerMap.put(user.getNick(), l);
            listOfUsers.add(user);
            String selectedSymbol = getFreeUserSymbol();
            user.setSymbol(selectedSymbol);
            System.out.println(String.format("User %s is now registered with symbol %s", user.getNick(), user.getSymbol()));
            ownersOfSymbols.put(selectedSymbol, user);
            return selectedSymbol;
        } else {
            return "";
        }
    }

    private void nextTurn(String gameId) throws RemoteException {
        if (userListenerMap.size() == 2) {
            System.out.println("Next turn!");
            if (lastSelectedUser == null) {
                lastSelectedUser = listOfUsers.stream().filter(user -> {
                    try {
                        return Objects.equals(user.getGameId(), gameId);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                }).findFirst().get().getNick();
            } else {
                lastSelectedUser = listOfUsers.stream().filter(user -> {
                    try {
                        return !Objects.equals(user.getNick(), lastSelectedUser);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                }).findFirst().get().getNick();
            }
            requestMoveFromClient(lastSelectedUser, gameId);
        }
    }

    @Override
    public void requestFirstMove(String gameId) {
        try {
            nextTurn(gameId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMove(IUser user, int order, String gameId) throws RemoteException {
        System.out.println("Move from " + user.getNick() + " registered. Processing...");
        board.performMove(user, order);

        Optional<String> winnerSymbol = board.detectIfWinner();
        if (winnerSymbol.isPresent()) {
            System.out.println("Winner symbol: " + winnerSymbol.get());
            sendMessageAboutWinnerToPlayers(gameId, winnerSymbol.get());
            return;
        }

        nextTurn(gameId);
    }

    private void sendMessageAboutWinnerToPlayers(String gameId, String winnerSymbol) throws RemoteException {
        List<String> usersToBeSendAboutWinning = new ArrayList<>();
        for (IUser user : listOfUsers) {
            if (Objects.equals(user.getGameId(), gameId)) {
                userListenerMap.get(user.getNick()).onGameEnd(winnerSymbol);
            }
            System.out.println("Game ended!");
        }
    }

    private void requestMoveFromClient(String selectedUser, String gameId) {
        System.out.println(String.format("Sending request to %s", selectedUser));
        try {
            userListenerMap.get(selectedUser).onYourTurn(board.getFreeSpots(), board.getVisualRepresentation(), gameId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String getFreeUserSymbol() {
        return availableSymbolsToTake.stream().filter(symbol -> !ownersOfSymbols.containsKey(symbol)).findFirst().get();
    }
}
