package sample;

import java.util.ArrayList;
import java.util.List;

public class MyMain {
    public static void main(String[] args) {
        List<Floor> floors=new ArrayList<>();
        List<Elevators> elevators = new ArrayList<>();

        for (int i=0;i<3;++i){
            elevators.add(new Elevators(new InterruptibleStrategy(), 69));
        }

        for (int i=0;i<5;++i){
            floors.add(new Floor(elevators.size()));
        }

        Building building = Building.getInstance(floors,elevators);

        PassengerManager mng = PassengerManager.getInstance(2000,5000);


    }
}
