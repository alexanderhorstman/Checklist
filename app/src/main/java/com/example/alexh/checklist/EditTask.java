package com.example.alexh.checklist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.Calendar;

public class EditTask extends Activity{
    private boolean replaceInsteadOfAdd = false;
    private int position;

    private Button timeButton;
    private Button dayButton;
    private CheckBox dailyCheckBox;
    private CheckBox reminderCheckBox;
    private EditText editText;
    private RadioGroup radioGroup;
    private String textHolder;
    DatePickerFragment datePickerFragment;
    TimePickerFragment timePickerFragment;

    @Override
    public void onBackPressed() {
        //set result code to canceled
        setResult(RESULT_CANCELED);
        //leave this activity
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_task);
        Intent previousActivity = getIntent();
        //will receive a blank string from the add item method
        //will receive a string from the edit item method(long click on item)
        String editTextText = previousActivity.getStringExtra("task description");
        //gets the position of the item clicked on
        position = previousActivity.getIntExtra("item position", 0);
        //checks to see if the item should be replaced
        //if editTextText is empty the item will be added
        //if the editTextText is not empty the item will be replaced with the new item
        if(!editTextText.equals("")) {
            textHolder = editTextText;
            replaceInsteadOfAdd = true;
        }
        //get all of the views from this activity
        editText = (EditText) findViewById(R.id.taskDescriptionEditText);
        radioGroup = (RadioGroup) findViewById(R.id.priorityRadioGroup);
        switch (previousActivity.getIntExtra("priority level", Globals.PRIORITY_NORMAL)) {
            case(Globals.PRIORITY_NORMAL):
                radioGroup.check(R.id.radio_normal);
                break;
            case(Globals.PRIORITY_RUSH):
                radioGroup.check(R.id.radio_rush);
                break;
            case(Globals.PRIORITY_URGENT):
                radioGroup.check(R.id.radio_urgent);
                break;
        }
        dailyCheckBox = (CheckBox) findViewById(R.id.addToPresetsCheckBox);
        reminderCheckBox = (CheckBox) findViewById(R.id.include_reminder_checkbox);
        timeButton = (Button) findViewById(R.id.reminder_time_picker_button);
        dayButton = (Button) findViewById(R.id.reminder_day_picker_button);
        timeButton.setClickable(false);
        dayButton.setClickable(false);
        //set the time and day button text and color
        timeButton.setTextColor(Color.GRAY);
        dayButton.setTextColor(Color.GRAY);
        Calendar day = Calendar.getInstance();
        String time;
        if(day.get(Calendar.HOUR) == 0) {
            time = "12:";
        }
        else {
            time = day.get(Calendar.HOUR) + ":";
        }


        if(day.get(Calendar.MINUTE) < 10) {
            time += "0" + day.get(Calendar.MINUTE);
        }
        else {
            time += day.get(Calendar.MINUTE);
        }
        timeButton.setText(time);
        String date = "";
        date += (day.get(Calendar.MONTH) + 1) + "/" + day.get(Calendar.DAY_OF_MONTH) + "/" + day.get(Calendar.YEAR);
        dayButton.setText(date);
        //set the edit text's text from the previous activity
        editText.setText(editTextText);
    }

    public void addItemToList(View view){
        String task = editText.getText().toString();
        int priority = Globals.PRIORITY_NORMAL;
        int id = radioGroup.getCheckedRadioButtonId();
        Time time;
        Date date;
        if(task.equals("")){
            //set result code to canceled
            setResult(RESULT_CANCELED);
            //leave this activity
            finish();
        }
        if(dailyCheckBox.isChecked()) {
            priority = Globals.PRIORITY_DAILY;
        }
        if(id == R.id.radio_normal) {
            priority = Globals.PRIORITY_NORMAL;
        }
        else if(id == R.id.radio_rush) {
            priority = Globals.PRIORITY_RUSH;
        }
        else if(id == R.id.radio_urgent) {
            priority = Globals.PRIORITY_URGENT;
        }
        //create item to add to list
        Item item;
        if(reminderCheckBox.isChecked()) {
            time = timePickerFragment.getTime();
            date = datePickerFragment.getDate();
            item = new Item(task, priority, time, date);
        }
        else {
            item = new Item(task, priority);
        }
        //create intent to send back
        Intent returnToMain = new Intent();
        if(dailyCheckBox.isChecked() && !replaceInsteadOfAdd) {
            //set result code to ok and add to presets
            setResult(Globals.RESULT_OK_EDIT_TASK_WITH_ADD_PRESETS, returnToMain);
        }
        else if(replaceInsteadOfAdd && !dailyCheckBox.isChecked()) {
            setResult(Globals.RESULT_REPLACE_IN_LIST, returnToMain);
            returnToMain.putExtra("old item", textHolder);
        }
        else if(replaceInsteadOfAdd && dailyCheckBox.isChecked()) {
            setResult(Globals.RESULT_REPLACE_IN_LIST_WITH_ADD_PRESETS, returnToMain);
            returnToMain.putExtra("old item", textHolder);
        }
        else {
            //set result code to ok
            setResult(Globals.RESULT_OK_EDIT_TASK, returnToMain);
        }
        //put item in the intent
        returnToMain.putExtra("new item", item);
        //put the item position in the intent
        returnToMain.putExtra("item position", position);
        //leave this activity
        finish();
    }

    public void chooseReminderDay(View view) {
        datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    public void chooseReminderTime(View view) {
        timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(getFragmentManager(), "timePicker");
    }

    public void setAsDailyTask(View view) {
        reminderCheckBox.setChecked(false);
        timeButton.setTextColor(Color.GRAY);
        dayButton.setTextColor(Color.GRAY);
        timeButton.setClickable(false);
        dayButton.setClickable(false);
    }

    public void toggleReminderStatus(View view) {
        dailyCheckBox.setChecked(false);
        if(timeButton.getCurrentTextColor() == Color.GRAY) {
            timeButton.setTextColor(Color.BLACK);
            dayButton.setTextColor(Color.BLACK);
            timeButton.setClickable(true);
            dayButton.setClickable(true);
        }
        else if(timeButton.getCurrentTextColor() == Color.BLACK) {
            timeButton.setTextColor(Color.GRAY);
            dayButton.setTextColor(Color.GRAY);
            timeButton.setClickable(false);
            dayButton.setClickable(false);
        }
    }

    public  void returnToMainActivityOnCancel(View view){
        //set result code to canceled
        setResult(RESULT_CANCELED);
        //leave this activity
        finish();
    }
}
