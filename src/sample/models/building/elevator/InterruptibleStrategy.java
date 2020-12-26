package sample.models.building.elevator;

import sample.models.building.passenger.Passenger;

import java.util.List;

public class InterruptibleStrategy implements IElevatorStrategy {
    @Override
    public boolean ifLoadPassengers(int floor, List<Passenger> passengersInElevator,int destinationFloor){
        return true;
    }
}
