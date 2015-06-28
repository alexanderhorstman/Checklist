package com.example.alexh.checklist;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class MyAdapter extends ArrayAdapter<String> {
    private Context context; //gets the context from the calling activity
    private ItemArray list; //the list that the adapter is showing

    //constructor get the context, the string array from the global list, and the global list
    public MyAdapter(Context context, String[] values, ItemArray list) {
        super(context, R.layout.row_layout_2, values);
        this.context = context;
        this.list = list;
    }

    //fired when the app tries to view an item from the String array list
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //this view has information about the layout if it was used previously
        //it is null if it was not used previously
        View view = convertView;
        //viewHolder holds the variables for all of the views
        Holder viewHolder = new Holder();
        //if the converted view has not been used before
        if(convertView == null) {
            //get an inflater to inflate the layout into the view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_layout_2, null);
            //set the viewHolder variables to the components of the layout
            viewHolder.textView1 = (TextView) view.findViewById(R.id.textView1);
            viewHolder.imageView1 = (ImageView) view.findViewById(R.id.checkboxImageView);
            viewHolder.reminderText = (TextView) view.findViewById(R.id.reminder_text);
            //set the tag of the view to the viewHolder
            //stores the information of the layout for recycled use later
            view.setTag(viewHolder);
        }
        //converted view has information about the layout from previous use
        else {
            viewHolder = (Holder) view.getTag();
        }
        //getItem(int) is a method from the extended ArrayAdapter to get the String at the position
        String selectedItem = getItem(position);
        //sets the text of the item from getItem(int)
        viewHolder.textView1.setText(selectedItem);
        //if the item at the "position" has a reminder add a reminder time text and make the text visible
        if(list.getItem(position).hasReminder()) {
            String reminderTime = list.getItem(position).getTime().toString() + " "
                    + list.getItem(position).getDate().toString();
            viewHolder.reminderText.setText("Reminder: " + reminderTime);
            viewHolder.reminderText.setVisibility(View.VISIBLE);
        }
        //make the reminder text invisible if that item has no reminder
        else {
            viewHolder.reminderText.setVisibility(View.GONE);
        }
        //sets the image to the checked box if the item is checked
        if(list.checkSelectedAt(position)) {
            viewHolder.imageView1.setImageResource(R.drawable.check_box_selected_new);
        }
        //sets the image to the unchecked box if the item is not checked
        else {
            viewHolder.imageView1.setImageResource(R.drawable.check_box_unselected_new);
        }
        //gets the priority of the item and sets the color accordingly
        switch(list.getItem(position).getPriority()){
            case Item.PRIORITY_NORMAL:
                viewHolder.textView1.setTextColor(Color.BLACK);
                break;
            case Item.PRIORITY_MEDIUM:
                viewHolder.textView1.setTextColor(Color.rgb(255, 141, 0));
                break;
            case Item.PRIORITY_HIGH:
                viewHolder.textView1.setTextColor(Color.RED);
                break;
            case Item.PRIORITY_DAILY:
                viewHolder.textView1.setTextColor(Color.BLUE);
                break;
        }
        //returns the view that shows on the list view
        return view;
    }

    class Holder {
        protected ImageView imageView1; //the image of the checkbox
        protected TextView reminderText; //the reminder time of the item if it has one
        protected TextView textView1; //the description of the item
    }
}

