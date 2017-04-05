package com.goalsmadeattainable.goalsmadeattainable.CreateGoal;

import android.app.TimePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class EditOrCreateGoalTimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    private TimePickerDialog timePickerDialog;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private SimpleDateFormat prettyTimeFormat = new SimpleDateFormat("hh:mm a");
    private java.text.DateFormat currentDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        if (timePickerDialog == null) {
            if (EditOrCreateGoalActivity.values.get("edit_goal_time") != null) {
                try {
                    currentDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    c.setTime(currentDateFormat.parse((String) EditOrCreateGoalActivity.values.get("edit_goal_time")));
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);

                    // Create a new instance of DatePickerDialog and return it
                    timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute,
                            DateFormat.is24HourFormat(getActivity()));

                    // Initialize values
                    updateValues(hour, minute);
                } catch (ParseException e) {}
            } else {
                // Use the current time as the default values for the picker
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // Create a new instance of TimePickerDialog and return it
                timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute,
                        DateFormat.is24HourFormat(getActivity()));

                // Initialize values
                updateValues(hour, minute);
            }
        }
        return timePickerDialog;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        updateValues(hourOfDay, minute);
    }

    private void updateValues(int hourOfDay, int minute) {
        timePickerDialog.updateTime(hourOfDay, minute);
        // Get needed times for time picker buttons
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            calendar.setTime(dateFormat.parse((String) EditOrCreateGoalActivity.values.get("date")));
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
        } catch (ParseException e) {}

        // Set pretty time
        prettyTimeFormat.setCalendar(calendar);
        Date date = calendar.getTime();
        EditOrCreateGoalActivity.pickTimeButton.setText(prettyTimeFormat.format(date));

        // Set time for database storage
        EditOrCreateGoalActivity.values.put("time", timeFormat.format(date));
    }
}
