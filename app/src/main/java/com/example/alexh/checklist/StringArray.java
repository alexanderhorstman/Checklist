package com.example.alexh.checklist;

import java.io.Serializable;
import java.util.ArrayList;

public class StringArray implements Serializable {
    ArrayList<Item> list = new ArrayList<>();

    //constructor does nothing
    public StringArray(){}

    //constructor makes a list from an array of items
    public StringArray(Item[] array)
    {
        for(int i = 0; i < array.length; i++)
        {
            list.add(array[i]);
        }
    }

    //adds an item to the list
    public void addItem(Item item)
    {
        int pos = list.size() - 1;
        if(list.size() > 0){
            while(pos >= 0 && item.priority > list.get(pos).priority){
                pos--;
            }
            pos++;
            list.add(pos, item);
        }
        else{
            list.add(item);
        }

    }

    //deletes an item from the list
    public void deleteItem(Item item)
    {
        list.remove(list.indexOf(item));
    }

    public void deleteItem(int index)
    {
        list.remove(index);
    }

    public Item getItem(String string)
    {
        int index = -1;
        for(int count = 0; count < list.size(); count++)
        {
            if(string.equals(list.get(count).task))
            {
                index = count;
                count = list.size();
            }
        }
        return list.get(index);
    }

    public Item getItem(int position)
    {
        return list.get(position);
    }


    public boolean checkSelectedAt(int index)
    {
        return list.get(index).isSelected();
    }

    public int size()
    {
        return list.size();
    }

    //returns an array of strings from the items in the list
    public String[] getStringArray()
    {
        String[] strings = new String[list.size()];
        for(int index = 0; index < list.size(); index++)
        {
            strings[index] = list.get(index).toString();
        }
        return strings;
    }

    public void selectAll()
    {
        if(checkAllSelected())
        {
            for(int i = 0; i < list.size(); i++)
            {
                list.get(i).makeUnselected();
            }
        }
        else
        {
            for(int i = 0; i < list.size(); i++)
            {
                list.get(i).makeSelected();
            }
        }

    }

    public boolean contains(Item item)
    {
        for(int i = 0; i < list.size(); i++)
        {
            if(item.equals(getItem(i)))
            {
                return true;
            }
        }
        return false;
    }

    public boolean checkAllSelected()
    {
        for(int i = 0; i < list.size(); i++)
        {
            if(!list.get(i).isSelected())
            {
                return false;
            }
        }
        return true;
    }

    public void changeText(int position, Item item)
    {
        list.set(position, item);
    }

    public void unselect(int position)
    {
        list.get(position).makeUnselected();
    }
}
