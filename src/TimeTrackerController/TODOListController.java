package TimeTrackerController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TODOListController extends AbstractController {

    private int displayedDay_;
    private int displayedMonth_;
    private int displayedYear_;

    @FXML
    private Label today_;
    @FXML
    private TextArea todoListUserInput_;

    public TODOListController(){

    }

    public void initalize(){
        java.util.Date date = new java.util.Date();
        DateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
        String today = dtf.format(date);
        today_.setText(today);
    }
    @FXML
    private void handleSaveButton(){
        Date date = Date.valueOf(LocalDate.of(displayedYear_, displayedMonth_, displayedDay_));
        try {
            controller_.requestInsertTodoList(todoListUserInput_.getText(), date);
        }catch(SQLException e){
            FailureController controller = (FailureController) controller_.requestController("FailureController");
            controller.setErrorCause(e.getMessage());
            controller_.spawnView("FailurePopUpView");
        }
    }
    public void setDate(int year, int month, int day){

        displayedDay_ = day;
        displayedMonth_ = month;
        displayedYear_ = year;
        String date = LocalDate.of(displayedYear_, displayedMonth_, displayedDay_).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        today_.setText(date);
    }
    public void setTodoList(String todolist){

        todoListUserInput_.setText(todolist);
    }

}
