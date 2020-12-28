package sample.models.building.passenger;

import sample.models.building.Building;
import sample.models.building.Floor;
import sample.models.building.Mediator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;

public class PassengerManager {
    private static PassengerManager instance = null;
    private final List<Timer> timers = new ArrayList<>();
    private final Mediator mediator;
    private float maxWeight;

    private PassengerManager(int minTimeToSpawn, int maxTimeToSpawn,float maxWeight, Mediator mediator) {
        Random random = new Random(LocalTime.now().toNanoOfDay());
        this.mediator = mediator;
        List<Floor> floors = Building.getInstance(null, null).getFloors();
        this.maxWeight = maxWeight;
        for (Floor floor : floors) {
            Timer timer = new Timer();
            timer.schedule(new PassengerCreator(floor, floors, maxWeight, mediator), random.nextInt(maxTimeToSpawn - minTimeToSpawn + 1), random.nextInt(maxTimeToSpawn - minTimeToSpawn + 1) + minTimeToSpawn);
            timers.add(timer);
        }
    }

    public static PassengerManager getInstance(int minTimeToSpawn, int maxTimeToSpawn,float maxWeight, Mediator mediator) {
        if (instance == null) instance = new PassengerManager(minTimeToSpawn, maxTimeToSpawn,maxWeight, mediator);
        return instance;
    }

    public List<Timer> getTimers() {
        return timers;
    }
}
