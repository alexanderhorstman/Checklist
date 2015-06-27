package com.example.alexh.checklist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;


public class MainActivity extends ActionBarActivity {
    private ListAdapter listAdapter;
    private ListView listView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateAdapter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Globals.list = new StringArray();
        Globals.presets = new StringArray();
        readSavedList();
        readPresets();
        //sets the main content
        setContentView(R.layout.activity_main);
        //instantiates the adapter with the list
        listAdapter = new MyAdapter(this, Globals.list.getStringArray(), Globals.list);
        //gets the list view and sets teh adapter to it
        listView = (ListView) findViewById(R.id.mainListView);
        listView.setAdapter(listAdapter);
        //sets the click behavior for the items in the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //finds the image view
                ImageView image = (ImageView) findViewById(R.id.checkboxImageView);
                //changes the selected state of the item that was clicked on
                Globals.list.getItem(String.valueOf(parent.getItemAtPosition(position))).flipSelected();
                //changes the image associated with the item that was clicked on
                if (Globals.list.getItem(String.valueOf(parent.getItemAtPosition(position))).isSelected()) {
                    //selected image
                    image.setImageResource(R.drawable.check_box_selected);
                } else {
                    //unselected image
                    image.setImageResource(R.drawable.check_box_unselected);
                }
                //updates the adapter to update the images for the clicked item
                ((BaseAdapter) listAdapter).notifyDataSetChanged();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //changes activity to edit task
                Intent editTask = new Intent(MainActivity.this, EditTask.class);
                final int result = 1;
                //sets extra info to be received in the edit task activity
                editTask.putExtra("task description", Globals.list.getItem(position).getTask());
                //puts item position into extras
                editTask.putExtra("item position", position);
                //puts priority level into extras
                editTask.putExtra("priority level", Globals.list.getItem(position).getPriority());
                //starts the edit task activity
                startActivityForResult(editTask, result);
                return false;
            }
        });
        //set daily tasks to be added to list every day at midnight
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        AlarmManager alarmManager =
                (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlertReceiver.class);
        alarmIntent.putExtra("alarm type", "add daily");
        //set alarm to trigger at midnight every day
        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                PendingIntent.getBroadcast(this, 0, alarmIntent, 0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //selects all of the items in the list
        if (id == R.id.select_all) {
            Globals.list.selectAll();
            ((BaseAdapter) listAdapter).notifyDataSetChanged();
            return true;
        }
        //sets the presets that can be added to the list
        else if(id == R.id.set_preset) {
            setPreset();
            return true;
        }
        //adds the preset tasks to the list
        else if(id == R.id.add_preset) {
            addPreset();
            return true;
        }
        //closes the app
        else if(id == R.id.exit) {

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addItem(View view) {
        //changes activity to edit task
        Intent editTask = new Intent(this, EditTask.class);
        final int result = 1;
        //sets extra info to be received in the edit task activity
        editTask.putExtra("task description", "");
        //starts the edit task activity
        startActivityForResult(editTask, result);
    }

    private void addPreset() {
        if(Globals.presets.sizeOf() != 0) {
            //adds the presets items if they are not already in the list
            for (int i = 0; i < Globals.presets.sizeOf(); i++) {
                //searches the list for the preset task
                if (!(Globals.list.contains(Globals.presets.getItem(i)))) {
                    //adds item if it was not already present
                    Globals.list.addItem(Globals.presets.getItem(i));
                }
            }
        }
        //updates the adapter and refreshes the list view
        updateAdapter();
        //saves the list to the list file
        saveList();
    }

    public void deleteSelected(View view) {
        //holds the items to be deleted, will delete everything at once
        int[] toDelete = new int[Globals.list.sizeOf()];
        //instantiates the toDelete array with false values
        for(int index = 0; index < Globals.list.sizeOf(); index++) {
            toDelete[index] = -1;
        }
        //changes the toDelete values if the item at the index is selected
        for(int index = 0; index < Globals.list.sizeOf(); index++) {
            //checks selected state
            if(Globals.list.checkSelectedAt(index)) {
                //changes toDelete value at the index
                //does not have to be "index", just has to not be -1
                toDelete[index] = index;
            }
        }
        //deletes the items that were selected
        for(int index = toDelete.length - 1; index >= 0; index--) {
            //if toDelete value was changed
            if(toDelete[index] != -1) {
                if(Globals.presets.contains(Globals.list.getItem(index))) {
                    Globals.list.getItem(index).flipSelected();
                }
                //cancel reminder if the item has one
                if(Globals.list.getItem(index).hasReminder()) {
                    AlarmManager alarmManager =
                            (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getBaseContext(), AlertReceiver.class);
                    alarmManager.cancel(PendingIntent.getBroadcast(getBaseContext(),
                            Globals.list.getItem(index).getIdNumber(), intent,
                            PendingIntent.FLAG_UPDATE_CURRENT));
                }
                //deletes item
                Globals.list.deleteItem(index);
            }
        }
        //updates the adapter and refreshes the list view
        updateAdapter();
        //saves the list to the list file
        saveList();
    }

    private void readPresets() {
        try {
            //creates object input stream using preset file name
            ObjectInputStream presetInput = new ObjectInputStream(openFileInput(Globals.presetFileName));
            //reads list in from file
            Globals.presets = (StringArray) presetInput.readObject();
            //closes file
            presetInput.close();
        }
        //if the file was not found
        catch(FileNotFoundException e){
            //make a new file object using preset file name
            File presetsFile = new File(this.getFilesDir().getAbsolutePath(), Globals.presetFileName);
            //try to create a new file, quit if system cannot create new file
            try{presetsFile.createNewFile();}
            catch(Exception f){Toast.makeText(this, "The presets file could not be created. Shutting down.",
                    Toast.LENGTH_LONG).show();
                try{wait(2000);}catch(Exception a){}
                finish();
            }
        }
        catch(Exception f){}
    }

    private void readSavedList() {
        try {
            //creates object input stream using preset file name
            ObjectInputStream listInput = new ObjectInputStream(openFileInput(Globals.listFileName));
            //reads list in from file
            Globals.list = (StringArray) listInput.readObject();
            //closes file
            listInput.close();
        }
        //if the file was not found
        catch(FileNotFoundException e){
            //make a new file object using preset file name
            File listFile = new File(this.getFilesDir().getAbsolutePath(), Globals.listFileName);
            //try to create a new file, quit if system cannot create new file
            try{listFile.createNewFile();}
            catch(Exception f){Toast.makeText(this, "The list file could not be created. Shutting down.",
                    Toast.LENGTH_LONG).show();
                try {
                    wait(2000);
                }
                catch(Exception a){}
                finish();
            }
        }
        catch(Exception f){}
    }

    public void setPreset() {
        //changes activity to get presets
        Intent getPresetList = new Intent(this, SetDaily.class);
        final int result = 1;
        //starts the get presets activity
        startActivityForResult(getPresetList, result);
    }

    public void saveList() {
        //opens the list file and writes the list to it without append
        try{ObjectOutputStream listOutput = new ObjectOutputStream(openFileOutput(Globals.listFileName, Context.MODE_PRIVATE));
            listOutput.writeObject(Globals.list);
            listOutput.close();
        }
        catch(Exception e){}
    }

    private void updateAdapter() {
        //updates the adapter with a new list
        listAdapter = new MyAdapter(getApplicationContext(),Globals.list.getStringArray(), Globals.list);
        //sets the updated adapter to the list view
        listView.setAdapter(listAdapter);
        //refreshes the list view
        listView.refreshDrawableState();
    }
}
