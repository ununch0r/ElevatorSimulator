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
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import javafx.util.Pair;
import sample.models.building.Building;
import sample.models.building.Floor;
import sample.models.building.Mediator;
import sample.models.building.elevator.Elevators;
import sample.models.building.elevator.UnInterruptibleStrategy;
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
    private List<Rectangle> floorsViews;
    private List<Rectangle> elevatorsViews;
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
    private Image elevatorImage;
    private Image roofImage;

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
            elevatorImage = new Image(new FileInputStream("src/sample/images/elevator.png"));
            roofImage = new Image(new FileInputStream("src/sample/images/roof.jpeg"));
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
        backgroundRect.setFill(new ImagePattern(roofImage));
        elevatorPane.getChildren().add(backgroundRect);
        floorsViews = new ArrayList<>();
        elevatorsViews = new ArrayList<>();
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
            floorsViews.add(curFloor);
        }
        elevatorPane.getChildren().addAll(floorsViews);
        elevatorPane.getChildren().addAll(floorLabels);
    }

    private void renderElevators(int count){
        ImagePattern elevatorView = new ImagePattern(elevatorImage);
        for(int i = 1;i <= count;i++){
            Rectangle curElevator = new Rectangle((spaceBetweenElevators * i) + (i * elevatorWidth),elevatorPane.getPrefHeight() - floorHeight,
                    elevatorWidth,floorHeight);
            curElevator.setFill(elevatorView);
            elevatorsViews.add(curElevator);
        }
        elevatorPane.getChildren().addAll(elevatorsViews);
    }

    private void renderPerson(int floor,int elevator,int ququeSize){
        if(ququeSize > maxPersonsInQueqe){
            return;
        }
        ImageView newPerson = new ImageView(personImages.get(random.nextInt(personImages.size() - 1)));
        newPerson.setFitWidth(personWidth);
        newPerson.setFitHeight(floorHeight / 2);
        newPerson.setLayoutY(floorsViews.get(floor).getY() + floorHeight / 2);
        newPerson.setLayoutX(elevatorsViews.get(elevator).getX() - personWidth - (ququeSize + 1) * personWidth);
        if(queues.get(new Pair<>(floor,elevator)) == null){
            queues.put(new Pair<>(floor,elevator),new ArrayList<>());
        }
        queues.get(new Pair<>(floor,elevator)).add(newPerson);
//        System.out.println(String.format("Add quque (%d,%d)",floor,elevator));
//        System.out.println(queues.get(new Pair<>(floor,elevator)));
        elevatorPane.getChildren().add(newPerson);
    }

    public void onFloorCountChange(ActionEvent event) {
        if(!floorsViews.isEmpty()) {
            elevatorPane.getChildren().removeAll(floorsViews);
            elevatorPane.getChildren().removeAll(floorLabels);
            floorsViews.clear();
            floorLabels.clear();
        }
        renderFloors(floorsCount.getSelectionModel().getSelectedItem());
        if(elevatorsCount.getSelectionModel().getSelectedItem() != null){
            if(!elevatorsViews.isEmpty()){
                elevatorPane.getChildren().removeAll(elevatorsViews);
                renderElevators(elevatorsCount.getSelectionModel().getSelectedItem());
            }
        }
    }

    public void onElevatorCountChange(ActionEvent event) {
        if(!elevatorsViews.isEmpty()) {
            elevatorPane.getChildren().removeAll(elevatorsViews);
            elevatorsViews.clear();
        }
        renderElevators(elevatorsCount.getSelectionModel().getSelectedItem());


    }

    private void moveElevatorToFloor(int elevatorNum, int srcFloor,int destFloor){
            Elevators elevatorThread = building.getElevators().get(elevatorNum);
                Rectangle elevator = elevatorsViews.get(elevatorNum);
                Rectangle floor = floorsViews.get(destFloor);
                double animationDuration = Math.abs((srcFloor) - destFloor)* floorHeight/30;
                TranslateTransition animation = new TranslateTransition(
                        Duration.seconds(animationDuration),elevator
                );
                animation.setToY(floor.getY() - elevator.getY());
                animation.setOnFinished(e -> {
                    synchronized (elevatorThread) {
                        elevatorThread.notify();
                    }
                });
                ArrayList<Animation> personsAnimations = new ArrayList<>();
                if(personsInElevator.get(elevatorNum) != null) {
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



    private void movePersonOutOfElevator(int elevator){
        Runnable animationMove = new Runnable() {
            @Override
            public void run() {
                List<ImageView> queue = personsInElevator.get(elevator);
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

//        if(queues.get(new Pair<>(floor - 1,elevator - 1)).size() > maxPersonsInQueqe){
//            return;
//        }
//        renderPerson(floor,elevator);
//    }

    public void movePersonToElevator(int floor,int elevator){
        List<ImageView> queue = queues.get(new Pair<>(floor,elevator));
        Thread elevatorThread = building.getElevators().get(elevator);
        if(queue == null){
            System.out.println("Not find quequ");
        }
        ImageView person = queue.get(0);
        queue.remove(person);
        Rectangle rElevator = elevatorsViews.get(elevator);
        if(personsInElevator.get(elevator) == null){
                    personsInElevator.put(elevator,new ArrayList<>());
                }
        TranslateTransition animation = new TranslateTransition(
                        Duration.seconds(Math.abs(
                                (rElevator.getX() + personsInElevator.get(elevator).size() * personWidth  - person.getLayoutX())/20)),person
                );
        animation.setToX(rElevator.getX() + personsInElevator.get(elevator).size() * personWidth  - person.getLayoutX());
        personsInElevator.get(elevator).add(person);
                animation.setOnFinished(e -> {
                    synchronized (elevatorThread) {
                        elevatorThread.notify();
                    }
                });
                animation.play();
                queue.forEach( p -> {
                    p.setLayoutX(p.getLayoutX() + personWidth);
                });
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
        for(int i = 0;i < elevatorsNum;i++){
            elevators.add(new Elevators(400,mediator,i));
            elevators.get(i).setStrategy(new UnInterruptibleStrategy());
        }
        for(int i = 0; i < floorsNum;i++){
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
                                    System.out.println(String.format("Passenger add to %d floor %d elevator",floor.getId(),floor.getQueueNumber(queue)));
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
                            if(change.getAddedSize() > 0){
                                System.out.println(String.format("Passanger on floor %d go to elevator %d",elevator.getCurrentFloor(),elevator.getIdNum()));
                                movePersonToElevator(elevator.getCurrentFloor(),elevator.getIdNum());
                            }
                        }
                    });
                }
            });
        });
        elevators.forEach(elevator -> {
            elevator.start();
        });
        PassengerManager pm = PassengerManager.getInstance(10000,20000,mediator);

    }
}



