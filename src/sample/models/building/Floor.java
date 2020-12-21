package sample.models.building;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.models.building.passenger.Passenger;

public class Floor {
    private final ObservableList<ObservableList<Passenger>> passengers;
    private int id;

    public  Floor(int amountOfElevators, int id ){
        //int amountOfElevators = Building.getInstance(null,null).getElevators().size();
        passengers = FXCollections.observableArrayList();
        this.id = id;

        for(int i = 0; i < amountOfElevators; ++i){
            passengers.add(FXCollections.observableArrayList());
        }
    }

    public ObservableList<ObservableList<Passenger>> getPassengers() {
        return passengers;
    }

    public int getQueueNumber(ObservableList<Passenger> queue){
        return passengers.indexOf(queue);
    }

    public void addPassengerToQueue(Passenger passenger){
        passengers.get(passenger.getQueue()).add(passenger);
    }

    public int getId() {
        return id;
    }
}

