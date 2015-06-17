package com.example.alexh.checklist;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener{

    private Time time;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = 0;

        return new TimePickerDialog(getActivity(), this, (hour + 1), minute, false);
    }

    public Time getTime() {
        return time;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        boolean isAm = true;
        int hour;
        Button timeButton = (Button) getActivity().findViewById(R.id.reminder_time_picker_button);
        if(hourOfDay > 11) {
            isAm = false;
            hour = hourOfDay - 12;
        }
        else {
            hour = hourOfDay;
        }
        if(hour == 0) {
            hour = 12;
        }
        time = new Time(hour, minute, isAm, hourOfDay);
        timeButton.setText(time.toString());
    }
}
