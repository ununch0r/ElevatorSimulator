package sample.models.building;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.models.building.passenger.Passenger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class Floor {
    private final ObservableList<ObservableList<Passenger>> passengers;
    private int id;
    //private static  Logger logger;
    public  Floor(int amountOfElevators, int id ){
        passengers = FXCollections.observableArrayList();
        this.id = id;

        for(int i = 0; i < amountOfElevators; ++i){
            passengers.add(FXCollections.observableArrayList());
        }
        //logger=Logger.getLogger("Floor Logger");
        //ConfigLogger();
    }

    public ObservableList<ObservableList<Passenger>> getPassengers() {
        return passengers;
    }

    public int getQueueNumber(ObservableList<Passenger> queue){
        return passengers.indexOf(queue);
    }

    public void addPassengerToQueue(Passenger passenger){

        passengers.get(passenger.getQueue()).add(passenger);
       // logger.info(String.format("Person was added to %d queue",id));
    }

    public int getId() {
        return id;
    }


}

