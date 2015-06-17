package com.example.alexh.checklist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener{

    Date date;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public Date getDate() {
        return date;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        date = new Date((month + 1), day, year);
        //update button text
        Button dayButton = (Button) getActivity().findViewById(R.id.reminder_day_picker_button);
        //dayButton.setText((month + 1) + "/" + day + "/" + year);
        dayButton.setText(date.toString());
    }
}
