import com.sun.org.apache.xml.internal.serialize.LineSeparator;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Board {
    private final int heightOfBoard = 3;
    private final int widthOfBoard = 3;

    private Field[][] boardRepresentation = new Field[][]{
            {new Field(0), new Field(1), new Field(2)},
            {new Field(3), new Field(4), new Field(5)},
            {new Field(6), new Field(7), new Field(8)}
    };

    public Optional<String> detectIfWinner() {
        boolean isWinner;
        /*Check for situation:
        X 1 2
        X 4 5
        X 7 8 */
        for (int j = 0; j < widthOfBoard; j++) {
            if (boardRepresentation[0][j].isAvaiableToTake()) {
                continue;
            }
            isWinner = true;
            String selectedString = boardRepresentation[0][j].getSymbolOfBeingTaken();
            for (int i = 0; i < heightOfBoard; i++) {
                if (!boardRepresentation[i][j].isAvaiableToTake()) {
                    isWinner = false;
                    break;
                }
                if (!boardRepresentation[i][j].getSymbolOfBeingTaken().equals(selectedString)) {
                    isWinner = false;
                    break;
                }
            }
            if (isWinner) {
                System.out.println("Winning combination horizontal");
                return Optional.of(selectedString);
            }
        }


        /*Check for situation:
        Y Y Y
        3 4 5
        6 7 8 */
        for (int j = 0; j < widthOfBoard; j++) {
            if (boardRepresentation[j][0].isAvaiableToTake()) {
                continue;
            }
            isWinner = true;
            String selectedString = boardRepresentation[j][0].getSymbolOfBeingTaken();
            for (int i = 0; i < heightOfBoard; i++) {
                if (!boardRepresentation[j][i].isAvaiableToTake()) {
                    isWinner = false;
                    break;
                }
                if (!boardRepresentation[j][i].getSymbolOfBeingTaken().equals(selectedString)) {
                    isWinner = false;
                    break;
                }

            }
            if (isWinner) {
                System.out.println("Winning combination vertical");
                return Optional.of(selectedString);
            }
        }


        /*Check for situation:
        Y 1 2
        3 Y 5
        6 7 Y */
        String selectedString = boardRepresentation[0][0].getSymbolOfBeingTaken();
        if (boardRepresentation[1][1].getSymbolOfBeingTaken().equals(selectedString) && boardRepresentation[2][2].getSymbolOfBeingTaken().equals(selectedString)) {
            isWinner = true;
            System.out.println("Winning combination left top to right down");
            return Optional.of(selectedString);
        }
        selectedString = boardRepresentation[1][1].getSymbolOfBeingTaken();
        if (boardRepresentation[2][0].getSymbolOfBeingTaken().equals(selectedString) && boardRepresentation[0][2].getSymbolOfBeingTaken().equals(selectedString)) {
            isWinner = true;
            return Optional.of(selectedString);
        }

        return Optional.empty();
    }

    public void performMove(IUser user, int order) throws RemoteException {
        for (Field[] fields : boardRepresentation) {
            for (Field field : fields) {
                if (field.getOrder() == order) {
                    field.take(user, user.getSymbol());
                    break;
                }
            }
        }
    }

    private void performMove(int order, String symbol) throws RemoteException {
        for (Field[] fields : boardRepresentation) {
            for (Field field : fields) {
                if (field.getOrder() == order) {
                    field.take(null, symbol);
                    break;
                }
            }
        }
    }

    public List<Field> getFreeSpots() {
        List<Field> freeSpots = new ArrayList<>();

        for (Field[] fields : boardRepresentation) {
            for (Field field : fields) {
                if (field.isAvaiableToTake()) {
                    freeSpots.add(field);
                }
            }
        }
        return freeSpots;
    }

    public String getVisualRepresentation() {
        StringBuilder visualRepresentation = new StringBuilder();
        for (Field[] fields : boardRepresentation) {
            for (Field field : fields) {
                visualRepresentation.append(field.isAvaiableToTake() ? field.getOrder() : field.getSymbolOfBeingTaken());
                visualRepresentation.append(" ");
            }
            visualRepresentation.append(LineSeparator.Unix);
        }
        return visualRepresentation.toString();
    }


    public boolean detectIfTie() {
        boolean tie = true;
        for (Field[] fields : boardRepresentation) {
            for (Field field : fields) {
                if (field.isAvaiableToTake()) {
                    tie = false;
                    break;
                }
            }
            if (!tie) {
                break;
            }
        }
        return tie;
    }

    public void randomMove(String symbol) throws RemoteException {
        int orderToTake = getFreeSpots().get(0).getOrder();
        performMove(orderToTake, symbol);
    }
}
