package TimeTrackerController;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class AddActivityController extends AbstractController{

    @FXML TextField activityInputField_;

    public AddActivityController(){

    }

    @FXML
    public void handleAddActivityButton(){

        try {

            controller_.requestInsertionActivity(activityInputField_.getText());
            activityInputField_.clear();
            controller_.spawnView("SuccessPopUpView");

        }catch(SQLException e){

            controller_.spawnErrorPanel(e.getMessage());

        }

    }
}
