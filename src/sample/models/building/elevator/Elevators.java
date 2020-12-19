package sample.models.building.elevator;

import sample.models.building.Building;
import sample.models.building.Floor;
import sample.models.building.passenger.Passenger;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Elevators extends  Thread {
    private IElevatorStrategy strategy;
    private float maxWeight;
    public int currentFloor;
    private List<Passenger> passengersInside;
    private List<Integer> destinations;
    private DirectionEnum currentDirection;

    public Elevators(float maxWeight){
        passengersInside = new ArrayList<>();
        destinations = new ArrayList<>();
        currentFloor = 0;
        this.maxWeight = maxWeight;
        currentDirection = DirectionEnum.Stay;
    }

    public DirectionEnum moveNext() {
        if(passengersInside.isEmpty() && destinations.isEmpty()) {
            return DirectionEnum.Stay;
        }

        if(!passengersInside.isEmpty())
        {
            if(isChangeDirectionNeeded())
            {
                changeDirection();
            }

            var passengerFloor = getPassengerFloor(passengersInside.get(0));
            var min = Math.abs(currentFloor - passengerFloor);

            for (Passenger p : passengersInside) {

                var currentPassengerFloor = getPassengerFloor(p);
                var difference = currentFloor - currentPassengerFloor;

                if(IsMatchWithCurrentDirection(difference)) {
                    difference = Math.abs(difference);

                    if (difference < min) {
                        passengerFloor = currentPassengerFloor;
                        min = difference;
                    }
                }
            }

            if(passengerFloor - currentFloor < 0)
            {
                return DirectionEnum.Down;
            }
            else
            {
                return DirectionEnum.Up;
            }
        }
        else{
            var nextFloor = destinations.get(0);

            if(nextFloor - currentFloor < 0)
            {
                currentDirection = DirectionEnum.Down;
            }
            else
            {
                currentDirection = DirectionEnum.Up;
            }
            return currentDirection;

        }
    }

    private void changeDirection()
    {
        if(currentDirection == DirectionEnum.Down)
        {
            currentDirection = DirectionEnum.Up;
        }
        if(currentDirection == DirectionEnum.Up)
        {
            currentDirection = DirectionEnum.Down;
        }
    }

    private boolean isChangeDirectionNeeded()
    {
        if(currentDirection == DirectionEnum.Down)
        {
            for (Passenger p : passengersInside) {
                if(currentFloor - getPassengerFloor(p) < 0)
                {
                    return false;
                }
            }
        }
        else
        {
            for (Passenger p : passengersInside) {
                if(currentFloor - getPassengerFloor(p) > 0)
                {
                    return true;
                }
            }
        }

        return false;
    }

    private int getPassengerFloor(Passenger passenger)
    {
        var building = Building.getInstance(null,null);
        var passengerFloor = building.getFloors().indexOf(passenger.getCurrentFloor());

        return passengerFloor;
    }

    private boolean IsMatchWithCurrentDirection(int difference)
    {
        if(difference < 0 && currentDirection == DirectionEnum.Down)
        {
            return true;
        }

        if(difference > 0 && currentDirection == DirectionEnum.Up)
        {
            return true;
        }

        return false;
    }

    private void arrivedToFloor()
    {

    }

    public void setStrategy(IElevatorStrategy str){
         strategy = str;
    }

    private void goToFloor(DirectionEnum direction){ //запускає анімацію на інтерфейсі
        //йде анімація
        if(direction == DirectionEnum.Stay)
        {
            currentDirection = direction;
        }

        if(direction == DirectionEnum.Down) {
            currentFloor--;
        }
        else if (direction == DirectionEnum.Up){
            currentFloor++;
        }

        var building = Building.getInstance(null,null);
        var maxFloor = building.getFloors().size();

        if(currentFloor == 0 && passengersInside.isEmpty()) {
            currentDirection = DirectionEnum.Stay;
        }

        if(currentFloor == 0 && !passengersInside.isEmpty()) {
            currentDirection = DirectionEnum.Up;
        }

        if(currentFloor == maxFloor && passengersInside.isEmpty()) {
            currentDirection = DirectionEnum.Stay;
        }

        if(currentFloor == maxFloor && !passengersInside.isEmpty()) {
            currentDirection = DirectionEnum.Down;
        }

        if(!destinations.isEmpty()){
            if(strategy.ifLoadPassengers(this.currentFloor, this.passengersInside)
                    && currentFloor == destinations.get(0)){
                destinations.remove(0);
            }
        }
    }

    public void unloadPassengers(){
        Building building = Building.getInstance(null,null);
        List<Floor> floors = building.getFloors();
        passengersInside.forEach(p -> {
            if(floors.indexOf(p.getDestinationFloor()) == this.currentFloor){
                passengersInside.remove(p);
            }
        });
    }

    public boolean canEnter(Passenger passenger){
        double currentWeight = passengersInside.stream().mapToDouble(Passenger::getWeight).sum();
        if(currentWeight + passenger.getWeight() < this.maxWeight){
            return true;
        } else {
            return false;
        }
    }

    public void addPassenger(Passenger passenger){
        passengersInside.add(passenger);
    }


    @Override
    public void run()
    {
        while (!destinations.isEmpty()){
            unloadPassengers();

            goToFloor(moveNext());

            if(strategy.ifLoadPassengers(this.currentFloor, this.passengersInside)){
                Building building = Building.getInstance(null,null);
                Queue<Passenger> queue = building.getFloors().get(this.currentFloor).getQueues().get(building.getElevators().indexOf(this));
                queue.forEach(p -> {
                    if(canEnter(p)){
                        passengersInside.add(p);
                    }
                });
            }

        }
    }
}
