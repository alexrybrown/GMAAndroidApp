package com.goalsmadeattainable.goalsmadeattainable.CreateGoal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CreateGoalDatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    private DatePickerDialog datePickerDialog;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (datePickerDialog == null) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        }
        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        datePickerDialog.updateDate(year, month, day);
        // Get needed times for time picker buttons
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat prettyDateFormat = new SimpleDateFormat("MM/dd/yyyy");

        // Set pretty time
        prettyDateFormat.setCalendar(calendar);
        Date date = calendar.getTime();
        CreateGoalActivity.pickDateButton.setText(prettyDateFormat.format(date));

        // Set utc time
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        date = calendar.getTime();
        CreateGoalActivity.values.put("date", dateFormat.format(date));


        // Set default values
        // Change button to reflect time chosen
    }
}
