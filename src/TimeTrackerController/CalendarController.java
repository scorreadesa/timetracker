package TimeTrackerController;

import TimeTrackerDataManagement.TimeTrackerEntry;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Date;

public class CalendarController extends AbstractController {

    @FXML private Label today_;
    @FXML private GridPane calendarArea_;

    private int displayedDay_;
    private int displayedMonth_;
    private int displayedYear_;

    public CalendarController(){

    }

    @FXML
    public void handleAddActivityButton(){

        controller_.spawnView("AddActivityPopUpView");
    }
    @FXML
    public void handleShowStatisticsViewButton(){

        controller_.swapView("StatisticsView");
    }
    @FXML
    public void handleRewindYearButton(){

        if(displayedYear_ - 1 >= 2012){
            --displayedYear_;
        }

        clearCalendarArea();
        fillCalendarArea(displayedYear_, displayedMonth_);
        setDate();
    }
    @FXML
    public void handleRewindMonthButton(){

        if(displayedMonth_ - 1 == 0){
            displayedMonth_ = 12;
            --displayedYear_;
        } else {
            --displayedMonth_;
        }

        clearCalendarArea();
        fillCalendarArea(displayedYear_, displayedMonth_);
        setDate();
    }
    @FXML
    public void handleForwardMonthButton(){

        if(displayedMonth_ + 1 > 12){
            displayedMonth_ = 1;
            ++displayedYear_;
        } else {

            ++displayedMonth_;
        }

        clearCalendarArea();
        fillCalendarArea(displayedYear_, displayedMonth_);
        setDate();
    }
    @FXML
    public void handleForwardYearButton(){

        ++displayedYear_; //let's assume I'll live forever ;-)
        clearCalendarArea();
        fillCalendarArea(displayedYear_, displayedMonth_);
        setDate();
    }

    public void initialize(){

        java.util.Date date = new java.util.Date();
        DateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
        String today = dtf.format(date);
        today_.setText(today);

        displayedYear_ = LocalDate.now().getYear();
        displayedMonth_ = LocalDate.now().getMonth().getValue();
        displayedDay_ = LocalDate.now().getDayOfMonth();

        fillCalendarArea(LocalDate.now().getYear(), LocalDate.now().getMonth().getValue());
    }

    private void fillCalendarArea(int year, int month){

        int numberOfDays = LocalDate.of(year, month, 1).lengthOfMonth();
        int calendarEntryDay = LocalDate.of(year, month, 1).getDayOfWeek().getValue();

        int rowIndex = 1;
        int colIndex = calendarEntryDay - 1;

        for(int day = 1; day <= numberOfDays; day++){

            StackPane calendarEntry = new StackPane();
            String calendarEntryDayString = "" + day;
            calendarEntry.getChildren().add(new Label(calendarEntryDayString));
            calendarEntry.setAlignment(Pos.TOP_LEFT);
            calendarEntry.setStyle("-fx-border-color: rgb(30,122,30)");

            calendarEntry.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    calendarEntry.setStyle("-fx-background-color: linear-gradient(to bottom left, darkcyan, #9198e5)");
                }
            });

            calendarEntry.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    calendarEntry.setStyle("-fx-border-color: rgb(30,122,30)");
                }
            });

            calendarEntry.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {

                    if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){

                        try {

                            Label text = (Label) calendarEntry.getChildren().get(0);
                            int day = Integer.valueOf(text.getText());
                            String date = LocalDate.of(displayedYear_, displayedMonth_, day).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                            ShowTimeController controller = (ShowTimeController)controller_.requestController("ShowTimeView");
                            Date dateTarget = new Date(Date.valueOf(LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))).getTime());
                            ObservableList<TimeTrackerEntry> activityDayList = controller_.requestActivitiesByDate(dateTarget);
                            controller.fillDisplayPane(activityDayList);
                            controller.setDate(displayedYear_, displayedMonth_, day);
                            controller_.swapView("ShowTimeView");

                        }catch(SQLException e){
                            System.err.println(e.getMessage());
                        }

                    } else if(mouseEvent.getButton().equals(MouseButton.SECONDARY)){

                        Label tileText = (Label)calendarEntry.getChildren().get(0);
                        int day = Integer.parseInt(tileText.getText());
                        AddTimeController controller = (AddTimeController)controller_.requestController("AddTimePopUpView");
                        controller.setDate(displayedYear_, displayedMonth_, day);
                        controller.setActivityList();
                        controller_.spawnView("AddTimePopUpView");
                    } else if(mouseEvent.getButton().equals(MouseButton.MIDDLE)){

                        try {
                            Label tileText = (Label) calendarEntry.getChildren().get(0);
                            int day = Integer.parseInt(tileText.getText());
                            TODOListController controller = (TODOListController) controller_.requestController("TODOListPopUpView");
                            controller.setDate(displayedYear_, displayedMonth_, day);
                            String todolist = controller_.requestDailyTasks(Date.valueOf(LocalDate.of(displayedYear_, displayedMonth_, day)));
                            controller.setTodoList(todolist);
                            controller_.spawnView("TODOListPopUpView");

                        }catch(SQLException e){
                            System.out.println(e.getMessage());
                        }
                    }
                }
            });

            calendarArea_.add(calendarEntry, colIndex, rowIndex);

            colIndex++;
            calendarEntryDay++;

            if(colIndex % 7 == 0){
                rowIndex++;
                colIndex = 0;
            }
        }

    }
    private void clearCalendarArea(){
        calendarArea_.getChildren().removeAll(calendarArea_.getChildren().filtered(node -> {
            return calendarArea_.getRowIndex(node) > 0;
        }));
    }
    private void setDate(){
        today_.setText(new Label(LocalDate.of(displayedYear_, displayedMonth_, displayedDay_).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).getText());
    }
}
