package TimeTrackerDataManagement;

import Query.SQLQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Model {

    private SQLQuery query_machine_;
    private int entriesCount_;

    public Model(String driver, String url, String user, String pwd)
            throws SQLException, ClassNotFoundException {

        query_machine_ = new SQLQuery(driver, url, user, pwd);
        ResultSet res = query_machine_.query("select id_tte from timetrackerentry");
        res.afterLast();
        if(res.previous()){
            entriesCount_ = res.getInt("id_tte");
        }
    }

    public ArrayList<TimeTrackerEntry> getAllTTEs() throws SQLException{

        ArrayList<TimeTrackerEntry> ret = new ArrayList<>();
        ResultSet res = query_machine_.query("select date_tte, activity, start_te, end_te, total_te, comment from " +
                "(select * from timeentry inner join activity on activity.id_activity = timeentry.activity_id) " +
                "as te_activity_table inner join timetrackerentry " +
                "on timetrackerentry.id_tte = te_activity_table.id_te");

        while(res.next()){
            String date_tte = res.getString("date_tte");
            String activity = res.getString("activity");
            String start_te = res.getString("start_te");
            String end_te = res.getString("end_te");
            String total_te = res.getString("total_te");
            String comment = res.getString("comment");
            TimeTrackerEntry tte = new TimeTrackerEntry(date_tte, activity, start_te, end_te, total_te, comment);
            ret.add(tte);
        }

        return ret;
    }
    public TreeMap<Integer, String> getAllActivities() throws SQLException{

        TreeMap<Integer, String> activities = new TreeMap<>();
        ResultSet res = query_machine_.query("select * from activity order by activity asc");

        while(res.next()){
            Integer id = Integer.valueOf(res.getString("id_activity"));
            String activity = res.getString("activity");
            activities.put(id, activity);
        }

        return activities;
    }
    public ObservableList<TimeTrackerEntry> getActivityByDate(Date date) throws SQLException{

        ObservableList<TimeTrackerEntry> activityDateList = FXCollections.observableArrayList();
        ArrayList<TimeTrackerEntry> entryList = getAllTTEs();

        for(TimeTrackerEntry entry : entryList){

            if(entry.getDateTTE().equals(date)) {

                activityDateList.add(entry);
            }
        }

        return activityDateList;
    }
    public void addActivity(String activity) throws SQLException{

        String query = "insert into activity values (NULL, \"" + activity + "\")";
        query_machine_.insert(query);
    }
    public void  addTimeTrackerEntry(TimeTrackerEntry entry) throws SQLException, IllegalArgumentException{

        TreeMap<Integer, String> activitiesIDMap = getAllActivities();
        int activityID = getActivityID(activitiesIDMap, entry);
        int counter = entriesCount_ + 1;

        String timeEntry = "insert into timeentry values (" +
                "NULL," +
                activityID + "," +
                "'" + entry.getStartTTE() + "'," +
                "'" + entry.getEndTTE() + "'," +
                "'" + entry.getTotalTTE() + "'," +
                "'" + (entry.getCommentTTE() == null || entry.getCommentTTE().isEmpty() ? "NULL" : entry.getCommentTTE()) +
                "')";

        String timeTrackerEntry = "insert into timetrackerentry values (" +
                "NULL," +
                "'" + entry.getDateTTE() + "'," +
                counter + ")" ;

        if(checkEntryIsValid(entry)) {

            query_machine_.insert(timeEntry);
            query_machine_.insert(timeTrackerEntry);
            ++entriesCount_;

        } else {
            throw new IllegalArgumentException("Entry cannot be added because of an overlap with another entry");
        }

    }
    public String getTodoList(Date date) throws SQLException{

        String query = "select * from todolist where todolist.date_todo_entry = '" + date.toString() + "'";
        ResultSet res = query_machine_.query(query);
        if(res.next()) {
            return res.getString("todo_entry_data");
        }
        return "";
    }
    private boolean checkEntryIsValid(TimeTrackerEntry entry) {
        try {
            ArrayList<TimeTrackerEntry> entries = getAllTTEs();

            for(TimeTrackerEntry existingEntry : entries){

                if(entry.getDateTTE().equals(existingEntry.getDateTTE()) &&
                        !(entry.getEndTTE().before(existingEntry.getStartTTE())
                        || entry.getStartTTE().after(existingEntry.getEndTTE()))) {

                    return false;
                }
            }

        }catch(SQLException e){
            System.err.println(e.getMessage());
        }

        return true;
    }
    private int getActivityID(TreeMap<Integer, String> activitiesIDMap, TimeTrackerEntry entry){

        int activityID = -1;

        for(Map.Entry<Integer, String> mapEntry : activitiesIDMap.entrySet()){
            if(mapEntry.getValue().equals(entry.getActivityTTE())){
                activityID = mapEntry.getKey();
            }
        }

        return activityID;
    }

    public void addTodoListEntry(String text, java.sql.Date date) throws SQLException{

        String query = "select * from todolist where todolist.date_todo_entry = '" + date.toString() + "'";
        ResultSet res = query_machine_.query(query);
        if(!res.next()){

            query = "insert into todolist values(NULL, " +
                    "'" + date.toString() + "', " +
                    "'" + text + "')";
            query_machine_.insert(query);
        } else {

            query = "update todolist " +
                    "set todolist.todo_entry_data = '" + text + "'" +
                    "where todolist.date_todo_entry='" + date.toString() + "'";
            query_machine_.insert(query);
        }
    }
}
