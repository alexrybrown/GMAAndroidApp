package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Goal {
    public int userID;
    public int goalID;
    public Integer futureGoalID;
    public String title;
    public String description;
    public String comment;
    public String createdAt;
    public String expectedCompletion;
    public String finishedAt;
    public String lastModified;
    public Boolean archived;

    public Goal() {}

    public Goal(int userID, int goalID, Integer futureGoalID, String title, String description, String comment,
                String createdAt, String expectedCompletion, String finishedAt, String lastModified,
                Boolean archived) {
        this.userID = userID;
        this.goalID = goalID;
        this.futureGoalID = futureGoalID;
        this.title = title;
        this.description = description;
        this.comment = comment;
        this.createdAt = createdAt;
        this.expectedCompletion = expectedCompletion;
        this.finishedAt = finishedAt;
        this.lastModified = lastModified;
        this.archived = archived;
    }

    public String getCreatedAt() {
        return formatDate(createdAt);
    }

    public String getExpectedCompletion() {
        return formatDate(expectedCompletion);
    }

    public String getFinishedAt() {
        return formatDate(finishedAt);
    }

    public String getLastModified() {
        return formatDate(lastModified);
    }

    private String formatDate(String date) {
        DateFormat finalDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        DateFormat currentDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        currentDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(currentDateFormat.parse(date));
            return finalDateFormat.format(calendar.getTime());
        } catch (ParseException e) {
            return date;
        }
    }
}
