package com.goalsmadeattainable.goalsmadeattainable.CreateGoal;

import android.app.TimePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CreateGoalTimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    private TimePickerDialog timePickerDialog;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (timePickerDialog == null) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }
        return timePickerDialog;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        timePickerDialog.updateTime(hourOfDay, minute);
        // Get needed times for time picker buttons
        Calendar calendar = Calendar.getInstance();
        calendar.set(0, 0, 0, hourOfDay, minute);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat prettyTimeFormat = new SimpleDateFormat("hh:mm a");

        // Set pretty time
        prettyTimeFormat.setCalendar(calendar);
        Date date = calendar.getTime();
        CreateGoalActivity.pickTimeButton.setText(prettyTimeFormat.format(date));

        // Set utc time
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        date = calendar.getTime();
        CreateGoalActivity.values.put("time", timeFormat.format(date));
    }
}
