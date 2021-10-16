package TimeTrackerController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FailureController extends AbstractController{

    @FXML
    private Label error_;

    public FailureController(){

    }

    public void setErrorCause(String cause){
        error_.setText(cause);
        error_.setStyle("-fx-text-fill: red");
    }
}
