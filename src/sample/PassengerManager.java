package sample;

import java.sql.Time;
import java.time.LocalTime;
import java.util.*;

public class PassengerManager {
    private List<Timer> timers = new ArrayList<>();
    private static PassengerManager instance = null;

    private PassengerManager(int minTimeToSpawn, int maxTimeToSpawn){
        Random random = new Random(LocalTime.now().toNanoOfDay());
        List<Floor> floors = Building.getInstance(null,null).getFloors();

        for (Floor floor: floors) {
            Timer timer  = new Timer();
            timer.schedule(new PassengerCreator(floor,floors, 60),0,random.nextInt(maxTimeToSpawn - minTimeToSpawn + 1) + minTimeToSpawn);
            timers.add(timer);
        }
    }

    public static PassengerManager getInstance(int minTimeToSpawn, int maxTimeToSpawn) {
        if(instance == null) instance = new PassengerManager(minTimeToSpawn, maxTimeToSpawn);
        return instance;
    }
}
