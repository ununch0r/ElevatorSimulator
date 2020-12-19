package sample.models.building.elevator;

import sample.models.building.passenger.Passenger;

import java.util.List;

public class InterruptibleStrategy implements IElevatorStrategy {
    @Override
    public boolean ifLoadPassengers(int floor) {
        return false;//TODO
    }

    @Override
    public int findNextFloor(List<Passenger> passengersInElevator, List<Integer> destinations) {
        return 0;//TODO
    }
}
