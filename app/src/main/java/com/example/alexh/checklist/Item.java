package com.example.alexh.checklist;

import java.io.Serializable;

public class Item implements Serializable {
    String task; //description of item
    boolean selected = false; //describes whether the item has be selected or not by the user
    int priority = 0; //priority of the task

    //constructor sets the description of the item
    public Item(String text, int priority)
    {
        task = text;
        this.priority = priority;
    }

    //changes whether the item is selected or not
    public void flipSelected()
    {
        if(selected)
        {
            selected = false;
        }
        else
        {
            selected = true;
        }
    }

    public void makeSelected()
    {
        selected = true;
    }

    public void makeUnselected()
    {
        selected = false;
    }

    //returns the selected state
    public boolean isSelected()
    {
        return selected;
    }

    //returns the description of the item
    public String toString()
    {
        return task;
    }

    public boolean equals(Item item)
    {
        if(item.task.equals(task))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void setTask(String task){
        this.task = task;
    }
}
