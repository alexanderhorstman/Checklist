package com.example.alexh.checklist;

import java.io.Serializable;

public class Time implements Serializable {

    private int hour; //on a 12 hour scale; values from 1 to 12
    private int minute; //60 minute scale; 0 to 59
    private boolean am; //true if the time is in the am
    private int hourOfDay; //24 hour scale; 0 to 23

    public Time() {
        hour = 12;
        minute = 0;
        am = true;
        hourOfDay = 0;
    }

    public Time(int hour, int minute, boolean am) {
        this.hour = hour;
        this.minute = minute;
        this.am = am;
        if(am) {
            hourOfDay = hour - 1;
        }
        else {
            hourOfDay = hour + 11;
        }
    }

    public Time(int minute, boolean am, int hourOfDay) {
        this.minute = minute;
        this.am = am;
        this.hourOfDay = hourOfDay;
        if(hourOfDay > 11) {
            hour = hourOfDay - 12;
        }
        else {
            hour = hourOfDay;
        }
        if(hour == 0) {
            hour = 12;
        }
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

    public int getHourOfDay() {
        return hourOfDay;
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
