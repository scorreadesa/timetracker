package TimeTrackerController;

import Query.SQLQuery;
import TimeTrackerDataManagement.TimeTrackerEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class AddTimeController extends AbstractController{

    private int displayedDay_;
    private int displayedMonth_;
    private int displayedYear_;

    @FXML private Label today_;
    @FXML private ComboBox activityInputField_;
    @FXML private ComboBox startHoursField_;
    @FXML private ComboBox startMinutesField_;
    @FXML private ComboBox endHoursField_;
    @FXML private ComboBox endMinutesField_;
    @FXML private TextField commentField_;

    public AddTimeController(){

    }
    public void setDate(int year, int month, int day){

        displayedDay_ = day;
        displayedMonth_ = month;
        displayedYear_ = year;
        String date = LocalDate.of(displayedYear_, displayedMonth_, displayedDay_).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        today_.setText(date);
    }
    public void setActivityList(){

        try {
            ObservableList<String> options = FXCollections.observableArrayList();
            options.addAll(controller_.requestActivitiesList());
            options = options.sorted();
            activityInputField_.setItems(options);
        }catch(SQLException e){

            controller_.spawnErrorPanel(e.getMessage());
        }
    }
    @FXML
    public void handleAddTimeEntry(){

        String date = today_.getText();
        String activity = activityInputField_.getValue().toString();
        String start_tte = startHoursField_.getValue() + ":" + startMinutesField_.getValue() + ":00";
        String end_tte  = endHoursField_.getValue() + ":" + endMinutesField_.getValue() + ":00";
        String comment = commentField_.getText();

        try {
            TimeTrackerEntry entry = new TimeTrackerEntry(date, activity, start_tte, end_tte, comment);
            controller_.requestInsertionTTE(entry);
            commentField_.clear();
            activityInputField_.getSelectionModel().clearSelection();
            startHoursField_.getSelectionModel().clearSelection();
            endHoursField_.getSelectionModel().clearSelection();
            startMinutesField_.getSelectionModel().clearSelection();
            endMinutesField_.getSelectionModel().clearSelection();
            controller_.spawnView("SuccessPopUpView");
        }catch(IllegalArgumentException | NullPointerException | SQLException e){

            controller_.spawnErrorPanel(e.getMessage());
        }

    }

}
