package sample.models.building.passenger;

import javafx.collections.ObservableList;
import sample.models.building.Floor;
import sample.models.building.Mediator;

public class Passenger {

    private final Mediator mediator;
    private final Floor currentFloor;
    private final Floor destinationFloor;
    private final float weight;
    private final int queue;
    private boolean isRiding;

    public Passenger(Floor currentFloor, Floor destinationFloor, float weight, Mediator mediator) {
        this.currentFloor = currentFloor;
        this.destinationFloor = destinationFloor;
        this.isRiding = false;
        this.mediator = mediator;
        this.weight = weight;
        this.queue = chooseQueue();
        this.mediator.notify(this);
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

    private int chooseQueue() {
        ObservableList<Passenger> min = currentFloor.getPassengers().stream().min((o1, o2) -> {
            if (o1.size() > o2.size()) return 1;
            else if (o1.size() < o2.size()) return -1;
            else return 0;
        }).get();

        return currentFloor.getPassengers().indexOf(min);
    }

    public int getQueue() {
        return queue;
    }

}
