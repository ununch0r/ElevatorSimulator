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


    public Elevators(float mw){
        passengersInside = new ArrayList<>();
        destinations = new ArrayList<>();
        currentFloor = 1;
        maxWeight = mw;
    }

    public void setStrategy(IElevatorStrategy str){
         strategy = str;
    }

    private void GoToFloor(int f){ //запускає анімацію на інтерфейсі
        //йде анімація
        this.currentFloor = f;
        if(!destinations.isEmpty()){
            if((Integer)f == destinations.get(0)){
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

            GoToFloor(strategy.findNextFloor(passengersInside, destinations));

            unloadPassengers();

            if(strategy.ifLoadPassengers(this.currentFloor)){
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
