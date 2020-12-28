package sample.models.building.elevator;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.models.building.Building;
import sample.models.building.Mediator;
import sample.models.building.passenger.Passenger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Elevators extends Thread {
    private final ObservableList<Passenger> passengersInside;
    private final float maxWeight;
    private final IntegerProperty currentFloor;
    private final Queue<Integer> destinations;
    private final int capacity;
    private final Mediator mediator;
    private final int idNum;
    public boolean isAnimated = false;
    private IElevatorStrategy strategy;
    private DirectionEnum currentDirection;


    private String threadName;

    public Elevators(float maxWeight, int capacity, Mediator mediator, int idNum) {
        passengersInside = FXCollections.observableArrayList();
        destinations = new LinkedList<>();
        currentFloor = new SimpleIntegerProperty(0);
        this.maxWeight = maxWeight;
        currentDirection = DirectionEnum.Stay;
        this.mediator = mediator;
        this.idNum = idNum;
        this.capacity = capacity;
        setName("Elevator " + idNum);
    }

    public ObservableList<Passenger> getPassengersInside() {
        return passengersInside;
    }

    public int getCurrentFloor() {
        return currentFloor.get();
    }

    private void setCurrentFloor(int currentFloor) {
        this.currentFloor.set(currentFloor);
    }

    public IntegerProperty currentFloorProperty() {
        return currentFloor;
    }

    public DirectionEnum moveNext() {

        if (passengersInside.isEmpty() && destinations.isEmpty()) {
            return DirectionEnum.Stay;
        }

        if (!passengersInside.isEmpty()) {
            if (isChangeDirectionNeeded()) {
                changeDirection();
            }

            int passengerFloor = getPassengerFloor(passengersInside.get(0));
            int min = Math.abs(currentFloor.get() - passengerFloor);

            for (Passenger p : passengersInside) {

                var currentPassengerFloor = getPassengerFloor(p);
                var difference = currentFloor.get() - currentPassengerFloor;

                if (IsMatchWithCurrentDirection(difference)) {
                    difference = Math.abs(difference);

                    if (difference < min) {
                        passengerFloor = currentPassengerFloor;
                        min = difference;
                    }
                }
            }

            if (passengerFloor - currentFloor.get() < 0) {
                return DirectionEnum.Down;
            } else if (passengerFloor - currentFloor.get() == 0) {
                return DirectionEnum.Stay;
            } else {
                return DirectionEnum.Up;
            }
        } else {
            var nextFloor = destinations.element();

            if (nextFloor - currentFloor.get() < 0) {
                currentDirection = DirectionEnum.Down;
            } else if (nextFloor - currentFloor.get() == 0) {
                currentDirection = DirectionEnum.Stay;
            } else {
                currentDirection = DirectionEnum.Up;
            }
            return currentDirection;

        }
    }

    private void changeDirection() {
        if (currentDirection == DirectionEnum.Down) {
            currentDirection = DirectionEnum.Up;
        }
        if (currentDirection == DirectionEnum.Up) {
            currentDirection = DirectionEnum.Down;
        }
        if(getPassengerFloor(passengersInside.get(0)) > getCurrentFloor()){
            currentDirection = DirectionEnum.Up;
        }
        else{
            currentDirection = DirectionEnum.Down;
        }
    }

    private boolean isChangeDirectionNeeded() {
        if (currentDirection == DirectionEnum.Down) {
            for (Passenger p : passengersInside) {
                if (currentFloor.get() - getPassengerFloor(p) < 0) {
                    return true;
                }
            }
        }
        else if(currentDirection == DirectionEnum.Stay){
            return true;
        }
        else {
            for (Passenger p : passengersInside) {
                if (currentFloor.get() - getPassengerFloor(p) > 0) {
                    return true;
                }
            }
        }

        return false;
    }

    private int getPassengerFloor(Passenger passenger) {
        return passenger.getDestinationFloor().getId();
    }

    private boolean IsMatchWithCurrentDirection(int difference) {
        if (difference < 0 && currentDirection == DirectionEnum.Down) {
            return true;
        }

        return difference > 0 && currentDirection == DirectionEnum.Up;
    }

    private void arrivedToFloor() {
        mediator.notify(this);
    }

    public void setStrategy(IElevatorStrategy str) {
        strategy = str;
    }

    private void goToFloor(DirectionEnum direction) {

        if (direction == DirectionEnum.Stay) {
            currentDirection = direction;

            return;
        }

        if (direction == DirectionEnum.Down) {
            currentFloor.set(currentFloor.get() - 1);
        } else if (direction == DirectionEnum.Up) {
            currentFloor.set(currentFloor.get() + 1);
        }

        Building building = Building.getInstance(null, null);
        int maxFloor = building.getFloors().size() - 1;

    }

    public void unloadPassengers() {
        Building building = Building.getInstance(null, null);
        ArrayList<Passenger> toDelete = new ArrayList<>();

        passengersInside.forEach(p -> {
            if (p.getDestinationFloor().getId() == this.currentFloor.get()) {
                toDelete.add(p);

            }
        });
        toDelete.forEach(td -> {
            passengersInside.remove(td);
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public boolean canEnter(Passenger passenger) {
        double currentWeight = passengersInside.stream().mapToDouble(Passenger::getWeight).sum();
        return passengersInside.size() < capacity && currentWeight + passenger.getWeight() < this.maxWeight;
    }

    public void addPassenger(Passenger passenger) {
        passengersInside.add(passenger);
        try {
            synchronized (this) {
                this.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public DirectionEnum getCurrentDirection() {
        return currentDirection;
    }

    synchronized public void AddNewDestination(int floorNumber) {
        for (int i : destinations) {
            if (i == floorNumber) return;
        }

        destinations.add(floorNumber);
    }

    @Override
    public void run() {
        while (true) {
            if (!destinations.isEmpty() || !passengersInside.isEmpty()) {
                goToFloor(moveNext());
                if(currentDirection != DirectionEnum.Stay) {
                    try {
                        synchronized (this) {
                            this.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (!passengersInside.isEmpty()) unloadPassengers();
                if (!destinations.isEmpty()) {
                    if (strategy.ifLoadPassengers(this.currentFloor.get(), this.passengersInside, destinations.element())
                    ) {
                        arrivedToFloor();
                        if (currentFloor.get() == destinations.element()) {
                            destinations.poll();
                        }
                    }
                }


            } else {
                currentDirection = DirectionEnum.Wait;
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
