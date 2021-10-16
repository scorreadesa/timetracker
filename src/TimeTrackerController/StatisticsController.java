package TimeTrackerController;

import TimeTrackerDataManagement.TimeTrackerEntry;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.sql.SQLException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

public class StatisticsController extends AbstractController{

    @FXML private Label dateLabel_;
    @FXML private BorderPane dataView_;

    private BarChart<String, Number> statistics_;

    private int displayedYear_;
    private int displayedMonth_;
    private int displayedDay_;

    public StatisticsController(){

    }

    private void setDate(){
        dateLabel_.setText(LocalDate.of(displayedYear_, displayedMonth_, displayedDay_).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
    private LocalTime addDuration(LocalTime time, int hour, int minutes, int seconds, int nanos){

        time = time.plusHours(hour);
        time = time.plusMinutes(minutes);
        time = time.plusSeconds(seconds);
        time = time.plusNanos(nanos);

        return time;
    }
    private Double computeActivityPercentage(LocalTime activity, LocalTime total){

        double activityTimeInMinutes = (activity.getHour() * 60) + activity.getMinute();
        double totalTimeInMinutes = (total.getHour() * 60) + total.getMinute();
        return (activityTimeInMinutes/totalTimeInMinutes) * 100;

    }

    public void fillBarChartMonthStatistics(){

        try {

            ArrayList<TimeTrackerEntry> activities = controller_.requestAllEntries();
            Month thisMonth = LocalDate.parse(dateLabel_.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy")).getMonth();
            int thisYear = LocalDate.parse(dateLabel_.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy")).getYear();
            //LocalTime activitiesTotalTimethisMonth = LocalTime.of(0,0,0,0);
            Double activitiesTotalTimethisMonth = 0.0;
            TreeMap<String, Double> timeInvestedPerActivity = new TreeMap<>();

            for(TimeTrackerEntry entry : activities){

                if(entry.getDateTTE().toLocalDate().getMonth().equals(thisMonth) && entry.getDateTTE().toLocalDate().getYear() == thisYear){

                    String activity = entry.getActivityTTE();
                    Double activityDuration = (entry.getTotalTTE().toLocalTime().getHour() * 60.0) + entry.getTotalTTE().toLocalTime().getMinute();
                    //activitiesTotalTimethisMonth = addDuration(activitiesTotalTimethisMonth, activityDuration.getHour(), activityDuration.getMinute(), activityDuration.getSecond(), activityDuration.getNano());
                    activitiesTotalTimethisMonth += activityDuration;
                    if(!timeInvestedPerActivity.containsKey(activity)){

                        timeInvestedPerActivity.put(activity, activityDuration);

                    } else {

                        Double currentActivityTime = timeInvestedPerActivity.get(activity);
                        activityDuration = currentActivityTime + activityDuration;
                        timeInvestedPerActivity.replace(activity, activityDuration);
                    }
                }
            }

            XYChart.Series series = new XYChart.Series();

            for(String activity : timeInvestedPerActivity.keySet()){

                //Double percentage = computeActivityPercentage(timeInvestedPerActivity.get(activity), activitiesTotalTimethisMonth);
                Double activityTotalTime = timeInvestedPerActivity.get(activity);
                //Double activityTotalTimeInMinutes = (activityTotalTime.getHour() * 60.0) + activityTotalTime.getMinute();
                Double percentage = (activityTotalTime / activitiesTotalTimethisMonth) * 100.0;
                //System.out.println(activity + ": " + timeInvestedPerActivity.get(activity) + "; " + percentage + "; " + activitiesTotalTimethisMonth);
                series.getData().add(new XYChart.Data(activity, percentage));
            }

            statistics_.getData().add(series);
            dataView_.setCenter(statistics_);

        }catch(SQLException e){

            System.err.println(e.getMessage());
        }
    }
    public void fillBarChartYearStatistics(){
        try {

            ArrayList<TimeTrackerEntry> activities = controller_.requestAllEntries();
            int thisYear = LocalDate.parse(dateLabel_.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy")).getYear();
            //LocalTime activitiesTotalTimethisYear = LocalTime.of(0,0,0,0);
            Double activitiesTotalTimethisYear = 0.0;
            TreeMap<String, Double> timeInvestedPerActivity = new TreeMap<>();

            for(TimeTrackerEntry entry : activities){

                if(entry.getDateTTE().toLocalDate().getYear() == thisYear){

                    String activity = entry.getActivityTTE();
                    //LocalTime activityDuration = entry.getTotalTTE().toLocalTime();
                    Double activityDuration = (entry.getTotalTTE().toLocalTime().getHour() * 60.0) + entry.getTotalTTE().toLocalTime().getMinute();
                    activitiesTotalTimethisYear += activityDuration;
                    if(!timeInvestedPerActivity.containsKey(activity)){

                        timeInvestedPerActivity.put(activity, activityDuration);

                    } else {

                        Double currentActivityTime = timeInvestedPerActivity.get(activity);
                        activityDuration = currentActivityTime + activityDuration;
                        timeInvestedPerActivity.replace(activity, activityDuration);
                    }
                }
            }

            XYChart.Series series = new XYChart.Series();

            for(String activity : timeInvestedPerActivity.keySet()){

                Double activityTotalTime = timeInvestedPerActivity.get(activity);
                Double percentage = (activityTotalTime / activitiesTotalTimethisYear) * 100.0;
                series.getData().add(new XYChart.Data(activity, percentage));

            }

            statistics_.getData().add(series);
            dataView_.setCenter(statistics_);

        }catch(SQLException e){

            System.err.println(e.getMessage());
        }
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
    public void handleMonthStatisticsButton(){

        if(dataView_.getCenter() != null){
            statistics_.getData().clear();
        }
        fillBarChartMonthStatistics();
    }
    @FXML
    public void handleYearStatisticsButton(){

        if(dataView_.getCenter() != null){
            statistics_.getData().clear();
        }
        fillBarChartYearStatistics();
    }
    @FXML
    public void handleRewindYearButton(){

        if(dataView_ != null){
            statistics_.getData().clear();
        }

        if(displayedYear_ - 1 >= 2012){
            --displayedYear_;
        }

        setDate();
        fillBarChartYearStatistics();

    }
    @FXML
    public void handleRewindMonthButton(){

        if(dataView_.getCenter() != null){
            statistics_.getData().clear();
        }

        if(displayedMonth_ - 1 == 0){
            displayedMonth_ = 12;
            --displayedYear_;
        } else {
            --displayedMonth_;
        }

        setDate();
        fillBarChartMonthStatistics();
    }
    @FXML
    public void handleForwardMonthButton(){

        if(dataView_.getCenter() != null){
            statistics_.getData().clear();
        }

        if(displayedMonth_ + 1 > 12){
            displayedMonth_ = 1;
            ++displayedYear_;
        } else {

            ++displayedMonth_;
        }

        setDate();
        fillBarChartMonthStatistics();
    }
    @FXML
    public void handleForwardYearButton(){

        if(dataView_.getCenter() != null){
            statistics_.getData().clear();
        }
        ++displayedYear_;
        setDate();
        fillBarChartYearStatistics();
    }

    public void initialize(){
        Date date = new Date();
        DateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
        String today = dtf.format(date);
        dateLabel_.setText(today);

        displayedYear_ = LocalDate.now().getYear();
        displayedMonth_ = LocalDate.now().getMonth().getValue();
        displayedDay_ = LocalDate.now().getDayOfMonth();

        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis(0, 100, 10);
        x.setLabel("Activities");
        y.setLabel("Time invested in %");


        statistics_ = new BarChart(x, y);
        statistics_.setStyle("-fx-font-weight: bold;");

    }
}
