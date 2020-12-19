package sample.models.building.passenger;

import sample.models.building.Floor;
import sample.models.building.Mediator;

import javax.print.attribute.standard.Media;
import java.util.Queue;

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
        this.queue = chooseQueue();
        this.weight = weight;
        this.mediator = mediator;
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
        Queue<Passenger> min = currentFloor.getQueues().stream().min((o1, o2) -> {
            if(o1.size() > o2.size()) return 1;
            else if(o1.size() < o2.size()) return -1;
            else return 0;
        }).get();

        mediator.notify(this);
        return currentFloor.getQueues().indexOf(min);
    }

    public int getQueue() {
        return queue;
    }

}
