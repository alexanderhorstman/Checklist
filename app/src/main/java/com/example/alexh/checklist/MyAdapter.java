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
    StringArray items;
    Context context;

    public MyAdapter(Context context, String[] values, StringArray list) {
        super(context, R.layout.row_layout_2, values);
        this.context = context;
        items = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Holder viewHolder = new Holder();
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_layout_2, null);
            viewHolder.textView1 = (TextView) view.findViewById(R.id.textView1);
            viewHolder.imageView1 = (ImageView) view.findViewById(R.id.imageView1);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (Holder) view.getTag();
        }
        String selectedItem = getItem(position);
        viewHolder.textView1.setText(selectedItem);
        if(items.checkSelectedAt(position))
        {
            viewHolder.imageView1.setImageResource(R.drawable.check_box_selected);
        }
        else
        {
            viewHolder.imageView1.setImageResource(R.drawable.check_box_unselected);
        }
        switch(items.getItem(position).priority){
            case Globals.PRIORITY_NORMAL:
                viewHolder.textView1.setTextColor(Color.BLACK);
                break;
            case Globals.PRIORITY_RUSH:
                viewHolder.textView1.setTextColor(Color.rgb(255, 141, 0));
                break;
            case Globals.PRIORITY_URGENT:
                viewHolder.textView1.setTextColor(Color.RED);
                break;
            case Globals.PRIORITY_DAILY:
                viewHolder.textView1.setTextColor(Color.BLUE);
                break;
        }



        return view;
    }

    class Holder{
        public TextView textView1;
        public ImageView imageView1;
    }
}

