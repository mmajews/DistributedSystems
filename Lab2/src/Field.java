import java.io.Serializable;
import java.rmi.RemoteException;

public class Field implements Serializable {
    //Counting from left top to right down
    private int order;
    private FieldState fieldState;
    private IUser takenBy = null;
    private Object id;

    public void take(IUser user) {
        if (fieldState != FieldState.TAKEN) {
            takenBy = user;
        }
        fieldState = FieldState.TAKEN;
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
        try {
            return takenBy.getSymbol();
        } catch (RemoteException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    enum FieldState {
        FREE, TAKEN
    }
}
