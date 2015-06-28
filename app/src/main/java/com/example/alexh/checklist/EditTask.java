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
    private boolean replaceInsteadOfAdd = false; //determines whether an item is being edited or added for the first time
    private int position; //the position of the item that is being edited; default value is 0

    private Button timeButton; //button used to choose time of reminder
    private Button dayButton; //button used to choose date of reminder
    private CheckBox dailyCheckBox; //checkbox used to determine if the item is a daily task or not
    private CheckBox reminderCheckBox; //checkbox used to determine if the item has a reminder
    private DatePickerFragment datePickerFragment; //fragment used to create the date picker dialog
    private EditText editText; //holds the description of the item
    private RadioGroup radioGroup; //the selected radio button determines the priority of the item
    private TimePickerFragment timePickerFragment; //fragment used to create the time picker dialog

    //fired when the back button is pressed
    @Override
    public void onBackPressed() {
        //set result code to canceled
        setResult(RESULT_CANCELED);
        //leave this activity
        finish();
    }

    //fired when the activity starts
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initial setup for activity
        ////////////////////////////

        super.onCreate(savedInstanceState);
        //sets the layout for the activity
        setContentView(R.layout.edit_task);
        //gets the intent from the previous activity
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

        //initializing all layout components with the correct settings based on the new or previous item
        ////////////////////////////////////////////////////////////////////////////////////////////////

        //if the item needs to be edited this will run
        if(replaceInsteadOfAdd) {
            //set the radio button to the previous priority setting for this item
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
            //item has no reminder
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
        //if the item is being added for the first time this will run
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

    //adds the item to list and returns to the main activity
    //fired when the Confirm button is pressed
    public void addItemToList(View view){
        //description of the item
        String task = editText.getText().toString();
        //priority of the item
        int priority = Item.PRIORITY_NORMAL;
        //id of the selected radio button; provides priority level
        int id = radioGroup.getCheckedRadioButtonId();
        //create intent to send back
        Intent returnToMain = new Intent();
        //time of reminder; if there is one
        Time time;
        //date of reminder; if there is one
        Date date;
        //checks for black description of task;
        if(task.equals("")){
            //set result code to canceled
            setResult(RESULT_CANCELED, returnToMain);
        }
        //if the description is not blank
        else {
            //sets daily priority if the daily checkbox is set
            if(dailyCheckBox.isChecked()) {
                priority = Item.PRIORITY_DAILY;
            }
            //sets the item priority based on the radio button selection
            //even if the item is a daily task this priority will override the daily priority the
            //first time it is added to the list
            if(id == R.id.radio_normal) {
                priority = Item.PRIORITY_NORMAL;
            }
            else if(id == R.id.radio_rush) {
                priority = Item.PRIORITY_MEDIUM;
            }
            else if(id == R.id.radio_urgent) {
                priority = Item.PRIORITY_HIGH;
            }
            //if no radio button is selected
            //will only get this if the item was a daily task
            else if(id == -1) {
                priority = Item.PRIORITY_DAILY;
            }
            //create item to add to list
            Item item;
            //if the item has a reminder
            if(reminderCheckBox.isChecked()) {
                //if the time picker button is clicked timePickerFragment will not be null
                if(timePickerFragment != null) {
                    //get time from time picker
                    time = timePickerFragment.getTime();
                }
                //if the time picker button is never clicked, then the default time is used
                //default time is the next hour and 0 minutes
                //ex. Current time = 5:23pm; Default time = 6:00pm
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
                    //sets the hour to one hour ahead of the current time
                    time = new Time((hour + 1), minute, isAm, hourOfDay + 1);
                }
                //if the date picker button is clicked the datePickerFragment will not be null;
                if(datePickerFragment != null) {
                    //get date from date picker
                    date = datePickerFragment.getDate();
                }
                //if the date picker button was never clicked, then today's date is used
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
            //if item does not have a reminder
            else {
                //create item without reminder
                item = new Item(task, priority);
            }
            //if item is being edited
            if(replaceInsteadOfAdd) {
                //check for old reminder
                if(Globals.list.getItem(position).hasReminder()) {
                    //cancel old reminder if it has one
                    AlarmManager alarmManager = (AlarmManager) getApplicationContext()
                            .getSystemService(Context.ALARM_SERVICE);
                    //intent fires the AlertReceiver class
                    Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);
                    //uses old item's id number to cancel the alarm that was set with that id number
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
                    //intent fires the AlertReceiver class
                    Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);
                    //sets alarm using new item's id number
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
            //item is being added for the first time
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
            //adds item to daily presets if daily check box is checked
            if(dailyCheckBox.isChecked()) {
                //add to presets
                //set item priority to daily to be in presets list
                item.setPriority(Item.PRIORITY_DAILY);
                Globals.presets.addItem(item);
                //save presets
                savePresets();
            }
            //set results as ok
            setResult(RESULT_OK, returnToMain);
        }
        //leave this activity
        finish();
    }

    //fires when date picker button is clicked
    public void chooseReminderDay(View view) {
        //creates a new date picker dialog
        datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    //fires when time picker button is clicked
    public void chooseReminderTime(View view) {
        //creates a new time picker dialog
        timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(getFragmentManager(), "timePicker");
    }

    //fires when the cancel button is clicked
    public  void returnToMainActivityOnCancel(View view){
        //set result code to canceled
        setResult(RESULT_CANCELED);
        //leave this activity
        finish();
    }

    //used to save the list to file
    public void saveList() {
        //opens the list file and writes the list to it without append
        try{ObjectOutputStream listOutput =
                new ObjectOutputStream(openFileOutput(Globals.listFileName, Context.MODE_PRIVATE));
            listOutput.writeObject(Globals.list);
            listOutput.close();
        }
        catch(Exception e){e.printStackTrace();}
    }

    //used to save the daily presets to file
    public void savePresets() {
        //opens the presets file and writes the presets to it without append
        try{ObjectOutputStream presetOutput =
                new ObjectOutputStream(openFileOutput(Globals.presetFileName, Context.MODE_PRIVATE));
            presetOutput.writeObject(Globals.presets);
            presetOutput.close();
        }
        catch(Exception e){e.printStackTrace();}
    }

    //fired when the daily check box is clicked
    public void setAsDailyTask(View view) {
        //does not allow a daily item to have a reminder
        reminderCheckBox.setChecked(false);
        //grays out the two reminder buttons to show that they are no longer clickable
        timeButton.setTextColor(Color.GRAY);
        dayButton.setTextColor(Color.GRAY);
        //makes the two reminder buttons unclickable
        timeButton.setClickable(false);
        dayButton.setClickable(false);
        //clears the radio buttons to give daily priority to item
        radioGroup.clearCheck();
    }

    //fired when the reminder check box is clicked
    //will toggle back and forth between having and not having a reminder
    public void toggleReminderStatus(View view) {
        //does not allow an item with a reminder to also be a daily task
        dailyCheckBox.setChecked(false);
        //if the two reminder buttons are grayed out and unavailable
        if(timeButton.getCurrentTextColor() == Color.GRAY) {
            //sets their colors to black
            timeButton.setTextColor(Color.BLACK);
            dayButton.setTextColor(Color.BLACK);
            //makes them clickable
            timeButton.setClickable(true);
            dayButton.setClickable(true);
        }
        //if the two reminder buttons are black and available
        else if(timeButton.getCurrentTextColor() == Color.BLACK) {
            //sets their colors to gray
            timeButton.setTextColor(Color.GRAY);
            dayButton.setTextColor(Color.GRAY);
            //makes them unclickable
            timeButton.setClickable(false);
            dayButton.setClickable(false);
        }
    }
}
