package sample.models.building.elevator;

import sample.models.building.passenger.Passenger;

import java.util.List;

public interface IElevatorStrategy {
    public boolean ifLoadPassengers(int floor, List<Passenger> passengersInElevator);
}
