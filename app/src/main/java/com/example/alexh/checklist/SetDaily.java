package com.example.alexh.checklist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ObjectOutputStream;

public class SetDaily extends Activity {
    private int itemPriority = Item.PRIORITY_DAILY; //default priority for items in the daily presets list
    private ListView listView; //list view that shows all of the items in the presets list
    private MyAdapter adapter; //the adapter that converts the list into views for the listView

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
        super.onCreate(savedInstanceState);
        //sets this as the active view
        setContentView(R.layout.set_daily);
        //instantiates the adapter with the list
        adapter = new MyAdapter(this, Globals.presets.getStringArray(), Globals.presets);
        //gets the list view and sets the adapter to it
        listView = (ListView) findViewById(R.id.theListView2);
        listView.setAdapter(adapter);
        //sets the click behavior for the items in the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //finds the image view
                ImageView image = (ImageView) findViewById(R.id.checkboxImageView);
                //changes the selected state of the item that was clicked on
                Globals.presets.getItem(String.valueOf(parent.getItemAtPosition(position)))
                        .flipSelected();
                //changes the image associated with the item that was clicked on
                if (Globals.presets.getItem(String.valueOf(parent.getItemAtPosition(position)))
                        .isSelected()) {
                    //selected image
                    image.setImageResource(R.drawable.check_box_selected_new);
                } else {
                    //unselected image
                    image.setImageResource(R.drawable.check_box_unselected_new);
                }
                //updates the adapter to update the images for the clicked item
                adapter.notifyDataSetChanged();
                //updateAdapter();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //instantiates an alert for the user to add a new task
                AlertDialog.Builder alert = new AlertDialog.Builder(SetDaily.this);
                //sets the title of the alert
                alert.setTitle("Edit this task");
                //instantiates an edit text to add to the alert
                final EditText input = new EditText(SetDaily.this);
                final int pos = position;
                input.setText(Globals.presets.getItem(position).getTask());
                //sets this edit text to be the content of the alert
                alert.setView(input);
                //set the click behavior for the positive button click
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //checks to make sure the text is not blank
                        if (!(input.getText().toString().equals(""))) {
                            //add the task to the list if it is not already in the list
                            if (!Globals.presets.contains(new Item(input.getText().toString(),
                                    itemPriority))) {
                                Item item = new Item(input.getText().toString(),
                                        Item.PRIORITY_DAILY);
                                Globals.presets.replaceItem(pos, item);
                            } else {
                                Toast.makeText(SetDaily.this, "The item " + input.getText()
                                                + " is already in the list",
                                        Toast.LENGTH_SHORT).show();
                            }
                            //updates the adapter to update the images for the clicked item
                            updateAdapter();
                        }
                    }
                });
                //set the click behavior for the negative button click
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing here
                    }
                });
                //show the alert
                alert.show();
                return false;
            }
        });
    }

    //adds an item to the list
    public void addItem(View view) {
        //instantiates an alert for the user to add a new task
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //sets the title of the alert
        alert.setTitle("Add a task to the list");
        //instantiates an edit text to add to the alert
        final EditText input = new EditText(this);
        //sets this edit text to be the content of the alert
        alert.setView(input);
        //set the click behavior for the positive button click
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //checks to make sure the text is not blank
                if (!(input.getText().toString().equals(""))) {
                    //add the task to the list
                    Globals.presets.addItem(new Item("" + input.getText(), itemPriority));
                    //update the adapter and refresh the list view
                    updateAdapter();
                }
            }
        });
        //set the click behavior for the negative button click
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing here
            }
        });
        //show the alert
        alert.show();
    }

    //deletes the items in the list that are selected
    public void deleteSelected(View view) {
        //holds the items to be deleted, will delete everything at once
        int[] toDelete = new int[Globals.presets.sizeOf()];
        //instantiates the toDelete array with false values
        for(int index = 0; index < Globals.presets.sizeOf(); index++) {
            toDelete[index] = -1;
        }
        //changes the toDelete values if the item at the index is selected
        for(int index = 0; index < Globals.presets.sizeOf(); index++) {
            //checks selected state
            if(Globals.presets.checkSelectedAt(index)) {
                //changes toDelete value at the index
                //does not have to be "index", just has to not be -1
                toDelete[index] = index;
            }
        }
        //deletes the items that were selected
        for(int index = toDelete.length - 1; index >= 0; index--) {
            //if toDelete value was changed
            if(toDelete[index] != -1) {
                //deletes item
                Globals.presets.deleteItem(index);
            }
        }
        //updates the adapter and refreshes the list view
        updateAdapter();
    }

    //saves the presets and returns to the main activity
    public void savePresets(View view) {
        //save the presets list
        savePresetsToFile();
        //create intent to send back
        Intent returnToMain = new Intent();
        //set result code to ok
        setResult(RESULT_OK, returnToMain);
        //leave this activity
        finish();
    }

    //tries to save the presets to its file
    public void savePresetsToFile() {
        //save file name
        String presetFileName = "presets.txt";
        //opens the presets file and writes the presets to it without append
        try {
            ObjectOutputStream presetOutput =
                    new ObjectOutputStream(openFileOutput(presetFileName, Context.MODE_PRIVATE));
            presetOutput.writeObject(Globals.presets);
            presetOutput.close();
        }
        catch(Exception e){}
    }

    //updates the adapter with the current version of the presets list
    private void updateAdapter() {
        //updates the adapter with a new list
        adapter = new MyAdapter(getApplicationContext(),Globals.presets.getStringArray(),
                Globals.presets);
        //sets the updated adapter to the list view
        listView.setAdapter(adapter);
        //refreshes the list view
        listView.refreshDrawableState();
    }
}
