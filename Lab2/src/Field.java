import java.io.Serializable;

public class Field implements Serializable {
    //Counting from left top to right down
    private int order;
    private FieldState fieldState;
    private IUser takenBy = null;
    private String symbol;
    private Object id;

    public void take(IUser user, String symbol) {
        if (fieldState != FieldState.TAKEN) {
            fieldState = FieldState.TAKEN;
            takenBy = user;
            this.symbol = symbol;
        }
    }

    public Field(int order) {
        symbol = String.valueOf(System.currentTimeMillis() + "$" + order);
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
        return symbol;
    }

    enum FieldState {
        FREE, TAKEN
    }
}
