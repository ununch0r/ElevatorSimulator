package sample.models.building.elevator;

import sample.models.building.Building;
import sample.models.building.passenger.Passenger;

import java.util.List;

public class UnInterruptibleStrategy implements IElevatorStrategy {
    @Override
    public boolean ifLoadPassengers(int floor, List<Passenger> passengersInElevator){
        if(passengersInElevator.isEmpty()) {
            return true;
        }
        else {
            return false;
        }
    }
}
