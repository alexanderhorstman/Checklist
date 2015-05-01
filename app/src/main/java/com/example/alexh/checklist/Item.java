package com.example.alexh.checklist;

import java.io.Serializable;

public class Item implements Serializable {
    private String task; //description of item
    private boolean selected = false; //describes whether the item has be selected or not by the user
    private int priority = 0; //priority of the task

    //constructor sets the description of the item
    public Item(String task, int priority) {
        this.task = task;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public String getTask() {
        return task;
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
        return item.task.equals(task);
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
        return "Task: " + task + "\nPriority: " + priority + "\nSelected: " + selected;
    }
}
