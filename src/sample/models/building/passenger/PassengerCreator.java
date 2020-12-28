package sample.models.building.passenger;

import sample.models.building.Floor;
import sample.models.building.Mediator;

import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;

public class PassengerCreator extends TimerTask {
    private final Floor sourceFloor;
    private final List<Floor> floors;
    private final float maxWeight;
    private final Mediator mediator;

    public PassengerCreator(Floor floor, List<Floor> floors, float maxWeight, Mediator mediator) {
        this.sourceFloor = floor;
        this.floors = floors;
        this.maxWeight = maxWeight;
        this.mediator = mediator;
    }

    @Override
    public void run() {
        Random random = new Random(LocalTime.now().toNanoOfDay());
        Floor destFloor = floors.get(random.nextInt(floors.size() - 1));
        while (destFloor == sourceFloor) destFloor = floors.get(random.nextInt(floors.size() - 1));

        sourceFloor.addPassengerToQueue(new Passenger(sourceFloor, destFloor, random.nextInt(60) + 40 * random.nextFloat(), mediator));
//        sourceFloor.addPassengerToQueue(new Passenger(sourceFloor, destFloor, 40 + random.nextFloat() * (maxWeight - 1) * 0.5f, mediator));
    }
}
