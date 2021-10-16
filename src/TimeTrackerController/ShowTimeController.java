package TimeTrackerController;


import TimeTrackerDataManagement.TimeTrackerEntry;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class ShowTimeController extends AbstractController{

    private int displayedDay_;
    private int displayedMonth_;
    private int displayedYear_;

    @FXML private Label today_;
    @FXML private TableView activityDisplayPane_;
    @FXML private TableColumn activityColumn_;
    @FXML private TableColumn timeStartColumn_;
    @FXML private TableColumn timeEndColumn_;
    @FXML private TableColumn durationColumn_;
    @FXML private TableColumn commentColumn_;

    public ShowTimeController(){

    }
    public void setDate(int year, int month, int day){

        displayedDay_ = day;
        displayedMonth_ = month;
        displayedYear_ = year;
        String date = LocalDate.of(displayedYear_, displayedMonth_, displayedDay_).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        today_.setText(date);
    }
    public void fillDisplayPane(ObservableList<TimeTrackerEntry> activities){

        activityColumn_.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TimeTrackerEntry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<TimeTrackerEntry, String> cellDataFeatures) {
                return cellDataFeatures.getValue().getActivity();
            }
        });

        timeStartColumn_.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TimeTrackerEntry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<TimeTrackerEntry, String> cellDataFeatures) {
                return cellDataFeatures.getValue().getStartTime();
            }
        });

        timeEndColumn_.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TimeTrackerEntry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<TimeTrackerEntry, String> cellDataFeatures) {
                return cellDataFeatures.getValue().getEndTime();
            }
        });

        durationColumn_.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TimeTrackerEntry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<TimeTrackerEntry, String> cellDataFeatures) {
                return cellDataFeatures.getValue().getTotalTime();
            }
        });

        commentColumn_.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TimeTrackerEntry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<TimeTrackerEntry, String> cellDataFeatures) {
                return cellDataFeatures.getValue().getComment();
            }
        });

        activityDisplayPane_.setItems(activities);

    }

    @FXML
    public void handleCalendarViewButton(){

        controller_.swapView("CalendarView");
    }
    @FXML
    public void handleAddActivityButton(){

        controller_.spawnView("AddActivityPopUpView");
    }
    @FXML
    public void handleStatisticsViewButton(){

        controller_.swapView("StatisticsView");
    }
    @FXML
    public void handleRewindYearButton(){

        if(displayedYear_ - 1 >= 2012){
            --displayedYear_;
        }

        try {
            Date date = new Date(Date.valueOf(LocalDate.of(displayedYear_, displayedMonth_, displayedDay_)).getTime());
            ObservableList<TimeTrackerEntry> activities = controller_.requestActivitiesByDate(date);
            fillDisplayPane(activities);
            setDate(displayedYear_, displayedMonth_, displayedDay_);
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }
    @FXML public void handleRewindMonthButton(){

        if(displayedMonth_ - 1 == 0){
            displayedMonth_ = 12;
            --displayedYear_;
        } else {
            --displayedMonth_;
        }

        try{

            Date date = new Date(Date.valueOf(LocalDate.of(displayedYear_, displayedMonth_, displayedDay_)).getTime());
            ObservableList<TimeTrackerEntry> activities = controller_.requestActivitiesByDate(date);
            fillDisplayPane(activities);
            setDate(displayedYear_, displayedMonth_, displayedDay_);
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }
    @FXML public void handleForwardMonthButton(){

        if(displayedMonth_ + 1 > 12){
            displayedMonth_ = 1;
            ++displayedYear_;
        } else {
            ++displayedMonth_;
        }

        try{

            Date date = new Date(Date.valueOf(LocalDate.of(displayedYear_, displayedMonth_, displayedDay_)).getTime());
            ObservableList<TimeTrackerEntry> activities = controller_.requestActivitiesByDate(date);
            fillDisplayPane(activities);
            setDate(displayedYear_, displayedMonth_, displayedDay_);
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }
    @FXML public void handleForwardYearButton(){

        ++displayedYear_;
        try {
            Date date = new Date(Date.valueOf(LocalDate.of(displayedYear_, displayedMonth_, displayedDay_)).getTime());
            ObservableList<TimeTrackerEntry> activities = controller_.requestActivitiesByDate(date);
            fillDisplayPane(activities);
            setDate(displayedYear_, displayedMonth_, displayedDay_);
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }
    public void initialize(){

        activityDisplayPane_.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        activityDisplayPane_.setEditable(true);

    }
}
