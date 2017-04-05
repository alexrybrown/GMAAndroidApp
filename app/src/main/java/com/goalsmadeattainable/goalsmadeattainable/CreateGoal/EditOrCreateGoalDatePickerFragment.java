package com.goalsmadeattainable.goalsmadeattainable.CreateGoal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class EditOrCreateGoalDatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat prettyDateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private DateFormat currentDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        if (datePickerDialog == null) {
            if (EditOrCreateGoalActivity.values.get("edit_goal_time") != null) {
                try {
                    currentDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    c.setTime(currentDateFormat.parse((String) EditOrCreateGoalActivity.values.get("edit_goal_time")));
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    // Create a new instance of DatePickerDialog and return it
                    datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);

                    // Initial setup for values
                    updateValues(year, month, day);
                } catch (ParseException e) {}
            } else {
                // Use the current date as the default date in the picker
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Create a new instance of DatePickerDialog and return it
                datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);

                // Initial setup for values
                updateValues(year, month, day);
            }
        }
        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        updateValues(year, month, day);
    }

    private void updateValues(int year, int month, int day) {
        datePickerDialog.updateDate(year, month, day);
        // Get needed times for time picker buttons
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        // Set pretty time
        prettyDateFormat.setCalendar(calendar);
        Date date = calendar.getTime();
        EditOrCreateGoalActivity.pickDateButton.setText(prettyDateFormat.format(date));

        // Put in time for data storage
        EditOrCreateGoalActivity.values.put("date", dateFormat.format(date));
    }
}
