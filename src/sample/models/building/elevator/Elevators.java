package sample.models.building.elevator;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.models.building.Building;
import sample.models.building.Floor;
import sample.models.building.Logger;
import sample.models.building.Mediator;
import sample.models.building.passenger.Passenger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Elevators extends  Thread  {
    private IElevatorStrategy strategy;
    private float maxWeight;
    private IntegerProperty currentFloor;
    private final ObservableList<Passenger> passengersInside;
    private Queue<Integer> destinations;
    private DirectionEnum currentDirection;
    private boolean isAnimated = false;
    private int capacity;
    private Mediator mediator;
    private int idNum;
    private String threadName;

    public Elevators(float maxWeight,int capacity, Mediator mediator,int idNum){
        passengersInside = FXCollections.observableArrayList();
        destinations = new LinkedList<>();
        currentFloor = new SimpleIntegerProperty(0);
        this.maxWeight = maxWeight;
        currentDirection = DirectionEnum.Stay;
        this.mediator = mediator;
        this.idNum = idNum;
        this.capacity = capacity;
       // this.threadName=Thread.currentThread().getName();
        setName("Elevator " + idNum);
    }

    public ObservableList<Passenger> getPassengersInside() {
        return passengersInside;
    }

    public int getCurrentFloor() {
        return currentFloor.get();
    }

    public IntegerProperty currentFloorProperty() {
        return currentFloor;
    }

    private void setCurrentFloor(int currentFloor) {
        this.currentFloor.set(currentFloor);
    }

    public DirectionEnum moveNext() {
        if(passengersInside.isEmpty() && destinations.isEmpty()) {
            return DirectionEnum.Stay;
        }
        if(currentFloor.get() == Building.getInstance(null,null).getFloors().size() - 1){
            System.out.println();
        }
        if(!passengersInside.isEmpty())
        {
            if(isChangeDirectionNeeded())
            {
                changeDirection();
            }

            var passengerFloor = getPassengerFloor(passengersInside.get(0));
            var min = Math.abs(currentFloor.get() - passengerFloor);

            for (Passenger p : passengersInside) {

                var currentPassengerFloor = getPassengerFloor(p);
                var difference = currentFloor.get() - currentPassengerFloor;

                if(IsMatchWithCurrentDirection(difference)) {
                    difference = Math.abs(difference);

                    if (difference < min) {
                        passengerFloor = currentPassengerFloor;
                        min = difference;
                    }
                }
            }

            if(passengerFloor - currentFloor.get() < 0)
            {
                return DirectionEnum.Down;
            }
            else if(passengerFloor - currentFloor.get() == 0){
                return DirectionEnum.Stay;
            }
            else
            {
                return DirectionEnum.Up;
            }
        }
        else{
            var nextFloor = destinations.element();

            if(nextFloor - currentFloor.get() < 0)
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
                if(currentFloor.get() - getPassengerFloor(p) < 0)
                {
                    return true;
                }
            }
        }
        else
        {
            for (Passenger p : passengersInside) {
                if(currentFloor.get() - getPassengerFloor(p) > 0)
                {
                    return true;
                }
            }
        }

        return false;
    }

    private int getPassengerFloor(Passenger passenger)
    {
        return passenger.getDestinationFloor().getId();
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
        mediator.notify(this);
    }

    public void setStrategy(IElevatorStrategy str){
         strategy = str;
    }

    private void goToFloor(DirectionEnum direction){ //запускає анімацію на інтерфейсі

        if(direction == DirectionEnum.Stay)
        {
            currentDirection = direction;
            return;
        }

        if(direction == DirectionEnum.Down) {
            currentFloor.set(currentFloor.get() - 1);
        }
        else if (direction == DirectionEnum.Up){
            currentFloor.set(currentFloor.get() + 1);
        }

        var building = Building.getInstance(null,null);
        var maxFloor = building.getFloors().size() - 1;

        if(currentFloor.get() == 0 && passengersInside.isEmpty()) {
            currentDirection = DirectionEnum.Stay;
        }

        if(currentFloor.get() == 0 && !passengersInside.isEmpty()) {
            currentDirection = DirectionEnum.Up;
        }

        if(currentFloor.get() == maxFloor && passengersInside.isEmpty()) {
            currentDirection = DirectionEnum.Stay;
        }

        if(currentFloor.get() == maxFloor && !passengersInside.isEmpty()) {
            currentDirection = DirectionEnum.Down;
        }

        if(!destinations.isEmpty()){
            if(strategy.ifLoadPassengers(this.currentFloor.get(), this.passengersInside)
                    && currentFloor.get() == destinations.element()){
                destinations.poll();
            }
        } else {
            currentDirection = DirectionEnum.Stay;
        }
    }

    public void unloadPassengers(){
        Building building = Building.getInstance(null,null);
        List<Floor> floors = building.getFloors();
        ArrayList<Passenger> toDelete = new ArrayList<>();

        passengersInside.forEach(p -> {
            if (p.getDestinationFloor().getId() == this.currentFloor.get()) {
                toDelete.add(p);

            }
        });
                    toDelete.forEach(td -> {
                        passengersInside.remove(td);
                       // Logger.Log(String.format("Passenger go out of the elevator %d, floor %d. Thread: %s \n",idNum,currentFloor.get(),this.getName()));
                        synchronized (this) {
                            try {
                                this.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }


    public boolean canEnter(Passenger passenger){
        double currentWeight = passengersInside.stream().mapToDouble(Passenger::getWeight).sum();
        if(passengersInside.size() < capacity && currentWeight + passenger.getWeight() < this.maxWeight){
            return true;
        } else {
            return false;
        }
    }

    public void addPassenger(Passenger passenger){
       // System.out.println(String.format("Passanger on floor %d go to elevator %d",currentFloor.get(),getIdNum()));
       // Logger.Log(String.format("Passenger on floor %d go to elevator %d. Thread: %s\n",currentFloor.get(),idNum,this.getName()));
        passengersInside.add(passenger);
        try {
            synchronized (this) {
                this.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public DirectionEnum getCurrentDirection()
    {
        return currentDirection;
    }

    synchronized public void AddNewDestination(int floorNumber)
    {
        for (int i : destinations) {
            if(i == floorNumber) return;
        }

        destinations.add(floorNumber);
    }

    @Override
    public void run () {
        while (true) {
            if (!destinations.isEmpty() || !passengersInside.isEmpty()) {
                goToFloor(moveNext());
                try {
                        synchronized (this) {
                            this.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                if(!passengersInside.isEmpty()) unloadPassengers();

                if (strategy.ifLoadPassengers(this.currentFloor.get(), this.passengersInside)) {
                    arrivedToFloor();
            }
//            if (!passengersInside.isEmpty()) continue;

            } else {
                try {
                    synchronized (this) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    public int getIdNum() {
        return idNum;
    }
}
