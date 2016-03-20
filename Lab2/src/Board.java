import com.sun.org.apache.xml.internal.serialize.LineSeparator;

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

    public Optional<IUser> detectIfWinner() {
        return Optional.empty();
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


}
