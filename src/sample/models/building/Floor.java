package sample.models.building;

import sample.models.building.passenger.Passenger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Floor {
    private List<Queue<Passenger>> passengers;
    private int id;

    public  Floor(int amountOfElevators, int id ){
        //int amountOfElevators = Building.getInstance(null,null).getElevators().size();
        passengers = new ArrayList<>();
        this.id = id;

        for(int i = 0; i < amountOfElevators; ++i){
            passengers.add(new LinkedList<>());
        }
    }
    public void addPassengerToQueue(Passenger passenger){
        passengers.get(passenger.getQueue()).add(passenger);
    }
    public List<Queue<Passenger>> getQueues(){
        return passengers;
    }

    public int getId() {
        return id;
    }
}

