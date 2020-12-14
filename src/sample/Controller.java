package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private Canvas elevatorCanvas;
    @FXML
    private ComboBox<Integer> floorsCount;
    @FXML
    private ComboBox<Integer> elevatorsCount;

    private GraphicsContext gc;
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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialize");
        gc = elevatorCanvas.getGraphicsContext2D();
        for(int i = 2;i <= maxFloors;i++){
            floorsCount.getItems().add(i);
        }
        for(int i = 1;i <= maxElevators;i++){
            elevatorsCount.getItems().add(i);
        }

        gc = elevatorCanvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0,0,elevatorCanvas.getWidth(),elevatorCanvas.getHeight());
    }


    private void renderFloors(int count){
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0,0,elevatorCanvas.getWidth(),elevatorCanvas.getHeight());
        for(int i = 1;i <= count;i++) {
            Stop[] stops = new Stop[]{new Stop(0, Color.web("#4B4A4E")), new Stop(0.4, Color.web("#646266")),
                    new Stop(0.6, Color.web("#646266")),new Stop(1, Color.web("#4B4A4E"))};
            LinearGradient lg1 = new LinearGradient(0.5, 1, 0.5, 0, true, CycleMethod.NO_CYCLE, stops);
            gc.setFill(lg1);
            gc.fillRect(0, elevatorCanvas.getHeight() - floorHeight * i, elevatorCanvas.getWidth(), floorHeight);
            gc.setFill(Color.color(0.5,0.5,0.5,0.6));
            gc.setFont(Font.font("Tahoma", FontWeight.SEMI_BOLD,floorNumberSize));
            gc.fillText(String.valueOf(i),floorNumberOffset,(elevatorCanvas.getHeight() - floorHeight * i) + floorHeight/1.4);
        }
    }

    private void renderElevators(int count){
        gc.setFill(Color.BLUE);
        for(int i = 1;i <= count;i++){
            gc.fillRect((spaceBetweenElevators * i) + (i * elevatorWidth),elevatorCanvas.getHeight() - floorHeight,
                    elevatorWidth,floorHeight);
        }
    }

    public void onCanvasClick(MouseEvent mouseEvent) {
        System.out.println(mouseEvent.getX());
        System.out.println(mouseEvent.getY());
    }

    public void onFloorCountChange(ActionEvent event) {
        gc.clearRect(0,0,elevatorCanvas.getWidth(),elevatorCanvas.getHeight());
        renderFloors(floorsCount.getSelectionModel().getSelectedItem());
        if(elevatorsCount.getSelectionModel().getSelectedItem() != null)
            renderElevators(elevatorsCount.getSelectionModel().getSelectedItem());
    }

    public void onElevatorCountChange(ActionEvent event) {
        gc.clearRect(0,0,elevatorCanvas.getWidth(),elevatorCanvas.getHeight());
        if(floorsCount.getSelectionModel().getSelectedItem() != null) {
            renderFloors(floorsCount.getSelectionModel().getSelectedItem());
        }
        renderElevators(elevatorsCount.getSelectionModel().getSelectedItem());
    }
}
