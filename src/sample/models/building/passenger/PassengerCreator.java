package sample.models.building.passenger;

import sample.models.building.Floor;

import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;

public class PassengerCreator extends TimerTask {
    private Floor sourceFloor;
    private List<Floor> floors;
    private float maxWeight;

    public PassengerCreator(Floor floor, List<Floor> floors,float maxWeight){
        this.sourceFloor=floor;
        this.floors = floors;
        this.maxWeight = maxWeight;
    }

    @Override
    public void run() {
        Random random = new Random(LocalTime.now().toNanoOfDay());
        Floor destFloor = floors.get(random.nextInt(floors.size() - 1));
        while (destFloor == sourceFloor) destFloor = floors.get(random.nextInt(floors.size() - 1));

        sourceFloor.addPassengerToQueue(new Passenger(sourceFloor,destFloor,1 + random.nextFloat() * (maxWeight - 1)));
    }
}
