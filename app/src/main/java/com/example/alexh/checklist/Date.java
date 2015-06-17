package com.example.alexh.checklist;

import java.io.Serializable;

public class Date implements Serializable {

    private int month;
    private int day;
    private int year;

    public Date(int month, int day, int year) {
        this.month = month;
        this.day = day;
        this.year = year;
    }

    public boolean equals(Date date) {
        if(day == date.getDay() && month == date.getMonth() && year == date.getYear()) {
            return true;
        }
        return false;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public String toString() {
        return month + "/" + day + "/" + year;
    }
}
