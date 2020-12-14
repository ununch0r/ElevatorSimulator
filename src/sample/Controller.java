package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;


public class Controller {
    @FXML
    private Button button;

    public void Click(MouseEvent mouseEvent) {

        button.setText("Hello");

    }
}
