import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Objects;

public class BoardClient {

    private static IUser user;
    private static IBoardHandler boardHandler;

    static class Listener implements IBoardListener, Serializable {
        @Override
        public void onNewText(String text) {
            System.out.println(text);
        }

        @Override
        public void onYourTurn(List<Field> freeSpots, String visualRepresentation) throws RemoteException {
            System.out.println("Your turn!");
            System.out.println("Current state of tic-tac-to:");
            System.out.println(visualRepresentation);
            System.out.println("Enter field to take: ");

            while (true) {
                String input = System.console().readLine();
                int chosenField = Integer.parseInt(input);
                System.out.println(String.format("You choose to modify: %d", chosenField));
                if (!freeSpots.stream().map(Field::getOrder).filter(order -> order == chosenField).findAny().isPresent()) {
                    System.out.println("This is field is not avaiable. Choose another");
                    continue;
                }

                boardHandler.sendMove(user, chosenField);
                break;
            }
        }


    }

    public static void main(String[] args) {
        try {
            System.err.println("Registering client..");
            boardHandler = (IBoardHandler) Naming.lookup("rmi://127.0.0.1/note");
            IBoardListener iBoardListener = (IBoardListener) UnicastRemoteObject.exportObject(new Listener(), 0);
            user = (IUser) UnicastRemoteObject.exportObject(new User(args[0]), 0);
            String symbol = boardHandler.register(user, iBoardListener);
            if (Objects.equals(symbol, "")) {
                System.out.println("Unfortunately we encountered problem while registering you. Exiting");
                return;
            }

            System.out.println(String.format("Your tic-tac-toe symbol is %s", symbol));
            System.out.println("Waiting for another user to join / your turn");
            boardHandler.requestFirstMove();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}