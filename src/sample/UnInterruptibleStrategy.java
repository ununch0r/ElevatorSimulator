package sample;

import java.util.List;

public class UnInterruptibleStrategy implements IElevatorStrategy {
    @Override
    public boolean ifLoadPassengers(int Floor) {
        return false; //TODO
    }

    @Override
    public int findNextFloor(List<Passenger> passengersInElevator, List<Integer> destinations) {
        return 0; //TODO
    }
}
