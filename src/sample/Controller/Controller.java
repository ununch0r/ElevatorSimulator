package sample.Controller;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.util.Pair;
import sample.Main;
import sample.models.building.Building;
import sample.models.building.Floor;
import sample.models.building.Mediator;
import sample.models.building.elevator.Elevators;
import sample.models.building.elevator.IElevatorStrategy;
import sample.models.building.elevator.InterruptibleStrategy;
import sample.models.building.elevator.UnInterruptibleStrategy;
import sample.models.building.passenger.Passenger;
import sample.models.building.passenger.PassengerManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {
    private final double floorHeight = 49;
    private final double floorNumberOffset = 20;
    private final double floorNumberSize = 25;
    private final int maxFloors = 15;
    private final int maxElevators = 7;
    private final int personWidth = 10;
    private final double spaceBetweenElevators = 65;
    private final double elevatorWidth = 45;
    Random random = new Random();
    ArrayList<Elevators> elevators = new ArrayList<>();
    ArrayList<Floor> floors = new ArrayList<>();
    @FXML
    private AnchorPane elevatorPane;
    @FXML
    private ComboBox<Integer> floorsCount;
    @FXML
    private ComboBox<Integer> elevatorsCount;
    @FXML
    private ComboBox<String> strategy_cb;
    @FXML
    private ComboBox<Integer> passangers_cb;
    @FXML
    private Spinner<Integer> weight_sp;
    @FXML
    private ComboBox<Integer> min_time_cb;
    private int maxPersonsInQueue;
    private int elevatorsCapasity;
    private List<Rectangle> floorsViews;
    private List<Rectangle> elevatorsViews;
    private List<Label> floorLabels;
    private Rectangle backgroundRect;
    private HashMap<Pair<Integer, Integer>, List<ImageView>> queues;
    private HashMap<Integer, List<ImageView>> personsInElevator;
    private List<ImageView> personsToRemove;
    private List<Image> personImages;
    private Building building;
    private int floorsNum;
    private int elevatorsNum;
    private Image elevatorImage;
    private Image roofImage;
    private IElevatorStrategy strategy;
    private int maxElevatorWeight;
    private int minTimeToSpawn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        personImages = new ArrayList<>();
        try {
            personImages.add(new Image(new FileInputStream("src/sample/images/Person1.png")));
            personImages.add(new Image(new FileInputStream("src/sample/images/Person2.png")));
            personImages.add(new Image(new FileInputStream("src/sample/images/Person3.png")));
            personImages.add(new Image(new FileInputStream("src/sample/images/Person4.png")));
            elevatorImage = new Image(new FileInputStream("src/sample/images/elevator.png"));
            roofImage = new Image(new FileInputStream("src/sample/images/roof.jpeg"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        backgroundRect = new Rectangle(0, 0, (int) elevatorPane.getPrefWidth(), (int) elevatorPane.getPrefHeight());
        backgroundRect.setFill(new ImagePattern(roofImage));
        elevatorPane.getChildren().add(backgroundRect);
        floorsViews = new ArrayList<>();
        elevatorsViews = new ArrayList<>();
        floorLabels = new ArrayList<>();
        personsInElevator = new HashMap<>();
        random = new Random();
        queues = new HashMap<>();
        floorsCount.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
                floorsNum = t1;
                if (floorsViews != null && !floorsViews.isEmpty()) {
                    elevatorPane.getChildren().removeAll(floorsViews);
                    elevatorPane.getChildren().removeAll(floorLabels);
                    floorsViews.clear();
                    floorLabels.clear();
                }
                renderFloors(floorsNum);
                if (elevatorsNum != 0) {
                    if (!elevatorsViews.isEmpty()) {
                        elevatorPane.getChildren().removeAll(elevatorsViews);
                        renderElevators(elevatorsNum);
                    }
                }
            }
        });
        Main.getPs().setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                if (elevators != null && elevators.size() > 0) {
                    elevators.forEach(elevator -> {
                        elevator.stop();
                    });
                    PassengerManager.getInstance(1, 1, null).getTimers().forEach(timer -> {
                        timer.cancel();
                        timer.purge();
                    });
                }
            }
        });
        elevatorsCount.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
                elevatorsNum = t1;
                if (elevatorsViews != null && !elevatorsViews.isEmpty()) {
                    elevatorPane.getChildren().removeAll(elevatorsViews);
                    elevatorsViews.clear();
                }
                renderElevators(elevatorsNum);

            }
        });
        strategy_cb.getItems().add("Interraptable");
        strategy_cb.getItems().add("Uninterraptable");

        strategy_cb.setValue("Interraptable");
        strategy = new InterruptibleStrategy();
        strategy_cb.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (t1 == "Interraptable") {
                    strategy = new InterruptibleStrategy();
                } else strategy = new UnInterruptibleStrategy();
            }
        });
        for (int i = 1; i <= 4; ++i)
            passangers_cb.getItems().add(i);
        passangers_cb.setValue(4);
        maxPersonsInQueue = 4;
        elevatorsCapasity = 4;
        passangers_cb.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
                maxPersonsInQueue = t1;
                elevatorsCapasity = t1;
            }
        });

        Integer initialValue = 100;
        maxElevatorWeight = 100;
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 400, initialValue, 10);

        weight_sp.setValueFactory(valueFactory);
        weight_sp.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
                maxElevatorWeight = t1;
            }
        });


        min_time_cb.getItems().add(2);
        min_time_cb.getItems().add(4);
        min_time_cb.getItems().add(8);
        min_time_cb.getItems().add(16);
        min_time_cb.setValue(2);
        minTimeToSpawn = 2;
        min_time_cb.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
                minTimeToSpawn = t1;
            }
        });


        for (int i = 2; i <= maxFloors; i++) {
            floorsCount.getItems().add(i);
        }
        for (int i = 1; i <= maxElevators; i++) {
            elevatorsCount.getItems().add(i);
        }
        floorsCount.setValue(2);
        elevatorsCount.setValue(1);
        elevatorsNum = 1;
        floorsNum = 2;

    }


    private void renderFloors(int count) {
        Stop[] stops = new Stop[]{new Stop(0, Color.web("#4B4A4E")), new Stop(0.4, Color.web("#646266")),
                new Stop(0.6, Color.web("#646266")), new Stop(1, Color.web("#4B4A4E"))};
        LinearGradient lg = new LinearGradient(0.5, 1, 0.5, 0, true, CycleMethod.NO_CYCLE, stops);
        Color textColor = Color.color(0.5, 0.5, 0.5, 0.6);
        for (int i = 1; i <= count; i++) {
            Rectangle curFloor = new Rectangle(0, elevatorPane.getPrefHeight() - floorHeight * i,
                    elevatorPane.getPrefWidth(), floorHeight);
            curFloor.setFill(lg);
            Label floorNumber = new Label(String.valueOf(i));
            floorNumber.setFont(Font.font("Tahoma", FontWeight.SEMI_BOLD, floorNumberSize));
            floorNumber.setTextFill(textColor);
            floorNumber.setLayoutX(floorNumberOffset);
            floorNumber.setLayoutY((elevatorPane.getPrefHeight() - floorHeight * i) + (0.2 * floorHeight));
            floorLabels.add(floorNumber);
            floorsViews.add(curFloor);
        }
        elevatorPane.getChildren().addAll(floorsViews);
        elevatorPane.getChildren().addAll(floorLabels);
    }

    private void renderElevators(int count) {
        ImagePattern elevatorView = new ImagePattern(elevatorImage);
        for (int i = 1; i <= count; i++) {
            Rectangle curElevator = new Rectangle((spaceBetweenElevators * i) + (i * elevatorWidth), elevatorPane.getPrefHeight() - floorHeight,
                    elevatorWidth, floorHeight);
            curElevator.setFill(elevatorView);
            elevatorsViews.add(curElevator);
        }
        elevatorPane.getChildren().remove(elevatorsViews);
        elevatorPane.getChildren().addAll(elevatorsViews);
    }

    private void renderPerson(int floor, int elevator, int ququeSize) {
        ImageView newPerson = new ImageView(personImages.get(random.nextInt(personImages.size() - 1)));
        newPerson.setFitWidth(personWidth);
        newPerson.setFitHeight(floorHeight / 2);
        newPerson.setLayoutY(floorsViews.get(floor).getY() + floorHeight / 2);
        newPerson.setLayoutX(elevatorsViews.get(elevator).getX() - personWidth - (ququeSize + 1) * personWidth);
        if (queues.get(new Pair<>(floor, elevator)) == null) {
            queues.put(new Pair<>(floor, elevator), new ArrayList<>());
        }
        queues.get(new Pair<>(floor, elevator)).add(newPerson);
        elevatorPane.getChildren().add(newPerson);
    }


    private void moveElevatorToFloor(int elevatorNum, int srcFloor, int destFloor) {
        Elevators elevatorThread = building.getElevators().get(elevatorNum);
        Rectangle elevator = elevatorsViews.get(elevatorNum);
        Rectangle floor = floorsViews.get(destFloor);
        double animationDuration = Math.abs((srcFloor) - destFloor) * floorHeight / 30;
        TranslateTransition animation = new TranslateTransition(
                Duration.seconds(animationDuration), elevator
        );
        animation.setToY(floor.getY() - elevator.getY());
        animation.setOnFinished(e -> {
            synchronized (elevatorThread) {
                elevatorThread.notify();
            }
        });
        ArrayList<Animation> personsAnimations = new ArrayList<>();
        if (personsInElevator.get(elevatorNum) != null) {
            personsInElevator.get(elevatorNum).forEach(person -> {
                TranslateTransition personAnimation = new TranslateTransition(
                        Duration.seconds(animationDuration), person
                );
                personAnimation.setToY(floor.getY() + floorHeight / 2 - person.getLayoutY());
                personsAnimations.add(personAnimation);
            });
        }
        animation.play();
        personsAnimations.forEach(pa -> {
            pa.play();
        });

    }


    private void movePersonOutOfElevator(int elevator) {
        List<ImageView> queue = personsInElevator.get(elevator);
        Thread elevatorThread = Building.getInstance(null, null).getElevators().get(elevator);
        ImageView person = queue.get(0);
        queue.remove(person);
        TranslateTransition moveOfElevatorAnimation = new TranslateTransition(Duration.seconds(0.5), person);
        moveOfElevatorAnimation.setToX(20);
        TranslateTransition movePersonOutOfBuildingAnimation = new TranslateTransition(Duration.seconds(Math.sqrt(person.getLayoutX()) / 5), person);
        movePersonOutOfBuildingAnimation.setToX(-person.getLayoutX());
        movePersonOutOfBuildingAnimation.setOnFinished(e -> {
            elevatorPane.getChildren().remove(person);
        });
        moveOfElevatorAnimation.setOnFinished(e -> {
            movePersonOutOfBuildingAnimation.play();
            synchronized (elevatorThread) {
                elevatorThread.notify();
            }
        });
        moveOfElevatorAnimation.play();
        queue.forEach(pers -> {
            pers.setLayoutX(pers.getLayoutX() - personWidth);
        });

    }


    public void movePersonToElevator(int floor, int elevator) {
        List<ImageView> queue = queues.get(new Pair<>(floor, elevator));
        Thread elevatorThread = building.getElevators().get(elevator);
        ImageView person = queue.get(0);
        queue.remove(person);
        Rectangle rElevator = elevatorsViews.get(elevator);
        if (personsInElevator.get(elevator) == null) {
            personsInElevator.put(elevator, new ArrayList<>());
        }
        TranslateTransition animation = new TranslateTransition(
                Duration.seconds(Math.abs(
                        (rElevator.getX() + personsInElevator.get(elevator).size() * personWidth - person.getLayoutX()) / 25)), person
        );
        animation.setToX(rElevator.getX() + personsInElevator.get(elevator).size() * personWidth - person.getLayoutX());
        personsInElevator.get(elevator).add(person);
        animation.setOnFinished(e -> {
            synchronized (elevatorThread) {
                elevatorThread.notify();
            }
        });
        animation.play();
        queue.forEach(p -> {
            p.setLayoutX(p.getLayoutX() + personWidth);
        });
    }


    public void onStart(ActionEvent event) {
        Mediator mediator = new Mediator();
        for (int i = 0; i < elevatorsNum; i++) {
            elevators.add(new Elevators(maxElevatorWeight, elevatorsCapasity, mediator, i));
            elevators.get(i).setStrategy(strategy);
        }
        for (int i = 0; i < floorsNum; i++) {
            floors.add(new Floor(elevatorsNum, maxPersonsInQueue, i));
        }
        building = Building.getInstance(floors, elevators);
        floors.forEach(floor -> {
            floor.getPassengers().forEach(queue -> {
                queue.addListener(new ListChangeListener<Passenger>() {
                    @Override
                    public void onChanged(Change<? extends Passenger> change) {
                        change.next();
                        int countChange = change.getAddedSize();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < countChange; i++) {
                                    renderPerson(floor.getId(), floor.getQueueNumber(queue), queue.size());
                                }
                            }
                        });
                    }
                });
            });
        });

        elevators.forEach(elevator -> {
            elevator.currentFloorProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            moveElevatorToFloor(elevator.getIdNum(), number.intValue(), t1.intValue());
                        }
                    });
                }
            });
        });

        elevators.forEach(elevator -> {
            elevator.getPassengersInside().addListener(new ListChangeListener<Passenger>() {
                @Override
                public void onChanged(Change<? extends Passenger> change) {
                    change.next();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (change.getAddedSize() > 0) {
                                movePersonToElevator(elevator.getCurrentFloor(), elevator.getIdNum());
                            } else if (change.getRemovedSize() > 0) {
                                movePersonOutOfElevator(elevator.getIdNum());
                            }
                        }
                    });
                }
            });
        });
        elevators.forEach(elevator -> {
            elevator.start();
        });
        PassengerManager pm = PassengerManager.getInstance(minTimeToSpawn * 1000, (minTimeToSpawn * 2) * 1000, mediator);

    }
}



