package com.example.alexh.checklist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

public class EditTask extends Activity{
    EditText editText;
    RadioGroup radioGroup;
    CheckBox checkBox;
    boolean replaceInsteadOfAdd = false;
    String textHolder;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_task);
        Intent previousActivity = getIntent();
        //will receive a blank string from the add item method
        //will receive a string from the edit item method(long click on item)
        String editTextText = previousActivity.getStringExtra("task description");
        //
        position = previousActivity.getIntExtra("item position", 0);
        //
        if(!editTextText.equals(""))
        {
            textHolder = editTextText;
            replaceInsteadOfAdd = true;
        }
        //get all of the views from this activity
        editText = (EditText) findViewById(R.id.taskDescriptionEditText);
        radioGroup = (RadioGroup) findViewById(R.id.priorityRadioGroup);
        switch (previousActivity.getIntExtra("priority level", Globals.PRIORITY_NORMAL))
        {
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
        checkBox = (CheckBox) findViewById(R.id.addToPresetsCheckBox);
        //set the edit text's text from the previous activity
        editText.setText(editTextText);


    }

    @Override
    public void onBackPressed()
    {
        //set result code to canceled
        setResult(RESULT_CANCELED);
        //leave this activity
        finish();
    }

    public void addItemToList(View view){
        String task = editText.getText().toString();
        int priority = Globals.PRIORITY_NORMAL;
        int id = radioGroup.getCheckedRadioButtonId();
        if(task.equals("")){
            //set result code to canceled
            setResult(RESULT_CANCELED);
            //leave this activity
            finish();
        }
        if(checkBox.isChecked())
        {
            priority = Globals.PRIORITY_DAILY;
        }
        if(id == R.id.radio_normal)
        {
            priority = Globals.PRIORITY_NORMAL;
        }
        else if(id == R.id.radio_rush)
        {
            priority = Globals.PRIORITY_RUSH;
        }
        else if(id == R.id.radio_urgent)
        {
            priority = Globals.PRIORITY_URGENT;
        }
        //create item to send back
        Item item = new Item(task, priority);
        //create intent to send back
        Intent sendItemBack = new Intent();
        if(checkBox.isChecked() && !replaceInsteadOfAdd)
        {
            //set result code to ok and add to presets
            setResult(Globals.RESULT_OK_EDIT_TASK_WITH_ADD_PRESETS, sendItemBack);
        }
        else if(replaceInsteadOfAdd && !checkBox.isChecked())
        {
            setResult(Globals.RESULT_REPLACE_IN_LIST, sendItemBack);
            sendItemBack.putExtra("old item", textHolder);
        }
        else if(replaceInsteadOfAdd && checkBox.isChecked())
        {
            setResult(Globals.RESULT_REPLACE_IN_LIST_WITH_ADD_PRESETS, sendItemBack);
            sendItemBack.putExtra("old item", textHolder);
        }
        else
        {
            //set result code to ok
            setResult(Globals.RESULT_OK_EDIT_TASK, sendItemBack);
        }
        //put item in the intent
        sendItemBack.putExtra("new item", item);
        //put the item position in the intent
        sendItemBack.putExtra("item position", position);
        //leave this activity
        finish();
    }

    public  void returnToMainActivity(View view){
        //set result code to canceled
        setResult(RESULT_CANCELED);
        //leave this activity
        finish();
    }
}
