package TimeTrackerController;

import Factories.LoaderFactory;
import TimeTrackerDataManagement.Model;
import TimeTrackerDataManagement.TimeTrackerEntry;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

public class Controller {

    private Model model_;
    private Stage primaryStage_;

    private TreeMap<String, Scene> viewList_;
    private TreeMap<String, AbstractController> controllerList_;

    public Controller(Model model, ArrayList<String> resources){

        try {

            model_ = model;

            LoaderFactory loaderFactory = new LoaderFactory();
            viewList_ = new TreeMap<>();
            controllerList_ = new TreeMap<>();

            for (String resource : resources) {
                loaderFactory.register(resource);
                String name = resource.substring(resource.lastIndexOf("/") + 1, resource.indexOf("."));
                FXMLLoader loader = loaderFactory.get(name);
                Scene scene = new Scene(loader.load());
                scene.getStylesheets().add("TimeTrackerView/stylesheet.css");
                viewList_.put(name, scene);
                controllerList_.put(name, loader.getController());
            }

            primaryStage_ = new Stage();
            //primaryStage_.setResizable(false);
            primaryStage_.setTitle("TimeTrackerApp");
            Scene scene = viewList_.get("CalendarView");
            primaryStage_.setScene(scene);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void spawnView(String view){

        Stage primaryStage = new Stage();
        Scene scene = viewList_.get(view);
        scene.getStylesheets().add("TimeTrackerView/stylesheet.css");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("TimeTrackerApp");
        primaryStage.show();
    }
    public void registerController(){

        for(AbstractController controller : controllerList_.values()){
            controller.registerController(this);
        }
    }
    public void swapView(String view){

        primaryStage_.setScene(viewList_.get(view));
    }
    public void spawnErrorPanel(String message){

        FailureController controller = (FailureController)controllerList_.get("FailurePopUpView");
        controller.setErrorCause(message);
        spawnView("FailurePopUpView");
    }

    public AbstractController requestController(String view){

        return controllerList_.get(view);
    }
    public void requestInsertionActivity(String query) throws SQLException{
            model_.addActivity(query);

    }
    public void requestInsertionTTE(TimeTrackerEntry entry) throws SQLException, IllegalArgumentException{
            model_.addTimeTrackerEntry(entry);

    }
    public ObservableList<TimeTrackerEntry> requestActivitiesByDate(Date date) throws SQLException{

        return model_.getActivityByDate(date);

    }
    public ArrayList<String> requestActivitiesList() throws SQLException{

        ArrayList<String> activities = new ArrayList<>();
        activities.addAll(model_.getAllActivities().values());
        return activities;
    }
    public ArrayList<TimeTrackerEntry> requestAllEntries() throws SQLException{

        return model_.getAllTTEs();
    }
    public String requestDailyTasks(Date date) throws SQLException{

        return model_.getTodoList(date);

    }
    public void startApplication(){

        primaryStage_.show();
    }

    public void requestInsertTodoList(String text, Date date) throws SQLException{
        model_.addTodoListEntry(text, date);
    }
}
