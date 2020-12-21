package sample.models.building.passenger;

import javafx.collections.ObservableList;
import sample.models.building.Floor;
import sample.models.building.Mediator;

public class Passenger {

    private Mediator mediator;
    private Floor currentFloor;
    private Floor destinationFloor;
    private float weight;
    private boolean isRiding;
    private int queue;

    public Passenger(Floor currentFloor, Floor destinationFloor, float weight, Mediator mediator) {
        this.currentFloor = currentFloor;
        this.destinationFloor = destinationFloor;
        this.isRiding = false;
        this.mediator = mediator;
        this.queue = chooseQueue();
        this.weight = weight;
    }

    public Floor getCurrentFloor() {
        return currentFloor;
    }
    public Floor getDestinationFloor() {
        return destinationFloor;
    }
    public boolean isRiding() {
        return isRiding;
    }
    public void setRiding(boolean riding) {
        isRiding = riding;
    }
    public float getWeight() {
        return weight;
    }

    private int chooseQueue()
    {
        ObservableList<Passenger> min = currentFloor.getPassengers().stream().min((o1, o2) -> {
            if(o1.size() > o2.size()) return 1;
            else if(o1.size() < o2.size()) return -1;
            else return 0;
        }).get();

        mediator.notify(this);
        return currentFloor.getPassengers().indexOf(min);
    }

    public int getQueue() {
        return queue;
    }

}
