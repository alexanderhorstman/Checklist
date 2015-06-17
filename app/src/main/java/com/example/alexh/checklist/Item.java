package com.example.alexh.checklist;

import java.io.Serializable;

public class Item implements Serializable {
    private String task; //description of item
    private boolean selected = false; //describes whether the item has be selected or not by the user
    private int priority = 0; //priority of the task
    private Time time;
    private Date date;

    //constructor sets the description of the item
    public Item(String task, int priority) {
        this.task = task;
        this.priority = priority;
        time = null;
        date = null;
    }

    public Item(String task, int priority, Time time, Date date) {
        this.task = task;
        this.priority = priority;
        this.time = time;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public int getPriority() {
        return priority;
    }

    public String getTask() {
        return task;
    }

    public Time getTime() {
        return time;
    }

    public boolean hasReminder() {
        if(time == null && date == null) {
            return false;
        }
        else {
            return true;
        }
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setTask(String task){
        this.task = task;
    }

    //returns the selected state
    public boolean isSelected() {
        return selected;
    }

    public boolean equals(Item item) {
        if(!item.hasReminder() && !hasReminder()) {
            if(item.task.equals(task)) {
                return true;
            }
        }
        else if((item.hasReminder() && !hasReminder()) || (!item.hasReminder() && hasReminder())) {
            return false;
        }
        else {
            if(item.task.equals(task) && time.equals(item.getTime()) && date.equals(item.getDate())) {
                return true;
            }
        }
        return false;
    }

    //changes whether the item is selected or not
    public void flipSelected() {
        if(selected) {
            selected = false;
        }
        else {
            selected = true;
        }
    }

    //returns the description of the item
    public String toString() {
        return task;
    }
}
