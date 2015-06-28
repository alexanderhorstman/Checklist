package com.example.alexh.checklist;

import java.io.Serializable;

public class Item implements Serializable {
    public static final int PRIORITY_DAILY = 300;
    public static final int PRIORITY_HIGH = 900;
    public static final int PRIORITY_MEDIUM = 500;
    public static final int PRIORITY_NORMAL = 100;

    private boolean selected = false; //describes whether the item has be selected or not by the user
    private Date date; //date of the reminder if the item has one
    private int priority = 0; //priority of the task
    private String task; //description of item
    private Time time; //time of the reminder if the item has one

    //constructor sets the description of the item and priority
    //nulls out the time and date since it has no reminder
    public Item(String task, int priority) {
        this.task = task;
        this.priority = priority;
        time = null;
        date = null;
    }

    //constructor sets the description and priority of the item
    //also sets the time and date of the reminder
    public Item(String task, int priority, Time time, Date date) {
        this.task = task;
        this.priority = priority;
        this.time = time;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    //id number is used to set reminders using semi-unique id numbers
    //id numbers are created using the reminder date and time
    //could also be used to sort based on reminder time
    public int getIdNumber() {
        if(date != null && time != null) {
            return date.getYear() * 100000000 + date.getMonth() * 1000000 + date.getDay() * 1000 +
                    time.getHourOfDay() * 100 + time.getMinute();
        }
        return 0;
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

    //returns the selected state
    public boolean isSelected() {
        return selected;
    }

    //returns true if the task and reminder is the same
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

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setTask(String task){
        this.task = task;
    }

    //returns the description of the item
    public String toString() {
        return task;
    }
}
