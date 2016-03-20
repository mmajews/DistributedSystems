import java.io.Serializable;

public class Field implements Serializable {
    //Counting from left top to right down
    private int order;
    private FieldState fieldState;
    private IUser takenBy = null;
    private String symbolOfBeingTaken;
    private Object id;

    private void take(IUser user, String symbolOfBeingTaken) {
        if (fieldState != FieldState.TAKEN) {
            takenBy = user;
            this.symbolOfBeingTaken = symbolOfBeingTaken;
        }
    }

    public Field(int order) {
        this.order = order;
        fieldState = FieldState.FREE;
    }

    public boolean isAvaiableToTake() {
        return fieldState != FieldState.TAKEN;
    }

    public int getOrder() {
        return order;
    }

    public String getSymbolOfBeingTaken() {
        return symbolOfBeingTaken;
    }

    enum FieldState {
        FREE, TAKEN
    }
}
