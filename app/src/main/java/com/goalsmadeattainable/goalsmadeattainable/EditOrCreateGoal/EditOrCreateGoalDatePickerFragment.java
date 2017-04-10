package com.goalsmadeattainable.goalsmadeattainable.EditOrCreateGoal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
        final DatePickerDialog.OnDateSetListener obj = this;
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

        // Rest data on cancel
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    try {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(dateFormat.parse((String) EditOrCreateGoalActivity.values.get("date")));
                        datePickerDialog = new DatePickerDialog(getActivity(), obj, calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    } catch (ParseException e) {}
                }
            }
        });
        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        updateValues(year, month, day);
    }

    private void updateValues(int year, int month, int day) {
        datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
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
