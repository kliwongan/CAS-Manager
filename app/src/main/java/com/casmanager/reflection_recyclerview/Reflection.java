package com.casmanager.reflection_recyclerview;

public class Reflection {

    private String title;
    private String description;
    private String activity;
    private String date;

    private int id;

    public Reflection(int id, String title, String description, String activity, String date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.activity = activity;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
