package com.example.myitschool_proj;

import java.sql.Timestamp;

public class Notification {
    private String title;
    private Timestamp time;
    private Timestamp notif_time;
    private String description;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Notification(String title, Timestamp time, Timestamp notif_time, String description, int id) {
        this.title = title;
        this.time = time;
        this.notif_time = notif_time;
        this.description = description;
        this.id = id;
    }

    public Notification(String title, Timestamp time, Timestamp notif_time, String description) {
        this.title = title;
        this.time = time;
        this.notif_time = notif_time;
        this.description = description;

    }
    @Override
    public String toString() {
        return "Notification{" +
                "title='" + title + '\'' +
                ", time=" + time +
                ", notif_time=" + notif_time +
                ", description='" + description + '\'' +
                ", id=" + id +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public Timestamp getNotif_time() {
        return notif_time;
    }

    public void setNotif_time(Timestamp notif_time) {
        this.notif_time = notif_time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
