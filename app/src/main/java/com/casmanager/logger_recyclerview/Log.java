package com.casmanager.logger_recyclerview;

public class Log {

    private String activity;
    private String date;
    private String length;
    private String type;

    private int id;

    public Log(int id, String activity, String date, String length, String type) {
        this.id = id;
        this.activity = activity;
        this.date = date;
        this.length = length;
        this.type = type;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLength() {
        return length;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
