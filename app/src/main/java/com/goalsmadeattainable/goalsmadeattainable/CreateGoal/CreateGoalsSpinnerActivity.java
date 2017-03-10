package com.goalsmadeattainable.goalsmadeattainable.CreateGoal;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;

public class CreateGoalsSpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Another interface callback
    }
}
