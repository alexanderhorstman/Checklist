package com.example.alexh.checklist;

import java.io.Serializable;

public class Time implements Serializable {

    private int hour;
    private int minute;
    private boolean am;
    private int hourOfDay;

    public Time() {
        hour = 12;
        minute = 0;
        am = true;
        hourOfDay = 0;
    }

    public Time(int hour, int minute, boolean am, int hourOfDay) {
        this.hour = hour;
        this.minute = minute;
        this.am = am;
        this.hourOfDay = hourOfDay;
    }

    public boolean equals(Time time) {
        if(hour == time.getHour() && minute == time.getMinute() && am ^ time.isAm()) {
            return true;
        }
        else {
            return false;
        }
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public boolean isAm() {
        return am;
    }

    public String toString() {
        String returnString = "";
        returnString += hour + ":";
        if(minute < 10) {
            returnString += "0";
        }
        returnString += minute + " ";
        if(am) {
            returnString += "AM";
        }
        else {
            returnString += "PM";
        }
        return returnString;
    }
}
