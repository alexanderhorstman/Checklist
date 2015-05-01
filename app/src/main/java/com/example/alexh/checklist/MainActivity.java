package com.example.alexh.checklist;

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


public class MainActivity extends ActionBarActivity {
    private StringArray list = new StringArray();
    private StringArray presets = new StringArray();
    private ListAdapter theAdapter;
    private ListView theListView;
    private String presetFileName = "presets.txt";
    private String listFileName = "list.txt";
    private ObjectInputStream presetInput = null, listInput = null;
    private ObjectOutputStream presetOutput = null, listOutput = null;

    /*
     * Overridden Methods
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if result code is ok then save the new presets, if not do nothing
        if (resultCode == Globals.RESULT_OK_SET_DAILY) {
            presets = (StringArray) data.getSerializableExtra("new presets");
            //save the new presets list
            savePresets();
        }
        else if (resultCode == Globals.RESULT_OK_EDIT_TASK) {
            list.addItem((Item) data.getSerializableExtra("new item"));
            //save the list with the new item
            saveList();
        }
        else if (resultCode == Globals.RESULT_OK_EDIT_TASK_WITH_ADD_PRESETS) {
            list.addItem((Item) data.getSerializableExtra("new item"));
            //change the priority so that the item appears as a daily task
            Item newItem = new Item(data.getSerializableExtra("new item").toString(), Globals.PRIORITY_DAILY);
            presets.addItem(newItem);
            //save the list with the new item
            saveList();
            //save the presets with the new item
            savePresets();
        }
        else if(resultCode == Globals.RESULT_REPLACE_IN_LIST){
            list.changeItemText(data.getIntExtra("item position", 0), (Item)data.getSerializableExtra("new item"));
            //re-order list with new priority
            reOrderList(data.getIntExtra("item position", 0), ((Item)data.getSerializableExtra("new item")).getPriority());
            //save the list with the new item
            saveList();
        }
        else if(resultCode == Globals.RESULT_REPLACE_IN_LIST_WITH_ADD_PRESETS){
            list.changeItemText(data.getIntExtra("item position", 0), (Item)data.getSerializableExtra("new item"));
            //re-order list with new priority
            reOrderList(data.getIntExtra("item position", 0), ((Item)data.getSerializableExtra("new item")).getPriority());
            presets.addItem((Item) data.getSerializableExtra("new item"));
            //save the list with the new item
            saveList();
            //save the presets with the new item
            savePresets();
        }
        updateAdapter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            //creates object input stream using preset file name
            listInput = new ObjectInputStream(openFileInput(listFileName));
            //reads list in from file
            list = (StringArray) listInput.readObject();
            //closes file
            listInput.close();
        }
        //if the file was not found
        catch(FileNotFoundException e){
            //make a new file object using preset file name
            File listFile = new File(this.getFilesDir().getAbsolutePath(), listFileName);
            //try to create a new file, quit if system cannot create new file
            try{listFile.createNewFile();}
            catch(Exception f){Toast.makeText(this, "The list file could not be created. Shutting down.",
                    Toast.LENGTH_LONG).show();
                try{wait(2000);}catch(Exception a){}
                finish();
            }
        }
        catch(Exception f){}
        //sets the global list to the list from file
        Globals.list = list;

        try {
            //creates object input stream using preset file name
            presetInput = new ObjectInputStream(openFileInput(presetFileName));
            //reads list in from file
            presets = (StringArray) presetInput.readObject();
            //closes file
            presetInput.close();
        }
        //if the file was not found
        catch(FileNotFoundException e){
            //make a new file object using preset file name
            File presetsFile = new File(this.getFilesDir().getAbsolutePath(), presetFileName);
            //try to create a new file, quit if system cannot create new file
            try{presetsFile.createNewFile();}
            catch(Exception f){Toast.makeText(this, "The presets file could not be created. Shutting down.",
                    Toast.LENGTH_LONG).show();
                try{wait(2000);}catch(Exception a){}
                finish();
            }
        }
        catch(Exception f){}

        //sets the main content
        setContentView(R.layout.activity_main);

        //instantiates the adapter with the list
        theAdapter = new MyAdapter(this, list.getStringArray(), list);
        //gets the list view and sets teh adapter to it
        theListView = (ListView) findViewById(R.id.theListView);
        theListView.setAdapter(theAdapter);
        //sets the click behavior for the items in the list
        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //finds the image view
                ImageView image = (ImageView) findViewById(R.id.imageView1);
                //changes the selected state of the item that was clicked on
                list.getItem(String.valueOf(parent.getItemAtPosition(position))).flipSelected();
                //changes the image associated with the item that was clicked on
                if(list.getItem(String.valueOf(parent.getItemAtPosition(position))).isSelected())
                {
                    //selected image
                    image.setImageResource(R.drawable.check_box_selected);
                }
                else
                {
                    //unselected image
                    image.setImageResource(R.drawable.check_box_unselected);
                }
                //updates the adapter to update the images for the clicked item
                ((BaseAdapter)theAdapter).notifyDataSetChanged();
            }
        });
        theListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //changes activity to edit task
                Intent editTask = new Intent(MainActivity.this, EditTask.class);
                final int result = 1;
                //sets extra info to be received in the edit task activity
                editTask.putExtra("task description", list.getItem(position).getTask());
                //
                editTask.putExtra("item position", position);
                //
                editTask.putExtra("priority level", list.getItem(position).getPriority());
                //starts the edit task activity
                startActivityForResult(editTask, result);
                return false;
            }
        });
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
            list.selectAll();
            ((BaseAdapter)theAdapter).notifyDataSetChanged();
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
        if(presets.sizeOf() != 0) {
            //adds the presets items if they are not already in the list
            for (int i = 0; i < presets.sizeOf(); i++) {
                //searches the list for the preset task
                if (!(list.contains(presets.getItem(i)))) {
                    //adds item if it was not already present
                    list.addItem(presets.getItem(i));
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
        int[] toDelete = new int[list.sizeOf()];
        //instantiates the toDelete array with false values
        for(int index = 0; index < list.sizeOf(); index++) {
            toDelete[index] = -1;
        }
        //changes the toDelete values if the item at the index is selected
        for(int index = 0; index < list.sizeOf(); index++) {
            //checks selected state
            if(list.checkSelectedAt(index)) {
                //changes toDelete value at the index
                //does not have to be "index", just has to not be -1
                toDelete[index] = index;
            }
        }
        //deletes the items that were selected
        for(int index = toDelete.length - 1; index >= 0; index--) {
            //if toDelete value was changed
            if(toDelete[index] != -1) {
                if(presets.contains(list.getItem(index))) {
                    list.getItem(index).flipSelected();
                }
                //deletes item
                list.deleteItem(index);
            }
        }
        //updates the adapter and refreshes the list view
        updateAdapter();
        //saves the list to the list file
        saveList();
    }

    private void reOrderList(int position, int newPriority){
        Item tempItem = new Item(list.getItem(position).getTask(), newPriority);
        list.deleteItem(position);
        list.addItem(tempItem);
    }

    public void setPreset() {
        //changes activity to get presets
        Intent getPresetList = new Intent(this, SetDaily.class);
        final int result = 1;
        //sets extra info to be received in the get presets activity
        getPresetList.putExtra("previous presets", presets);
        //starts the get presets activity
        startActivityForResult(getPresetList, result);
    }

    public void saveList() {
        //opens the list file and writes the list to it without append
        try{listOutput = new ObjectOutputStream(openFileOutput(listFileName, Context.MODE_PRIVATE));
            listOutput.writeObject(list);
            listOutput.close();
        }
        catch(Exception e){}
    }

    public void savePresets() {
        //opens the presets file and writes the presets to it without append
        try{presetOutput = new ObjectOutputStream(openFileOutput(presetFileName, Context.MODE_PRIVATE));
            presetOutput.writeObject(presets);
            presetOutput.close();
        }
        catch(Exception e){}
    }

    private void updateAdapter() {
        //updates the adapter with a new list
        theAdapter = new MyAdapter(getApplicationContext(),list.getStringArray(),list);
        //sets the updated adapter to the list view
        theListView.setAdapter(theAdapter);
        //refreshes the list view
        theListView.refreshDrawableState();
    }
}
