package sample.models.building;

import sample.models.building.elevator.Elevators;
import sample.models.building.elevator.IElevatorStrategy;

import java.util.List;

public class Building {
    private static Building instance = null;
    private final List<Floor> floors;
    private final List<Elevators> elevators;

    private Building(List<Floor> floors, List<Elevators> elevators) {
        this.floors = floors;
        this.elevators = elevators;
    }

    public static Building getInstance(List<Floor> floors, List<Elevators> elevators) {
        if (instance == null) instance = new Building(floors, elevators);
        return instance;
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public List<Elevators> getElevators() {
        return elevators;
    }

    public void setStrategy(IElevatorStrategy strategy) {
        elevators.forEach(el -> {
            el.setStrategy(strategy);
        });
    }
}
