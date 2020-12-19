package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("FXML/sample.fxml"));
        primaryStage.setTitle("Elevator simulation");
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        Screen screen = Screen.getPrimary();
        double sceneWidth = screen.getVisualBounds().getWidth();
        double sceneHeight = screen.getVisualBounds().getHeight();
        System.out.println(screen.getVisualBounds().getHeight());
        primaryStage.setScene(new Scene(root, sceneWidth, sceneHeight));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
