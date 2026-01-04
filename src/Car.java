import java.util.ArrayList;
import java.util.List;

public class Car {
    public enum State { IN_MOTION, PARKED }

    private String id;
    private State currentState;
    private List<ParkingSession> history;

    public Car(String id) {
        this.id = id;
        this.currentState = State.IN_MOTION;
        this.history = new ArrayList<>();
    }

    public String getId() { return id; }
    public State getCurrentState() { return currentState; }
    public void setCurrentState(State currentState) { this.currentState = currentState; }
    public List<ParkingSession> getHistory() { return history; }
}

class ParkingSession {
    public int entryStep;
    public int exitStep = -1;
}