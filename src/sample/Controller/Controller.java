package sample.Controller;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import javafx.util.Pair;
import sample.models.building.Building;
import sample.models.building.Floor;
import sample.models.building.Mediator;
import sample.models.building.elevator.Elevators;
import sample.models.building.passenger.Passenger;
import sample.models.building.passenger.PassengerManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {
    @FXML
    private AnchorPane elevatorPane;
    @FXML
    private ComboBox<Integer> floorsCount;
    @FXML
    private ComboBox<Integer> elevatorsCount;


    private double floorHeight = 49;
    private double floorNumberOffset = 20;
    private double floorNumberSize = 25;
    private int maxFloors = 15;
    private int maxElevators = 8;
    private int maxPersonsInQueqe = 4;
    private int elevatorsCapasity = 4;
    private int personWidth = 10;
    private double spaceBetweenElevators = maxPersonsInQueqe * personWidth + 30;
    private double elevatorsOffset = 70;
    private double elevatorWidth = elevatorsCapasity * personWidth + 5;
    private List<Rectangle> floors;
    private List<Rectangle> elevators;
    private List<Label> floorLabels;
    private Rectangle backgroundRect;
    private HashMap<Pair<Integer,Integer>,List<ImageView>> queues;
    private HashMap<Integer,List<ImageView>> personsInElevator;
    private List<ImageView> personsToRemove;
    private List<Image> personImages;
    Random random = new Random();
    private Building building;
    private int floorsNum;
    private int elevatorsNum;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        personsToRemove = new ArrayList<>();
        random = new Random();
        queues = new HashMap<>();
        personImages = new ArrayList<>();
        floorsCount.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
                floorsNum = t1;
                System.out.println("Fllors num " + floorsNum);
            }
        });

        elevatorsCount.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
                elevatorsNum = t1;
                System.out.println("Elevators num " + elevatorsNum);
            }
        });
        try {
            personImages.add(new Image(new FileInputStream("src/sample/images/Person1.png")));
            personImages.add(new Image(new FileInputStream("src/sample/images/Person2.png")));
            personImages.add(new Image(new FileInputStream("src/sample/images/Person3.png")));
            personImages.add(new Image(new FileInputStream("src/sample/images/Person4.png")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for(int i = 2;i <= maxFloors;i++){
            floorsCount.getItems().add(i);
        }
        for(int i = 1;i <= maxElevators;i++){
            elevatorsCount.getItems().add(i);
        }
        backgroundRect = new Rectangle(0,0,(int)elevatorPane.getPrefWidth(),(int)elevatorPane.getPrefHeight());
        backgroundRect.setFill(Color.LIGHTGRAY);
        elevatorPane.getChildren().add(backgroundRect);
        floors = new ArrayList<>();
        elevators = new ArrayList<>();
        floorLabels = new ArrayList<>();
        personsInElevator = new HashMap<>();
    }


    private void renderFloors(int count){
        Stop[] stops = new Stop[]{new Stop(0, Color.web("#4B4A4E")), new Stop(0.4, Color.web("#646266")),
                new Stop(0.6, Color.web("#646266")),new Stop(1, Color.web("#4B4A4E"))};
        LinearGradient lg = new LinearGradient(0.5, 1, 0.5, 0, true, CycleMethod.NO_CYCLE, stops);
        Color textColor = Color.color(0.5,0.5,0.5,0.6);
        for(int i = 1;i <= count;i++) {
            Rectangle curFloor =  new Rectangle(0, elevatorPane.getPrefHeight() - floorHeight * i,
                    elevatorPane.getPrefWidth(), floorHeight);
            curFloor.setFill(lg);
            Label floorNumber = new Label(String.valueOf(i));
            floorNumber.setFont(Font.font("Tahoma", FontWeight.SEMI_BOLD,floorNumberSize));
            floorNumber.setTextFill(textColor);
            floorNumber.setLayoutX(floorNumberOffset);
            floorNumber.setLayoutY((elevatorPane.getPrefHeight() - floorHeight * i) + (0.2 * floorHeight));
            floorLabels.add(floorNumber);
            floors.add(curFloor);
        }
        elevatorPane.getChildren().addAll(floors);
        elevatorPane.getChildren().addAll(floorLabels);
    }

    private void renderElevators(int count){
        Color elevatorColor = Color.BLUE;
        for(int i = 1;i <= count;i++){
            Rectangle curElevator = new Rectangle((spaceBetweenElevators * i) + (i * elevatorWidth),elevatorPane.getPrefHeight() - floorHeight,
                    elevatorWidth,floorHeight);
            curElevator.setFill(elevatorColor);
            elevators.add(curElevator);
        }
        elevatorPane.getChildren().addAll(elevators);
    }

    private void renderPerson(int floor,int elevator,int ququeSize){
        ImageView newPerson = new ImageView(personImages.get(random.nextInt(personImages.size() - 1)));
        newPerson.setFitWidth(personWidth);
        newPerson.setFitHeight(floorHeight / 2);
        newPerson.setLayoutY(floors.get(floor - 1).getY() + floorHeight / 2);
        newPerson.setLayoutX(elevators.get(elevator).getX() - personWidth - (ququeSize + 1) * personWidth);
        elevatorPane.getChildren().add(newPerson);
    }

    public void onFloorCountChange(ActionEvent event) {
        if(!floors.isEmpty()) {
            elevatorPane.getChildren().removeAll(floors);
            elevatorPane.getChildren().removeAll(floorLabels);
            floors.clear();
            floorLabels.clear();
        }
        renderFloors(floorsCount.getSelectionModel().getSelectedItem());
        if(elevatorsCount.getSelectionModel().getSelectedItem() != null){
            if(!elevators.isEmpty()){
                elevatorPane.getChildren().removeAll(elevators);
                renderElevators(elevatorsCount.getSelectionModel().getSelectedItem());
            }
        }
    }

    public void onElevatorCountChange(ActionEvent event) {
        if(!elevators.isEmpty()) {
            elevatorPane.getChildren().removeAll(elevators);
            elevators.clear();
        }
        renderElevators(elevatorsCount.getSelectionModel().getSelectedItem());


    }

    private void moveElevatorToFloor(int elevatorNum, int srcFloor,int destFloor){
        Runnable animationThread = new Runnable() {
            @Override
            public void run() {
                Rectangle elevator = elevators.get(elevatorNum - 1);
                Rectangle floor = floors.get(destFloor - 1);
                TranslateTransition animation = new TranslateTransition(
                        Duration.seconds(Math.abs(srcFloor - destFloor)* floorHeight/30),elevator
                );
                animation.setToY(floor.getY() - elevator.getY());
                ArrayList<Animation> personAnimations = new ArrayList<>();

                animation.play();

            }
        };
        animationThread.run();
    }


    private void movePersonOutOfElevator(int elevator){
        Runnable animationMove = new Runnable() {
            @Override
            public void run() {
                List<ImageView> queue = personsInElevator.get(elevator - 1);
                ImageView person = queue.get(0);
                queue.remove(person);
                personsToRemove.add(person);
                TranslateTransition animation = new TranslateTransition(Duration.seconds(3),person);
                animation.setToX(-person.getLayoutX());
                animation.play();
                queue.forEach( pers -> {
                    pers.setLayoutX(pers.getLayoutX() - personWidth);
                });
            }
        };
        animationMove.run();
    }

//    public void onMoveElevatorClick(ActionEvent event) {
//        moveElevatorToFloor(elevatorNum.getSelectionModel().getSelectedItem(),floorNum.getSelectionModel().getSelectedItem());
//    }
//
//    public void onPersonRender(ActionEvent event) {
//        int elevator = elevatorNum.getSelectionModel().getSelectedItem() ;
//        int floor = floorNum.getSelectionModel().getSelectedItem();
//        if(queues.get(new Pair<>(floor - 1,elevator - 1)) == null){
//            queues.put(new Pair<>(floor - 1,elevator - 1),new ArrayList<>());
//        }
//        if(queues.get(new Pair<>(floor - 1,elevator - 1)).size() > maxPersonsInQueqe){
//            return;
//        }
//        renderPerson(floor,elevator);
//    }

    public void movePersonToElevator(int floor,int elevator){
        Runnable moveAnimation = new Runnable() {
            @Override
            public void run() {
                List<ImageView> queqe = queues.get(new Pair<>(floor - 1,elevator - 1));
                ImageView person = queqe.get(0);
                queqe.remove(person);
                Rectangle rElevator = elevators.get(elevator - 1);
                if(personsInElevator.get(elevator) == null){
                    personsInElevator.put(elevator - 1,new ArrayList<>());
                }
                TranslateTransition animation = new TranslateTransition(
                        Duration.seconds(Math.abs(
                                (rElevator.getX() + personsInElevator.get(elevator - 1).size() * personWidth  - person.getLayoutX())/20)),person
                );
                animation.setToX(rElevator.getX() + personsInElevator.get(elevator - 1).size() * personWidth  - person.getLayoutX());
                personsInElevator.get(elevator - 1).add(person);
                animation.play();
                queqe.forEach( p -> {
                    p.setLayoutX(p.getLayoutX() + personWidth);
                });
            }
        };
        moveAnimation.run();
    }
//
//    public void onPersonMove(ActionEvent event) {
//        movePersonToElevator(floorNum.getSelectionModel().getSelectedItem(),
//                elevatorNum.getSelectionModel().getSelectedItem());
//    }
//
//    public void onOutOfElevatorClick(ActionEvent event) {
//        movePersonOutOfElevator(elevatorNum.getSelectionModel().getSelectedItem());
//    }

    public void onRemovePersons(ActionEvent event) {
        elevatorPane.getChildren().removeAll(personsToRemove);
    }

    public void onStart(ActionEvent event) {
        ArrayList<Elevators> elevators = new ArrayList<>();
        ArrayList<Floor> floors = new ArrayList<>();
        Mediator mediator = new Mediator();
        for(int i = 1;i <= elevatorsNum;i++){
            elevators.add(new Elevators(20,mediator,i));
        }
        for(int i = 1; i <= floorsNum;i++){
            floors.add(new Floor(elevatorsNum,i));
        }
        building = Building.getInstance(floors,elevators);
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
                                for(int i = 0;i < countChange;i++){
                                    renderPerson(floor.getId(),floor.getQueueNumber(queue),queue.size());
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
                            moveElevatorToFloor(elevator.getIdNum(),number.intValue(),t1.intValue());
                            System.out.println("Animation start");
                        }
                    });
                }
            });
        });
        elevators.forEach(elevator -> {
            elevator.start();
        });
        PassengerManager pm = PassengerManager.getInstance(5000,10000,mediator);

    }
}


