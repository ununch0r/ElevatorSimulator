package sample;

import java.util.List;

public interface IElevatorStrategy {
    public boolean ifLoadPassengers(int Floor);
    public int findNextFloor(List<Passenger> passengersInElevator, List<Integer> destinations);
}
