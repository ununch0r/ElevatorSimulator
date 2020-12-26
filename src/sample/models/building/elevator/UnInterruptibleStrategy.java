package sample.models.building.elevator;

import sample.models.building.passenger.Passenger;

import java.util.List;

public class UnInterruptibleStrategy implements IElevatorStrategy {
    @Override
    public boolean ifLoadPassengers(int floor, List<Passenger> passengersInElevator,int destinationFloor){
        if(passengersInElevator.isEmpty() && floor == destinationFloor) {
            return true;
        }
        else {
            return false;
        }
    }
}
