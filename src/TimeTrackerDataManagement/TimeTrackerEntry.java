package TimeTrackerDataManagement;


import javafx.beans.property.SimpleStringProperty;
import java.sql.Date;
import java.sql.Time;
import java.time.*;
import java.time.format.DateTimeFormatter;


public class TimeTrackerEntry {

    private Date date_tte_;
    private String activity_;
    private Time start_te_;
    private Time end_te_;
    private Time total_te_;
    private String comment_;

    public TimeTrackerEntry(String date_tte, String activity, String start_te,
                             String end_te, String comment)  throws IllegalArgumentException, NullPointerException{

        if(date_tte == null || activity == null || start_te == null || end_te == null){
            throw new NullPointerException("TimeTrackerEntry: one or more argument(s) were not properly initialized or set");
        }

        date_tte_ = new Date(Date.valueOf(LocalDate.parse(date_tte, DateTimeFormatter.ofPattern("dd/MM/yyyy"))).getTime());
        activity_ = activity;
        start_te_ = new Time(Time.valueOf(LocalTime.parse(start_te, DateTimeFormatter.ofPattern("HH:mm:ss"))).getTime());
        end_te_ = new Time(Time.valueOf(LocalTime.parse(end_te, DateTimeFormatter.ofPattern("HH:mm:ss"))).getTime());

        if(start_te_.getTime() > end_te_.getTime()){
            throw new IllegalArgumentException("TimeTrackerEntry: start > end");
        }

        int endHours = Integer.valueOf(end_te.substring(0, end_te.indexOf(':')));
        int endMinutes = Integer.valueOf(end_te.substring(end_te.indexOf(':') + 1, end_te.indexOf(':', end_te.indexOf(':') + 1)));

        int startHours = Integer.valueOf(start_te.substring(0, start_te.indexOf(':')));
        int startMinutes = Integer.valueOf(start_te.substring(start_te.indexOf(':') + 1, start_te.indexOf(':', start_te.indexOf(':') + 1)));

        int diffHours = Math.abs(endHours - startHours);
        int diffMinutes = Math.floorMod(endMinutes - startMinutes, 60);

        if(endMinutes - startMinutes < 0){
            --diffHours;
        }

        String diffTime = "";

        if(diffHours < 10){
            diffTime += "0" + diffHours + ":";
        } else {
            diffTime += diffHours + ":";
        }

        if(diffMinutes < 10){
            diffTime += "0" + diffMinutes + ":";
        } else {
            diffTime += diffMinutes + ":";
        }

        diffTime += "00";

        total_te_ = new Time(Time.valueOf(LocalTime.parse(diffTime, DateTimeFormatter.ofPattern("HH:mm:ss"))).getTime());
        comment_ = comment;
    }

    public TimeTrackerEntry(String date_tte, String activity, String start_te, String end_te, String total_te, String comment){

        date_tte_ = new Date(Date.valueOf(LocalDate.parse(date_tte, DateTimeFormatter.ofPattern("yyyy-MM-dd"))).getTime());
        activity_ = activity;
        start_te_ = new Time(Time.valueOf(LocalTime.parse(start_te, DateTimeFormatter.ofPattern("HH:mm:ss"))).getTime());
        end_te_ = new Time(Time.valueOf(LocalTime.parse(end_te, DateTimeFormatter.ofPattern("HH:mm:ss"))).getTime());
        total_te_ = new Time(Time.valueOf(LocalTime.parse(total_te, DateTimeFormatter.ofPattern("HH:mm:ss"))).getTime());
        comment_ = comment;
    }

    @Override
    public String toString(){
        return date_tte_.toString() + " " + activity_ + "\n\t" + "start: "
                + start_te_.toString() + "\n\t" + "end: " + end_te_.toString() + "\n\t"
                + "total_te: " + total_te_.toString()
                + "\n\t" + "comment: " + (comment_ == null ? "" : comment_);
    }
    public String getActivityTTE(){
        return activity_;
    }
    public Date getDateTTE(){
        return date_tte_;
    }
    public Time getStartTTE(){
        return start_te_;
    }
    public Time getEndTTE(){
        return end_te_;
    }
    public Time getTotalTTE(){
        return total_te_;
    }
    public String getCommentTTE(){return comment_;}

    public SimpleStringProperty getDate(){

        return new SimpleStringProperty(date_tte_.toString());
    }
    public SimpleStringProperty getActivity(){

        return new SimpleStringProperty(activity_);
    }
    public SimpleStringProperty getStartTime(){

        return new SimpleStringProperty(start_te_.toString());

    }
    public SimpleStringProperty getEndTime(){

        return new SimpleStringProperty(end_te_.toString());
    }
    public SimpleStringProperty getTotalTime(){

        return new SimpleStringProperty(total_te_.toString());
    }
    public SimpleStringProperty getComment(){

        return new SimpleStringProperty(comment_);
    }
}
