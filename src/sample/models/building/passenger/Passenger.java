package sample.models.building.passenger;

import sample.models.building.Floor;

import java.util.Queue;

public class Passenger {
    public Floor getCurrentFloor() {
        return currentFloor;
    }

    private Floor currentFloor;

    public Floor getDestinationFloor() {
        return destinationFloor;
    }

    private Floor destinationFloor;

    public float getWeight() {
        return weight;
    }

    private float weight;
    private boolean isRiding;
    private int queue;

    public Passenger(Floor currentFloor, Floor destinationFloor, float weight) {
        this.currentFloor = currentFloor;
        this.destinationFloor = destinationFloor;
        this.isRiding = false;
        this.queue = chooseQueue();
        this.weight = weight;
    }

    public boolean isRiding() {
        return isRiding;
    }

    public void setRiding(boolean riding) {
        isRiding = riding;
    }

    private int chooseQueue()
    {
        Queue<Passenger> min = currentFloor.getQueues().stream().min((o1, o2) -> {
            if(o1.size() > o2.size()) return 1;
            else if(o1.size() < o2.size()) return -1;
            else return 0;
        }).get();

        return currentFloor.getQueues().indexOf(min);
    }

    public int getQueue() {
        return queue;
    }

}
