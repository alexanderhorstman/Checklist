package com.example.alexh.checklist;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.io.ObjectOutputStream;
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
    private DatePickerFragment datePickerFragment;
    private TimePickerFragment timePickerFragment;

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
        Item item = null;
        //will receive a blank string from the add item method
        //will receive a string from the edit item method(long click on item)
        String editTextText = previousActivity.getStringExtra("task description");
        //gets the position of the item clicked on
        position = previousActivity.getIntExtra("item position", 0);
        //checks to see if the item should be replaced
        //if editTextText is empty the item will be added
        //if the editTextText is not empty the item will be replaced with the new item
        if(!editTextText.equals("")) {
            replaceInsteadOfAdd = true;
            item = Globals.list.getItem(position);
        }
        //get all of the views from this activity
        editText = (EditText) findViewById(R.id.taskDescriptionEditText);
        radioGroup = (RadioGroup) findViewById(R.id.priorityRadioGroup);
        dailyCheckBox = (CheckBox) findViewById(R.id.addToPresetsCheckBox);
        reminderCheckBox = (CheckBox) findViewById(R.id.include_reminder_checkbox);
        timeButton = (Button) findViewById(R.id.reminder_time_picker_button);
        dayButton = (Button) findViewById(R.id.reminder_day_picker_button);
        if(replaceInsteadOfAdd) {
            //set the radio button to the previous setting for this item
            switch (previousActivity.getIntExtra("priority level", Item.PRIORITY_NORMAL)) {
                case(Item.PRIORITY_NORMAL):
                    radioGroup.check(R.id.radio_normal);
                    break;
                case(Item.PRIORITY_MEDIUM):
                    radioGroup.check(R.id.radio_rush);
                    break;
                case(Item.PRIORITY_HIGH):
                    radioGroup.check(R.id.radio_urgent);
                    break;
            }
            //set the reminder if it was previously set on item
            if(item != null && item.hasReminder()) {
                //set time and date button texts to the previous time and date
                Time time = item.getTime();
                Date date = item.getDate();
                timeButton.setText(time.toString());
                dayButton.setText(date.toString());
                //allow buttons to be clickable
                timeButton.setClickable(true);
                dayButton.setClickable(true);
                //set buttons text color to black
                timeButton.setTextColor(Color.BLACK);
                dayButton.setTextColor(Color.BLACK);
                reminderCheckBox.setChecked(true);
            }
            else {
                //do not allow buttons to be clickable until the reminder checkbox is checked
                timeButton.setClickable(false);
                dayButton.setClickable(false);
                //set the time and day button text and color
                timeButton.setTextColor(Color.GRAY);
                dayButton.setTextColor(Color.GRAY);
                //get current time and date for default button text status
                Calendar day = Calendar.getInstance();
                String time;
                if(day.get(Calendar.HOUR) == 0) {
                    time = "12:";
                }
                else if(day.get(Calendar.HOUR) == 12) {
                    time = "1:";
                }
                else {
                    time = (day.get(Calendar.HOUR) + 1) + ":";
                }
                time += "00";
                if(day.get(Calendar.HOUR_OF_DAY) > 11) {
                    time += " PM";
                }
                else {
                    time += " AM";
                }
                timeButton.setText(time);
                String date = "";
                date += (day.get(Calendar.MONTH) + 1) + "/" + day.get(Calendar.DAY_OF_MONTH) + "/" +
                        day.get(Calendar.YEAR);
                dayButton.setText(date);
            }
            //set the edit text's text from the previous activity
            editText.setText(editTextText);
        }
        else {
            //set the radio button to default
            radioGroup.check(R.id.radio_normal);
            //set the default status of the reminder time and date buttons
            //do not allow buttons to be clickable until the reminder checkbox is checked
            timeButton.setClickable(false);
            dayButton.setClickable(false);
            //set the time and day button text and color
            timeButton.setTextColor(Color.GRAY);
            dayButton.setTextColor(Color.GRAY);
            //get current time and date for default button text status
            Calendar day = Calendar.getInstance();
            String time;
            if(day.get(Calendar.HOUR) == 0) {
                time = "12:";
            }
            else if(day.get(Calendar.HOUR) == 12) {
                time = "1:";
            }
            else {
                time = (day.get(Calendar.HOUR) + 1) + ":";
            }
            time += "00";
            if(day.get(Calendar.HOUR_OF_DAY) > 11) {
                time += " PM";
            }
            else {
                time += " AM";
            }
            timeButton.setText(time);
            String date = "";
            date += (day.get(Calendar.MONTH) + 1) + "/" + day.get(Calendar.DAY_OF_MONTH) + "/" +
                    day.get(Calendar.YEAR);
            dayButton.setText(date);
        }
    }

    public void addItemToList(View view){
        String task = editText.getText().toString();
        int priority = Item.PRIORITY_NORMAL;
        int id = radioGroup.getCheckedRadioButtonId();
        //create intent to send back
        Intent returnToMain = new Intent();
        Time time;
        Date date;
        if(task.equals("")){
            //set result code to canceled
            setResult(RESULT_CANCELED, returnToMain);
        }
        else {
            if(dailyCheckBox.isChecked()) {
                priority = Item.PRIORITY_DAILY;
            }
            if(id == R.id.radio_normal) {
                priority = Item.PRIORITY_NORMAL;
            }
            else if(id == R.id.radio_rush) {
                priority = Item.PRIORITY_MEDIUM;
            }
            else if(id == R.id.radio_urgent) {
                priority = Item.PRIORITY_HIGH;
            }
            else if(id == -1) {
                priority = Item.PRIORITY_DAILY;
            }
            //create item to add to list
            Item item;
            if(reminderCheckBox.isChecked()) {
                if(timePickerFragment != null) {
                    //get time from time picker
                    time = timePickerFragment.getTime();
                }
                else {
                    Calendar calendar = Calendar.getInstance();
                    int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                    int hour;
                    int minute = 0;
                    boolean isAm;
                    if(hourOfDay > 11) {
                        hour = hourOfDay - 12;
                    }
                    else {
                        hour = hourOfDay;
                    }
                    if(hour == 0) {
                        hour = 12;
                    }
                    isAm = calendar.get(Calendar.AM_PM) == Calendar.AM;
                    time = new Time((hour + 1), minute, isAm, hourOfDay + 1);
                }
                if(datePickerFragment != null) {
                    //get date from date picker
                    date = datePickerFragment.getDate();
                }
                else {
                    Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    date = new Date(month, day, year);
                }

                //create item with a time and date for reminder
                item = new Item(task, priority, time, date);
            }
            else {
                //create item without reminder
                item = new Item(task, priority);
            }
            if(replaceInsteadOfAdd) {
                //check for old reminder
                if(Globals.list.getItem(position).hasReminder()) {
                    //cancel old reminder
                    AlarmManager alarmManager = (AlarmManager) getApplicationContext()
                            .getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);
                    alarmManager.cancel(PendingIntent.getBroadcast(getApplicationContext(),
                            Globals.list.getItem(position).getIdNumber(), intent,
                            PendingIntent.FLAG_UPDATE_CURRENT));
                }
                //if there is a new reminder, set it
                if(reminderCheckBox.isChecked()) {
                    //start new reminder
                    //set time for alarm
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, item.getTime().getHourOfDay());
                    calendar.set(Calendar.MINUTE, item.getTime().getMinute());
                    calendar.set(Calendar.MONTH, item.getDate().getMonth());
                    calendar.set(Calendar.DAY_OF_MONTH, item.getDate().getDay());
                    calendar.set(Calendar.YEAR, item.getDate().getYear());
                    //set alarm
                    AlarmManager alarmManager = (AlarmManager) getApplicationContext()
                            .getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);

                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            PendingIntent.getBroadcast(getApplicationContext(),
                                    item.getIdNumber(), intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT));
                }
                //replace old item with new item
                Globals.list.replaceItem(position, item);
                //save list
                saveList();
            }
            else {
                //set reminder if there is one
                if(item.hasReminder()) {
                    //set reminder
                    //set time for alarm
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, item.getTime().getHourOfDay());
                    calendar.set(Calendar.MINUTE, item.getTime().getMinute());
                    calendar.set(Calendar.MONTH, item.getDate().getMonth());
                    calendar.set(Calendar.DAY_OF_MONTH, item.getDate().getDay());
                    calendar.set(Calendar.YEAR, item.getDate().getYear());
                    //set alarm
                    AlarmManager alarmManager = (AlarmManager) getApplicationContext()
                            .getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);
                    intent.putExtra("alarm type", "create notification");
                    intent.putExtra("task", item.getTask());
                    intent.putExtra("priority", item.getPriority());
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            PendingIntent.getBroadcast(getApplicationContext(),
                                    item.getIdNumber(), intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT));
                }
                //add to list
                Globals.list.addItem(item);
                //save list
                saveList();
            }
            if(dailyCheckBox.isChecked()) {
                //add to presets
                //set item priority to daily to be in presets list
                item.setPriority(Item.PRIORITY_DAILY);
                Globals.presets.addItem(item);
                //save presets
                savePresets();
            }
            setResult(RESULT_OK, returnToMain);
        }
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

    public void saveList() {
        //opens the list file and writes the list to it without append
        try{ObjectOutputStream listOutput =
                new ObjectOutputStream(openFileOutput(Globals.listFileName, Context.MODE_PRIVATE));
            listOutput.writeObject(Globals.list);
            listOutput.close();
        }
        catch(Exception e){e.printStackTrace();}
    }

    public void savePresets() {
        //opens the presets file and writes the presets to it without append
        try{ObjectOutputStream presetOutput =
                new ObjectOutputStream(openFileOutput(Globals.presetFileName, Context.MODE_PRIVATE));
            presetOutput.writeObject(Globals.presets);
            presetOutput.close();
        }
        catch(Exception e){e.printStackTrace();}
    }

    public void setAsDailyTask(View view) {
        reminderCheckBox.setChecked(false);
        timeButton.setTextColor(Color.GRAY);
        dayButton.setTextColor(Color.GRAY);
        timeButton.setClickable(false);
        dayButton.setClickable(false);
        radioGroup.clearCheck();
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
