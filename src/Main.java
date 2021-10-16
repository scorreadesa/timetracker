import TimeTrackerController.Controller;
import TimeTrackerDataManagement.Model;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Model model = new Model("com.mysql.cj.jdbc.Driver", "", "",
                "");

        ArrayList<String> views_locations = new ArrayList<>();
        views_locations.add("/TimeTrackerView/AddActivityPopUpView.fxml");
        views_locations.add("/TimeTrackerView/AddTimePopUpView.fxml");
        views_locations.add("/TimeTrackerView/CalendarView.fxml");
        views_locations.add("/TimeTrackerView/FailurePopUpView.fxml");
        views_locations.add("/TimeTrackerView/StatisticsView.fxml");
        views_locations.add("/TimeTrackerView/SuccessPopUpView.fxml");
        views_locations.add("/TimeTrackerView/ShowTimeView.fxml");
        views_locations.add("/TimeTrackerView/TODOListPopUpView.fxml");

        Controller controller = new Controller(model, views_locations);
        controller.registerController();
        controller.startApplication();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
