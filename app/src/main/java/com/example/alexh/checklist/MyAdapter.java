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
    private Context context;
    private StringArray list;

    public MyAdapter(Context context, String[] values, StringArray list) {
        super(context, R.layout.row_layout_2, values);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Holder viewHolder = new Holder();
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_layout_2, null);
            viewHolder.textView1 = (TextView) view.findViewById(R.id.textView1);
            viewHolder.imageView1 = (ImageView) view.findViewById(R.id.checkboxImageView);
            viewHolder.reminderText = (TextView) view.findViewById(R.id.reminder_text);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (Holder) view.getTag();
        }
        String selectedItem = getItem(position);
        viewHolder.textView1.setText(selectedItem);
        if(list.getItem(position).hasReminder()) {
            String reminderTime = list.getItem(position).getTime().toString() + " "
                    + list.getItem(position).getDate().toString();
            viewHolder.reminderText.setText("Reminder: " + reminderTime);
            viewHolder.reminderText.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.reminderText.setVisibility(View.GONE);
        }

        if(list.checkSelectedAt(position)) {
            viewHolder.imageView1.setImageResource(R.drawable.check_box_selected_new);
        }
        else {
            viewHolder.imageView1.setImageResource(R.drawable.check_box_unselected_new);
        }
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
        return view;
    }

    class Holder {
        protected ImageView imageView1;
        protected TextView textView1;
        protected TextView reminderText;
    }
}

