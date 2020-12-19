package sample.models.building;

import sample.models.building.passenger.Passenger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Floor {
    private List<Queue<Passenger>> passengers;

    public  Floor(int amountOfElevators ){
        //int amountOfElevators = Building.getInstance(null,null).getElevators().size();
        passengers = new ArrayList<>();

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
}

