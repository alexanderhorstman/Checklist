package com.example.alexh.checklist;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemArray implements Serializable {
    private ArrayList<Item> list = new ArrayList<>(); //the array list that holds all of the items

    //constructor does nothing
    public ItemArray(){}

    //adds an item to the list
    public void addItem(Item item) {
        int pos = list.size() - 1;
        //checks if there is anything in the list
        if(list.size() > 0) {
            //will put the new item in a position in the list based on its priority
            while(pos >= 0 && item.getPriority() > list.get(pos).getPriority()){
                pos--;
            }
            //add one because the item should be inserted above the one that it was just compared to
            pos++;
            list.add(pos, item);
        }
        else {
            list.add(item);
        }
    }

    //returns true if the item at index is checked
    public boolean checkSelectedAt(int index) {
        return list.get(index).isSelected();
    }

    //returns true if the list has the given item in it
    public boolean contains(Item item) {
        for(int i = 0; i < list.size(); i++) {
            if(item.equals(getItem(i))) {
                return true;
            }
        }
        return false;
    }

    //deletes an item from the list
    public void deleteItem(Item item) {
        list.remove(list.indexOf(item));
    }

    //deletes an item from the list
    public void deleteItem(int index) {
        list.remove(index);
    }


    //returns the item with the description matching the given string
    public Item getItem(String string) {
        int index = -1;
        for(int count = 0; count < list.size(); count++) {
            if(string.equals(list.get(count).getTask())) {
                index = count;
                count = list.size();
            }
        }
        return list.get(index);
    }

    //returns the item at the position in the list
    public Item getItem(int position) {
        return list.get(position);
    }

    //returns an array of strings from the items in the list
    public String[] getStringArray() {
        String[] strings = new String[list.size()];
        for(int index = 0; index < list.size(); index++) {
            strings[index] = list.get(index).getTask();
        }
        return strings;
    }

    //replace the item at the given position in the list with the given item
    public void replaceItem(int position, Item item) {
        if(item.getPriority() == getItem(position).getPriority()) {
            list.set(position, item);
        }
        else {
            list.remove(position);
            addItem(item);
        }
    }

    //checks all items in the list
    public void selectAll() {
        if(checkAllSelected()) {
            for(int i = 0; i < list.size(); i++) {
                list.get(i).setSelected(false);
            }
        }
        else {
            for(int i = 0; i < list.size(); i++) {
                list.get(i).setSelected(true);
            }
        }
    }

    //returns the number of items in the list
    public int sizeOf() {
        return list.size();
    }

    //deselects the item at the given position
    public void unselect(int position) {
        list.get(position).setSelected(false);
    }

    //returns true if all of the items in the list are selected
    private boolean checkAllSelected() {
        for(int i = 0; i < list.size(); i++) {
            if(!list.get(i).isSelected()) {
                return false;
            }
        }
        return true;
    }
}
