package sample;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
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
    @FXML
    private ComboBox<Integer> elevatorNum;
    @FXML
    private ComboBox<Integer> floorNum;


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
    private List<Image> personImages;
    Random random;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialize");
        random = new Random();
        queues = new HashMap<>();
        personImages = new ArrayList<>();
        try {
            personImages.add(new Image(new FileInputStream("D:\\3 course labs\\CPP\\elevatorProject\\ElevatorSimulator\\src\\sample\\Person1.png")));
            personImages.add(new Image(new FileInputStream("D:\\3 course labs\\CPP\\elevatorProject\\ElevatorSimulator\\src\\sample\\Person2.png")));
            personImages.add(new Image(new FileInputStream("D:\\3 course labs\\CPP\\elevatorProject\\ElevatorSimulator\\src\\sample\\Person3.png")));
            personImages.add(new Image(new FileInputStream("D:\\3 course labs\\CPP\\elevatorProject\\ElevatorSimulator\\src\\sample\\Person4.png")));

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

    private void renderPerson(int floor,int elevator){
        ImageView newPerson = new ImageView(personImages.get(random.nextInt(personImages.size() - 1)));
        newPerson.setFitWidth(personWidth);
        newPerson.setFitHeight(floorHeight / 2);
        newPerson.setLayoutY(floors.get(floor - 1).getY() + floorHeight / 2);
        List<ImageView> persons =  queues.get(new Pair<>(floor - 1,elevator - 1));
        newPerson.setLayoutX(elevators.get(elevator - 1).getX() - personWidth - (persons.size() + 1) * personWidth);
        queues.get(new Pair<>(floor - 1,elevator - 1)).add(newPerson);
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
        for(int i = 1;i <= elevators.size();i++){
            elevatorNum.getItems().add(i);
        }
        for(int i = 1;i <= floors.size();i++){
            floorNum.getItems().add(i);
        }

    }

    private void moveElevatorToFloor(int elevatorNum, int floorNum){
        Runnable animationThread = new Runnable() {
            @Override
            public void run() {
                Rectangle elevator = elevators.get(elevatorNum - 1);
                Rectangle floor = floors.get(floorNum - 1);
                TranslateTransition animation = new TranslateTransition(
                        Duration.seconds(3),elevator
                );
                animation.setToY(floor.getY() - elevator.getY());
                System.out.println(elevator.getY());
                ArrayList<Animation> personAnimations = new ArrayList<>();
                personsInElevator.get(elevatorNum - 1).forEach(person -> {
                    TranslateTransition personAnimation = new TranslateTransition(
                            Duration.seconds(3),person
                    );
                    personAnimation.setToY(floor.getY() + floorHeight / 2 - person.getLayoutY());
                    personAnimations.add(personAnimation);
                });
                animation.play();
                personAnimations.forEach(anim -> {
                    anim.play();
                });
            }
        };
        animationThread.run();
    }

    public void onAnimateClick(ActionEvent event) {
        moveElevatorToFloor(elevatorNum.getSelectionModel().getSelectedItem(),floorNum.getSelectionModel().getSelectedItem());
    }

    public void onPersonRender(ActionEvent event) {
        int elevator = elevatorNum.getSelectionModel().getSelectedItem() ;
        int floor = floorNum.getSelectionModel().getSelectedItem();
        if(queues.get(new Pair<>(floor - 1,elevator - 1)) == null){
            queues.put(new Pair<>(floor - 1,elevator - 1),new ArrayList<>());
        }
        if(queues.get(new Pair<>(floor - 1,elevator - 1)).size() > maxPersonsInQueqe){
            return;
        }
        renderPerson(floor,elevator);
    }

    public void movePersonToElevator(int floor,int elevator){
        Runnable moveAnimation = new Runnable() {
            @Override
            public void run() {
                List<ImageView> queqe = queues.get(new Pair<>(floor - 1,elevator - 1));
                ImageView person = queqe.get(0);
                queqe.remove(person);
                Rectangle rElevator = elevators.get(elevator - 1);
                if(personsInElevator.get(elevator - 1) == null){
                    personsInElevator.put(elevator - 1,new ArrayList<>());
                }
                TranslateTransition animation = new TranslateTransition(
                        Duration.seconds(Math.abs(
                                (rElevator.getX() + personsInElevator.get(elevator - 1).size() * personWidth  - person.getLayoutX())/20)),person
                );
                animation.setToX(rElevator.getX() + personsInElevator.get(elevator - 1).size() * personWidth  - person.getLayoutX());
                personsInElevator.get(elevator - 1).add(person);
                System.out.println(personsInElevator.get(elevator - 1).size());
                animation.play();
                queqe.forEach( p -> {
                    p.setLayoutX(p.getLayoutX() + personWidth);
                });
            }
        };
        moveAnimation.run();
    }

    public void onPersonMove(ActionEvent event) {
        movePersonToElevator(floorNum.getSelectionModel().getSelectedItem(),
                elevatorNum.getSelectionModel().getSelectedItem());
    }
}


